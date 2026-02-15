package com.dieti.dietiestates25.ui.features.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.data.remote.FcmTokenRequest
import com.dieti.dietiestates25.data.remote.NotificationPreferencesRequest
import com.dieti.dietiestates25.data.remote.ProfileData
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import com.dieti.dietiestates25.ui.utils.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: ProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object UserDeleted : ProfileUiState()
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val profileService: ProfileApiService by lazy {
        RetrofitClient.retrofit.create(ProfileApiService::class.java)
    }

    private val availablePrefixes = listOf(
        PhonePrefix("+39", "🇮🇹", "Italia"),
        PhonePrefix("+1", "🇺🇸", "USA"),
        PhonePrefix("+44", "🇬🇧", "UK"),
        PhonePrefix("+33", "🇫🇷", "Francia"),
        PhonePrefix("+49", "🇩🇪", "Germania")
    )

    fun loadUserProfile(context: Context, navUserId: String? = null) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val sessionUserId = SessionManager.getUserId(context)

            val finalUserId = when {
                !sessionUserId.isNullOrEmpty() -> sessionUserId
                !navUserId.isNullOrEmpty() && navUserId != "utente" && navUserId != "{idUtente}" && navUserId != "session" -> navUserId
                else -> null
            }

            if (finalUserId.isNullOrEmpty()) {
                _uiState.value = ProfileUiState.Error("Sessione scaduta. Effettua il login.")
                return@launch
            }

            try {
                val response = profileService.getUserProfile(finalUserId)
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    _uiState.value = ProfileUiState.Success(mapDtoToProfileData(dto))

                    if (finalUserId == sessionUserId) {
                        syncFcmToken()
                    }
                } else {
                    if (response.code() == 404) {
                        logout(context)
                        _uiState.value = ProfileUiState.Error("Utente non trovato.")
                    } else {
                        _uiState.value = ProfileUiState.Error("Errore API: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Errore connessione: ${e.localizedMessage}")
            }
        }
    }

    private fun syncFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let { sendTokenToBackend(it) }
            }
        }
    }

    private fun sendTokenToBackend(token: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.notificationService.updateFcmToken(FcmTokenRequest(token))
            } catch (e: Exception) {
                Log.e("ProfileVM", "Errore invio token", e)
            }
        }
    }

    fun updateNotificationPreference(t: Boolean? = null, p: Boolean? = null, n: Boolean? = null) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            val oldData = currentState.data
            val newData = oldData.copy(
                notifTrattative = t ?: oldData.notifTrattative,
                notifPubblicazione = p ?: oldData.notifPubblicazione,
                notifNuoviImmobili = n ?: oldData.notifNuoviImmobili
            )
            _uiState.value = ProfileUiState.Success(newData)

            viewModelScope.launch {
                try {
                    RetrofitClient.notificationService.updatePreferences(
                        NotificationPreferencesRequest(newData.notifTrattative, newData.notifPubblicazione, newData.notifNuoviImmobili)
                    )
                } catch (e: Exception) {
                    Log.e("ProfileVM", "Errore update preferenze", e)
                }
            }
        }
    }

    fun deleteProfile(context: Context) {
        viewModelScope.launch {
            val userId = SessionManager.getUserId(context)
            if (userId.isNullOrEmpty()) return@launch

            _uiState.value = ProfileUiState.Loading
            try {
                val response = profileService.deleteUser(userId)
                if (response.isSuccessful) {
                    SessionManager.logout(context)
                    _uiState.value = ProfileUiState.UserDeleted
                } else {
                    _uiState.value = ProfileUiState.Error("Errore eliminazione")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Errore di rete")
            }
        }
    }

    fun logout(context: Context) {
        SessionManager.logout(context)
    }

    private fun mapDtoToProfileData(dto: UtenteResponseDTO): ProfileData {
        val fullPhone = dto.telefono ?: ""
        val foundPrefix = availablePrefixes.firstOrNull { fullPhone.startsWith(it.prefix) }
        val fullName = "${dto.nome} ${dto.cognome}".trim()

        return ProfileData(
            name = fullName,
            email = dto.email ?: "",
            selectedPrefix = foundPrefix ?: availablePrefixes.first(),
            phoneNumberWithoutPrefix = if (foundPrefix != null) fullPhone.removePrefix(foundPrefix.prefix).trim() else fullPhone,
            notifTrattative = dto.notifTrattative,
            notifPubblicazione = dto.notifPubblicazione,
            notifNuoviImmobili = dto.notifNuoviImmobili
        )
    }
}