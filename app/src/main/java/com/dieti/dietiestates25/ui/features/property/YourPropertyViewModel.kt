package com.dieti.dietiestates25.ui.features.property

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Stato specifico per questa schermata
sealed class YourPropertyState {
    object Idle : YourPropertyState()
    object Loading : YourPropertyState()
    data class Success(val immobili: List<ImmobileDTO>) : YourPropertyState()
    data class Error(val message: String) : YourPropertyState()
}

class YourPropertyViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.retrofit.create(PropertyApiService::class.java)

    private val _uiState = MutableStateFlow<YourPropertyState>(YourPropertyState.Idle)
    val uiState: StateFlow<YourPropertyState> = _uiState.asStateFlow()

    fun loadMyProperties() {
        _uiState.value = YourPropertyState.Loading

        // Recuperiamo l'ID del manager loggato
        val userId = SessionManager.getUserId(getApplication())

        if (userId == null) {
            _uiState.value = YourPropertyState.Error("Utente non identificato. Effettua il login.")
            return
        }

        viewModelScope.launch {
            try {
                val response = api.getImmobiliByAgente(userId)
                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!
                    _uiState.value = YourPropertyState.Success(lista)
                } else {
                    _uiState.value = YourPropertyState.Error("Nessun immobile trovato o errore server.")
                }
            } catch (e: Exception) {
                _uiState.value = YourPropertyState.Error("Errore di connessione: ${e.message}")
            }
        }
    }
}