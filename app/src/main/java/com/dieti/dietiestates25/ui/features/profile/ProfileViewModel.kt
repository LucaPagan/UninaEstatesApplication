package com.dieti.dietiestates25.ui.features.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Import fondamentale corretto
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.data.remote.NotificationPreferencesRequest
import com.dieti.dietiestates25.data.remote.ProfileData
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import com.dieti.dietiestates25.ui.utils.SessionManager
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

            val finalUserId = if (!sessionUserId.isNullOrEmpty()) {
                sessionUserId
            } else if (!navUserId.isNullOrEmpty() && navUserId != "utente" && navUserId != "{idUtente}") {
                navUserId
            } else {
                null
            }

            if (finalUserId.isNullOrEmpty()) {
                _uiState.value = ProfileUiState.Error("Sessione scaduta. Effettua il login.")
                return@launch
            }

            try {
                val response = profileService.getUserProfile(finalUserId)
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    val profileData = mapDtoToProfileData(dto)
                    _uiState.value = ProfileUiState.Success(profileData)
                } else {
                    if (response.code() == 404) {
                        SessionManager.logout(context)
                        _uiState.value = ProfileUiState.Error("Utente non trovato (404). Logout eseguito.")
                    } else {
                        _uiState.value = ProfileUiState.Error("Errore API: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Errore connessione: ${e.message}")
            }
        }
    }

    fun updateNotificationPreference(
        trattative: Boolean? = null,
        pubblicazione: Boolean? = null,
        nuoviImmobili: Boolean? = null
    ) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            val oldData = currentState.data

            // Ora questi riferimenti funzioneranno perché ProfileData è aggiornato
            val newTrattative = trattative ?: oldData.notifTrattative
            val newPubblicazione = pubblicazione ?: oldData.notifPubblicazione
            val newNuovi = nuoviImmobili ?: oldData.notifNuoviImmobili

            val newData = oldData.copy(
                notifTrattative = newTrattative,
                notifPubblicazione = newPubblicazione,
                notifNuoviImmobili = newNuovi
            )

            _uiState.value = ProfileUiState.Success(newData)

            viewModelScope.launch {
                try {
                    val request = NotificationPreferencesRequest(
                        notifTrattative = newTrattative,
                        notifPubblicazione = newPubblicazione,
                        notifNuoviImmobili = newNuovi
                    )
                    RetrofitClient.notificationService.updatePreferences(request)
                    Log.d("ProfileVM", "Preferenze notifiche aggiornate sul server")
                } catch (e: Exception) {
                    Log.e("ProfileVM", "Errore aggiornamento preferenze", e)
                }
            }
        }
    }

    fun deleteProfile(context: Context) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val userId = SessionManager.getUserId(context)

            if (userId.isNullOrEmpty()) {
                _uiState.value = ProfileUiState.Error("Impossibile eliminare: ID utente mancante.")
                return@launch
            }

            try {
                val response = profileService.deleteUser(userId)
                if (response.isSuccessful) {
                    SessionManager.logout(context)
                    _uiState.value = ProfileUiState.UserDeleted
                } else {
                    _uiState.value = ProfileUiState.Error("Errore eliminazione: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Errore rete: ${e.message}")
            }
        }
    }

    private fun mapDtoToProfileData(dto: UtenteResponseDTO): ProfileData {
        val fullPhone = dto.telefono ?: ""
        val foundPrefix = availablePrefixes.firstOrNull { fullPhone.startsWith(it.prefix) }
        val fullName = "${dto.nome} ${dto.cognome}".trim()

        return if (foundPrefix != null) {
            ProfileData(
                name = fullName,
                email = dto.email,
                selectedPrefix = foundPrefix,
                phoneNumberWithoutPrefix = fullPhone.removePrefix(foundPrefix.prefix).trim(),
                notifTrattative = dto.notifTrattative,
                notifPubblicazione = dto.notifPubblicazione,
                notifNuoviImmobili = dto.notifNuoviImmobili
            )
        } else {
            ProfileData(
                name = fullName,
                email = dto.email,
                selectedPrefix = availablePrefixes.first(),
                phoneNumberWithoutPrefix = fullPhone,
                notifTrattative = dto.notifTrattative,
                notifPubblicazione = dto.notifPubblicazione,
                notifNuoviImmobili = dto.notifNuoviImmobili
            )
        }
    }

    fun logout(context: Context) {
        SessionManager.logout(context)
    }
}