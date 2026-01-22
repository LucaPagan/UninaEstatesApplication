package com.dieti.dietiestates25.ui.features.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.ProfileData
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Stati della UI
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: ProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Creiamo il servizio API specifico per questa feature
    // Assumiamo che RetrofitClient abbia un metodo getClient() o esponga la proprietà retrofit
    private val profileService: ProfileApiService by lazy {
        RetrofitClient.retrofit.create(ProfileApiService::class.java)
    }

    // Lista prefissi per la logica di visualizzazione
    private val availablePrefixes = listOf(
        PhonePrefix("+39", "🇮🇹", "Italia"),
        PhonePrefix("+1", "🇺🇸", "USA"),
        PhonePrefix("+44", "🇬🇧", "UK"),
        PhonePrefix("+33", "🇫🇷", "Francia"),
        PhonePrefix("+49", "🇩🇪", "Germania")
    )

    fun loadUserProfile(context: Context) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val userId = SessionManager.getUserId(context)

            if (userId.isNullOrEmpty()) {
                _uiState.value = ProfileUiState.Error("Sessione scaduta o ID utente mancante.")
                return@launch
            }

            try {
                // Chiamata API usando il servizio specifico del profilo
                val response = profileService.getUserProfile(userId)

                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    val profileData = mapDtoToProfileData(dto)
                    _uiState.value = ProfileUiState.Success(profileData)
                } else {
                    _uiState.value = ProfileUiState.Error("Errore API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching profile", e)
                _uiState.value = ProfileUiState.Error("Errore di connessione: ${e.message}")
            }
        }
    }

    private fun mapDtoToProfileData(dto: UtenteResponseDTO): ProfileData {
        val fullPhone = dto.telefono ?: ""

        // Logica per separare prefisso e numero
        val foundPrefix = availablePrefixes.firstOrNull { fullPhone.startsWith(it.prefix) }

        // Uniamo Nome e Cognome dal DTO per formare il campo 'name' del Model
        val fullName = "${dto.nome} ${dto.cognome}".trim()

        return if (foundPrefix != null) {
            ProfileData(
                name = fullName, // Importante: Mappiamo esplicitamente il nome!
                email = dto.email,
                selectedPrefix = foundPrefix,
                phoneNumberWithoutPrefix = fullPhone.removePrefix(foundPrefix.prefix).trim()
            )
        } else {
            ProfileData(
                name = fullName, // Importante: Mappiamo esplicitamente il nome!
                email = dto.email,
                selectedPrefix = availablePrefixes.first(), // Default Italia se non riconosciuto
                phoneNumberWithoutPrefix = fullPhone
            )
        }
    }

    fun logout(context: Context) {
        SessionManager.logout(context)
    }
}