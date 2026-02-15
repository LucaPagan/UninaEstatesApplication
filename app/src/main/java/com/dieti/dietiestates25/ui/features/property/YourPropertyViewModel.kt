package com.dieti.dietiestates25.ui.features.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Stato specifico per questa schermata
sealed class YourPropertyState {
    object Idle : YourPropertyState()
    object Loading : YourPropertyState()
    data class Success(val immobili: List<ImmobileDTO>) : YourPropertyState()
    data class Error(val message: String) : YourPropertyState()
}

class YourPropertyViewModel : ViewModel() {
    private val api = RetrofitClient.retrofit.create(PropertyApiService::class.java)
    private val _uiState = MutableStateFlow<YourPropertyState>(YourPropertyState.Idle)
    val uiState = _uiState.asStateFlow()

    fun loadProperties(context: android.content.Context) {
        _uiState.value = YourPropertyState.Loading

        val userId = SessionManager.getUserId(context)
        val role = SessionManager.getUserRole(context)

        viewModelScope.launch {
            try {
                if (userId == null && role != "ADMIN") {
                    _uiState.value = YourPropertyState.Error("Sessione scaduta. Effettua nuovamente il login.")
                    return@launch
                }

                // Se l'utente è ADMIN, chiamiamo l'endpoint generico getImmobili
                // che, senza parametri di ricerca, restituisce tutto il catalogo.
                val response = if (role == "ADMIN") {
                    api.getAllImmobili()
                } else {
                    // Se è un Agente/Manager, carichiamo solo i suoi
                    if (userId == null) throw Exception("ID Utente non trovato in sessione")
                    api.getImmobiliByAgente(userId)
                }

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = YourPropertyState.Success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Errore server: ${response.code()}"
                    _uiState.value = YourPropertyState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = YourPropertyState.Error("Errore di connessione: ${e.message}")
            }
        }
    }
}