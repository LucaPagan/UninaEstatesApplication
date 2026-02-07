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
            if (savedEmail != null) RetrofitClient.loggedUserEmail = savedEmail
        }
    }

    // REGISTRAZIONE: Aggiunto parametro rememberMe
    fun eseguiRegistrazione(nome: String, cognome: String, email: String, pass: String, telefono: String?, rememberMe: Boolean) {
        if (nome.isBlank() || email.isBlank() || pass.isBlank()) {
            _state.value = RegisterState.Error("Compila tutti i campi obbligatori")
            return
        }
        _state.value = RegisterState.Loading

        val request = UtenteRegistrazioneRequest(nome, cognome, email, pass, telefono)

        viewModelScope.launch {
            try {
                val response = apiService.registrazione(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!
                    // Salvataggio con preferenza rememberMe
                    salvaSessioneCompleta(utente, rememberMe)
                    _state.value = RegisterState.Success(utente)
                } else {
                    _state.value = RegisterState.Error("Errore reg: ${response.code()}")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error("Errore rete: ${e.message}")
            }
        }
    }

    // LOGIN: Il parametro rememberMe era già presente, ora lo usiamo
    fun eseguiLogin(email: String, pass: String, rememberMe: Boolean) {
        if (email.isBlank() || pass.isBlank()) {
            _state.value = RegisterState.Error("Inserisci email e password")
            return
        }
        _state.value = RegisterState.Loading

        val request = LoginRequest(email, pass)

        viewModelScope.launch {
            try {
                val response = apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!
                    Log.d("AUTH_DEBUG", "Login Successo. ID: ${utente.id}, RememberMe: $rememberMe")

                    // Passiamo rememberMe alla funzione di salvataggio
                    salvaSessioneCompleta(utente, rememberMe)

                    _state.value = RegisterState.Success(utente)
                } else {
                    val errMsg = response.errorBody()?.string() ?: "Errore ${response.code()}"
                    _state.value = RegisterState.Error("Credenziali non valide")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error("Errore connessione: ${e.message}")
            }
        }
    }

    // Aggiunto parametro rememberMe
    private suspend fun salvaSessioneCompleta(utente: UtenteResponseDTO, rememberMe: Boolean) {
        RetrofitClient.loggedUserEmail = utente.email
        try {
            // UserPrefs (DataStore) serve per l'email rapida al login (autocomplete)
            // Se rememberMe è false, magari non vogliamo salvare l'email per i suggerimenti futuri?
            // Per ora lo lasciamo per comodità, ma il SessionManager gestisce l'accesso.
            userPrefs.saveUserData(utente.id, utente.email)
            userPrefs.setFirstRunCompleted()

            // Salviamo nel SessionManager con la logica di scadenza
            SessionManager.saveUserSession(
                getApplication(),
                utente.id,
                "${utente.nome} ${utente.cognome}",
                utente.ruolo ?: "UTENTE",
                rememberMe // PASSAGGIO FONDAMENTALE
            )
        } catch (e: Exception) {
            Log.e("AUTH_DEBUG", "Errore salvataggio sessione", e)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearUser()
            RetrofitClient.loggedUserEmail = null
            SessionManager.logout(getApplication())
        }
    }

    fun completaWelcomeScreen() { viewModelScope.launch { userPrefs.setFirstRunCompleted() } }
}