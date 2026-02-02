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

    // Assicurati che AuthApiService sia importato correttamente
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
                Log.d("AUTH_DEBUG", "Inizio chiamata Registrazione...")
                val response = apiService.registrazione(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!
                    Log.d("AUTH_DEBUG", "Registrazione OK. Utente: ${utente.id}")

                    salvaSessioneCompleta(utente)

                    _state.value = RegisterState.Success(utente)
                } else {
                    Log.e("AUTH_DEBUG", "Errore Registrazione API: ${response.code()}")
                    _state.value = RegisterState.Error("Errore reg: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Eccezione Registrazione", e)
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
                Log.d("AUTH_DEBUG", "Inizio chiamata Login per $email...")
                val response = apiService.login(request)
                if (response.isSuccessful && response.body() != null) {
                    val utente = response.body()!!
                    Log.d("AUTH_DEBUG", "Login OK. Utente ricevuto: ${utente.id}")

                    // Salvataggio critico
                    salvaSessioneCompleta(utente)

                    _state.value = RegisterState.Success(utente)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Errore sconosciuto"
                    Log.e("AUTH_DEBUG", "Errore Login API: $errorBody")
                    _state.value = RegisterState.Error("Login fallito: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("AUTH_DEBUG", "Eccezione Login", e)
                _state.value = RegisterState.Error("Errore connessione: ${e.message}")
            }
        }
    }

    private suspend fun salvaSessioneCompleta(utente: UtenteResponseDTO) {
        Log.d("AUTH_DEBUG", ">>> INIZIO Salvataggio Sessione Completa")

        // 1. Memoria volatile
        RetrofitClient.loggedUserEmail = utente.email
        Log.d("AUTH_DEBUG", "1. RetrofitClient email impostata: ${utente.email}")

        // 2. DataStore (Preferenze asincrone)
        try {
            userPrefs.saveUserData(utente.id, utente.email)
            Log.d("AUTH_DEBUG", "2. DataStore UserData salvato.")

            userPrefs.setFirstRunCompleted()
            Log.d("AUTH_DEBUG", "3. DataStore FirstRunCompleted impostato (Intro disattivata).")
        } catch (e: Exception) {
            Log.e("AUTH_DEBUG", "ERRORE SALVATAGGIO DATASTORE", e)
        }

        // 3. SessionManager (Preferenze sincrone per MainActivity e Profile)
        try {
            SessionManager.saveUserSession(
                getApplication(),
                utente.id,
                "${utente.nome} ${utente.cognome}"
            )
            Log.d("AUTH_DEBUG", "4. SessionManager salvato. Verifica ID: ${SessionManager.getUserId(getApplication())}")
        } catch (e: Exception) {
            Log.e("AUTH_DEBUG", "ERRORE SALVATAGGIO SESSIONMANAGER", e)
        }

        Log.d("AUTH_DEBUG", ">>> FINE Salvataggio Sessione Completa")
    }

    fun logout() {
        viewModelScope.launch {
            Log.d("AUTH_DEBUG", "Eseguo Logout...")
            userPrefs.clearUser()
            RetrofitClient.loggedUserEmail = null
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