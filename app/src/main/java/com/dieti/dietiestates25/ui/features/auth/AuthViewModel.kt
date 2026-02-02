package com.dieti.dietiestates25.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.LoginRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteRegistrazioneRequest
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
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

    // All'avvio, ripristiniamo l'email se esiste
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
                val response = apiService.registrazione(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!

                    // Salviamo SEMPRE i dati per evitare problemi di sessione persa
                    userPrefs.saveUserData(utente.id, utente.email)
                    RetrofitClient.loggedUserEmail = utente.email

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
                val response = apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!

                    // Impostiamo l'email in memoria per le chiamate immediate
                    RetrofitClient.loggedUserEmail = utente.email

                    // FIX: Salviamo SEMPRE su disco, indipendentemente da 'ricordami'.
                    // 'ricordami' potrebbe essere usato in futuro per l'auto-login all'avvio,
                    // ma la sessione corrente deve essere persistente ai crash/riavvii.
                    userPrefs.saveUserData(utente.id, utente.email)

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

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearUser()
            RetrofitClient.loggedUserEmail = null
        }
    }

    fun completaWelcomeScreen() {
        viewModelScope.launch {
            userPrefs.setFirstRunCompleted()
        }
    }

    val userLoggedFlow = userPrefs.userId
}