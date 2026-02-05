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

    // Registrazione Utente (Invariata)
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

    // Login Unificato (Prova Utente -> poi Admin)
    fun eseguiLogin(email: String, pass: String, ricordami: Boolean) {
        if (email.isBlank() || pass.isBlank()) {
            _state.value = RegisterState.Error("Inserisci email e password")
            return
        }
        _state.value = RegisterState.Loading

        val request = LoginRequest(email, pass)

        viewModelScope.launch {
            // STEP 1: Tentativo Login Utente Standard
            var loginUtenteSuccess = false
            try {
                Log.d("AUTH_DEBUG", "Tentativo Login UTENTE per $email...")
                val responseUser = apiService.login(request)

                if (responseUser.isSuccessful && responseUser.body() != null) {
                    val utente = responseUser.body()!!
                    Log.d("AUTH_DEBUG", "Login UTENTE riuscito: ${utente.id}")
                    salvaSessioneCompleta(utente)
                    _state.value = RegisterState.Success(utente)
                    loginUtenteSuccess = true
                } else {
                    Log.d("AUTH_DEBUG", "Login UTENTE fallito (${responseUser.code()}).")
                }
            } catch (e: Exception) {
                Log.w("AUTH_DEBUG", "Errore tecnico Login Utente (proseguo con Admin): ${e.message}")
            }

            // STEP 2: Se fallisce utente, prova Login Admin
            if (!loginUtenteSuccess) {
                Log.d("AUTH_DEBUG", "Tentativo Login ADMIN...")
                try {
                    val responseAdmin = apiService.loginAdmin(request)

                    if (responseAdmin.isSuccessful && responseAdmin.body() != null) {
                        val admin = responseAdmin.body()!!
                        Log.d("AUTH_DEBUG", "Login ADMIN riuscito: ${admin.id}")

                        val adminUserMock = UtenteResponseDTO(
                            id = "ADMIN_SESSION",
                            nome = "Amministratore",
                            cognome = "Sistema",
                            email = admin.email,
                            telefono = null,
                            preferiti = emptyList()
                        )

                        salvaSessioneCompleta(adminUserMock)
                        _state.value = RegisterState.Success(adminUserMock)
                    } else {
                        Log.e("AUTH_DEBUG", "Login ADMIN fallito: ${responseAdmin.code()}")
                        _state.value = RegisterState.Error("Credenziali non valide")
                    }
                } catch (e: Exception) {
                    Log.e("AUTH_DEBUG", "Errore Login Admin", e)
                    _state.value = RegisterState.Error("Errore Login: ${e.message}")
                }
            }
        }
    }

    private suspend fun salvaSessioneCompleta(utente: UtenteResponseDTO) {
        RetrofitClient.loggedUserEmail = utente.email

        try {
            userPrefs.saveUserData(utente.id, utente.email)
            userPrefs.setFirstRunCompleted()
        } catch (e: Exception) {
            Log.e("AUTH_DEBUG", "ERRORE SALVATAGGIO DATASTORE", e)
        }

        try {
            SessionManager.saveUserSession(
                getApplication(),
                utente.id,
                "${utente.nome} ${utente.cognome}"
            )
        } catch (e: Exception) {
            Log.e("AUTH_DEBUG", "ERRORE SALVATAGGIO SESSIONMANAGER", e)
        }
    }

    fun logout() {
        viewModelScope.launch {
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