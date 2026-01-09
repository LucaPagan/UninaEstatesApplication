package com.tuonome.immobiliare.ui.register // Metti il tuo package

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.LoginRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UtenteRegistrazioneRequest
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    // MODIFICA QUI: Ora passiamo l'intero utente, non solo una stringa
    data class Success(val utente: UtenteResponseDTO) : RegisterState()
    data class Error(val errore: String) : RegisterState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefs = UserPreferences(application.applicationContext)
    // 1. Inizializzo il servizio API (meglio se fatto tramite Dependency Injection, ma ok così per ora)
    private val apiService = RetrofitClient.retrofit.create(AuthApiService::class.java)
    // 2. LiveData per gestire lo stato della UI
    // _state è privato e modificabile solo qui dentro
    private val _state = MutableLiveData<RegisterState>(RegisterState.Idle)
    // state è pubblico e immutabile, l'Activity osserva questo
    val state: LiveData<RegisterState> = _state

    // 3. La funzione chiamata dal bottone "Registrati"
    fun eseguiRegistrazione(nome: String, cognome: String, email: String, pass: String, telefono: String?) {

        // Controllo validazione base (opzionale, ma consigliato)
        if (nome.isBlank() || email.isBlank() || pass.isBlank()) {
            _state.value = RegisterState.Error("Compila tutti i campi obbligatori")
            return
        }

        // Imposto lo stato su LOADING (l'Activity mostrerà la rotellina)
        _state.value = RegisterState.Loading

        // Preparo il DTO
        val request = UtenteRegistrazioneRequest(
            nome = nome,
            cognome = cognome,
            email = email,
            password = pass,
            telefono = telefono
        )

        // Lancio la Coroutine (operazione asincrona)
        viewModelScope.launch {
            try {
                // Chiamata di rete effettiva
                val response = apiService.registrazione(request)

                if (response.isSuccessful) {
                    val nuovoUtente = response.body()
                    if (nuovoUtente != null) {
                        // Passiamo l'oggetto utente allo stato Success
                        _state.value = RegisterState.Success(nuovoUtente)
                    } else {
                        _state.value = RegisterState.Error("Risposta vuota dal server")
                    }
                }

            } catch (e: IOException) {
                // Errore di rete (No internet, Server down)
                _state.value = RegisterState.Error("Nessuna connessione internet")
            } catch (e: HttpException) {
                // Errore HTTP non gestito
                _state.value = RegisterState.Error("Errore del server: ${e.message}")
            } catch (e: Exception) {
                // Qualsiasi altro crash
                _state.value = RegisterState.Error("Errore generico: ${e.localizedMessage}")
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
                // Chiamata all'endpoint di login
                val response = apiService.login(request)
                println("LOGIN DEBUG: Codice risposta = ${response.code()}")

                if (response.isSuccessful) {
                    val utente = response.body()
                    if (utente != null) {
                        if (ricordami) {
                            userPrefs.saveUserId(utente.id)
                        }

                        _state.value = RegisterState.Success(utente)
                    } else {
                        _state.value = RegisterState.Error("Login fallito: risposta vuota")
                    }
                } else {
                    val errorString = response.errorBody()?.string()
                    println("LOGIN DEBUG: Errore Server = $errorString")

                    _state.value = RegisterState.Error("Login fallito: $errorString")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error("Errore di connessione: ${e.message}")
            }
        }
    }
    // NUOVO: Funzione per il Logout
    fun logout() {
        viewModelScope.launch {
            userPrefs.clearUser()
        }
    }

    fun completaWelcomeScreen() {
        viewModelScope.launch {
            userPrefs.setFirstRunCompleted()
        }
    }

    // NUOVO: Espone il flusso dell'utente salvato (per la MainActivity)
    val userLoggedFlow = userPrefs.userId
}