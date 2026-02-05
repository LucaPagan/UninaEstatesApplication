package com.dieti.dietiestates25.ui.features.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.LoginRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteRegistrazioneRequest
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// --- REGISTER STATE ---
sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val utente: UtenteResponseDTO) : RegisterState()
    data class Error(val errore: String) : RegisterState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application.applicationContext)
    private val apiService = RetrofitClient.retrofit.create(AuthApiService::class.java)

    private val _state = MutableLiveData<RegisterState>(RegisterState.Idle)
    val state: LiveData<RegisterState> = _state

    init {
        viewModelScope.launch {
            val savedEmail = userPrefs.userEmail.first()
            if (savedEmail != null) {
                RetrofitClient.loggedUserEmail = savedEmail
            }
        }
    }

    fun eseguiRegistrazione(nome: String, cognome: String, email: String, pass: String, telefono: String?) {
        if (nome.isBlank() || email.isBlank() || pass.isBlank()) {
            _state.value = RegisterState.Error("Compila tutti i campi obbligatori")
            return
        }
        _state.value = RegisterState.Loading

        val request = UtenteRegistrazioneRequest(nome, cognome, email, pass, telefono)

        viewModelScope.launch {
            try {
                Log.d("AUTH_DEBUG", "Inizio Registrazione...")
                val response = apiService.registrazione(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!

                    // FIX LOOP: Salviamo sessione e flag Primo Avvio
                    salvaSessioneCompleta(utente)

                    _state.value = RegisterState.Success(utente)
                } else {
                    _state.value = RegisterState.Error("Errore reg: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error("Errore rete: ${e.message}")
            }
        }
    }

    fun eseguiLogin(email: String, pass: String, ricordami: Boolean) {
        if (email.isBlank() || pass.isBlank()) {
            _state.value = RegisterState.Error("Inserisci email e password")
            return
        }
        _state.value = RegisterState.Loading

        val request = LoginRequest(email, pass)

        viewModelScope.launch {
            try {
                Log.d("AUTH_DEBUG", "Inizio Login...")
                val response = apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!

                    // FIX LOOP: Salviamo sessione e flag Primo Avvio
                    salvaSessioneCompleta(utente)

                    _state.value = RegisterState.Success(utente)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Errore sconosciuto"
                    _state.value = RegisterState.Error("Login fallito: $errorBody")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error("Errore connessione: ${e.message}")
            }
        }
    }

    // --- FUNZIONE HELPER AGGIUNTA (CRITICA PER IL FIX) ---
    private suspend fun salvaSessioneCompleta(utente: UtenteResponseDTO) {
        RetrofitClient.loggedUserEmail = utente.email
        userPrefs.saveUserData(utente.id, utente.email)
        userPrefs.setFirstRunCompleted()

        // Passiamo il ruolo ricevuto dal DTO (sarà "UTENTE" o "MANAGER")
        SessionManager.saveUserSession(
            getApplication(),
            utente.id,
            "${utente.nome} ${utente.cognome}",
            utente.ruolo
        )

        Log.d("AUTH_DEBUG", "Sessione salvata. Ruolo: ${utente.ruolo}")
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearUser()
            RetrofitClient.loggedUserEmail = null
            // Pulizia completa
            SessionManager.logout(getApplication())
        }
    }

    fun completaWelcomeScreen() {
        viewModelScope.launch {
            userPrefs.setFirstRunCompleted()
        }
    }

    val userLoggedFlow = userPrefs.userId
}