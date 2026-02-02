package com.dieti.dietiestates25.ui.features.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.ProfileData
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import com.dieti.dietiestates25.ui.features.profile.ProfileApiService
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: ProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
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

    // FIX: Aggiunto il parametro navUserId per compatibilità con ProfileScreen
    fun loadUserProfile(context: Context, navUserId: String? = null) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val sessionUserId = SessionManager.getUserId(context)

            // Log per debuggare la situazione "Zombie"
            Log.e("PROFILE_DEBUG", "------------------------------------------------")
            Log.e("PROFILE_DEBUG", "ID Sessione: '$sessionUserId'")
            Log.e("PROFILE_DEBUG", "ID Navigazione: '$navUserId'")

            // 1. Logica di selezione ID
            // Diamo priorità alla Sessione. Se è null, controlliamo la Navigazione.
            // (Utile se la sessione si perde ma la navigazione ha ancora l'ID in memoria)
            val finalUserId = if (!sessionUserId.isNullOrEmpty()) {
                sessionUserId
            } else if (!navUserId.isNullOrEmpty() && navUserId != "utente" && navUserId != "{idUtente}") {
                Log.w("PROFILE_DEBUG", "Sessione vuota! Tento fallback su NavID: $navUserId")
                navUserId
            } else {
                null
            }

            if (finalUserId.isNullOrEmpty()) {
                Log.e("PROFILE_DEBUG", "ERRORE: Nessun ID valido trovato.")
                _uiState.value = ProfileUiState.Error("Sessione scaduta o ID mancante.\nEffettua nuovamente il login.")
                return@launch
            }

            try {
                Log.d("PROFILE_DEBUG", "Chiamata API a: api/utenti/$finalUserId")

                val response = profileService.getUserProfile(finalUserId)

                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    Log.d("PROFILE_DEBUG", "Successo! DTO ricevuto.")

                    val profileData = mapDtoToProfileData(dto)
                    _uiState.value = ProfileUiState.Success(profileData)
                } else {
                    Log.e("PROFILE_DEBUG", "Errore API. Codice: ${response.code()}")
                    if (response.code() == 404) {
                        // Messaggio specifico per aiutare a capire il problema dei dati disallineati
                        _uiState.value = ProfileUiState.Error("Errore 404: Utente non trovato sul server.\n(ID salvato non valido: $finalUserId)")
                    } else {
                        _uiState.value = ProfileUiState.Error("Errore API: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("PROFILE_DEBUG", "Eccezione di rete", e)
                _uiState.value = ProfileUiState.Error("Errore connessione: ${e.message}")
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
                phoneNumberWithoutPrefix = fullPhone.removePrefix(foundPrefix.prefix).trim()
            )
        } else {
            ProfileData(
                name = fullName,
                email = dto.email,
                selectedPrefix = availablePrefixes.first(),
                phoneNumberWithoutPrefix = fullPhone
            )
        }
    }
}