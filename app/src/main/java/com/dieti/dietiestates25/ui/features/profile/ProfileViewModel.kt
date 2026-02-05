package com.dieti.dietiestates25.ui.features.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.ProfileData
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import com.dieti.dietiestates25.data.remote.AgenteDTO
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: ProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    // Nuovo stato per segnalare l'avvenuta cancellazione
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

    companion object {
        private const val ROLE_MANAGER = "MANAGER"
    }

    fun loadUserProfile(context: Context, navUserId: String? = null) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val sessionUserId = SessionManager.getUserId(context)
            val userRole = SessionManager.getUserRole(context)

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
                // Check user role and call appropriate endpoint
                val profileData = if (userRole == ROLE_MANAGER) {
                    // For managers, use the agent profile endpoint
                    val response = profileService.getAgenteProfile(finalUserId)
                    if (response.isSuccessful && response.body() != null) {
                        val agenteDto = response.body()!!
                        mapAgenteDtoToProfileData(agenteDto)
                    } else {
                        if (response.code() == 404) {
                            SessionManager.logout(context)
                            _uiState.value = ProfileUiState.Error("Agente non trovato (404). Logout eseguito.")
                            return@launch
                        } else {
                            _uiState.value = ProfileUiState.Error("Errore API: ${response.code()}")
                            return@launch
                        }
                    }
                } else {
                    // For regular users, use the user profile endpoint
                    val response = profileService.getUserProfile(finalUserId)
                    if (response.isSuccessful && response.body() != null) {
                        val dto = response.body()!!
                        mapUtenteDtoToProfileData(dto)
                    } else {
                        if (response.code() == 404) {
                            SessionManager.logout(context)
                            _uiState.value = ProfileUiState.Error("Utente non trovato (404). Logout eseguito.")
                            return@launch
                        } else {
                            _uiState.value = ProfileUiState.Error("Errore API: ${response.code()}")
                            return@launch
                        }
                    }
                }
                
                _uiState.value = ProfileUiState.Success(profileData)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Errore connessione: ${e.message}")
            }
        }
    }

    // --- NUOVA FUNZIONE ELIMINAZIONE ---
    fun deleteProfile(context: Context) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val userId = SessionManager.getUserId(context)

            if (userId.isNullOrEmpty()) {
                _uiState.value = ProfileUiState.Error("Impossibile eliminare: ID utente mancante.")
                return@launch
            }

            try {
                Log.d("PROFILE_DEBUG", "Eliminazione utente: $userId")
                val response = profileService.deleteUser(userId)

                if (response.isSuccessful) {
                    Log.d("PROFILE_DEBUG", "Utente eliminato con successo dal DB.")
                    // Logout locale per pulire le preferenze
                    SessionManager.logout(context)
                    // Segnaliamo alla UI che è stato cancellato
                    _uiState.value = ProfileUiState.UserDeleted
                } else {
                    _uiState.value = ProfileUiState.Error("Errore eliminazione: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PROFILE_DEBUG", "Errore rete eliminazione", e)
                _uiState.value = ProfileUiState.Error("Errore rete: ${e.message}")
            }
        }
    }

    private fun mapUtenteDtoToProfileData(dto: UtenteResponseDTO): ProfileData {
        val fullPhone = dto.telefono ?: ""
        val foundPrefix = availablePrefixes.firstOrNull { fullPhone.startsWith(it.prefix) }
        val fullName = "${dto.nome} ${dto.cognome}".trim()
        val defaultPrefix = availablePrefixes.firstOrNull() ?: PhonePrefix("+39", "🇮🇹", "Italia")

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
                selectedPrefix = defaultPrefix,
                phoneNumberWithoutPrefix = fullPhone
            )
        }
    }

    private fun mapAgenteDtoToProfileData(dto: AgenteDTO): ProfileData {
        val fullName = "${dto.nome} ${dto.cognome}".trim()
        val defaultPrefix = availablePrefixes.firstOrNull() ?: PhonePrefix("+39", "🇮🇹", "Italia")
        // Managers (agents) don't have a phone number in their DTO
        // Use empty string for phone number
        return ProfileData(
            name = fullName,
            email = dto.email,
            selectedPrefix = defaultPrefix,
            phoneNumberWithoutPrefix = ""
        )
    }

    fun logout(context: Context) {
        SessionManager.logout(context)
    }
}