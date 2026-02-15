package com.dieti.dietiestates25.ui.features.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// DTO per la richiesta (deve coincidere con quello backend)
data class CreateSubAgentRequest(
    val nome: String,
    val cognome: String,
    val email: String,
    val password: String
)

data class ManagerUiState(
    val notificationCount: Int = 0,
    val proposalCount: Int = 0,
    val isCapo: Boolean = false,
    val isLoading: Boolean = false,

    // Stati per la creazione agente
    val isCreating: Boolean = false,
    val creationSuccess: Boolean = false,
    val creationError: String? = null
)

class ManagerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerUiState())
    val uiState: StateFlow<ManagerUiState> = _uiState.asStateFlow()

    fun loadDashboardData(idUtente: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = RetrofitClient.managerService.getDashboardStats(idUtente)

                if (response.isSuccessful) {
                    val stats = response.body()
                    _uiState.value = _uiState.value.copy(
                        notificationCount = stats?.numeroNotifiche ?: 0,
                        proposalCount = stats?.numeroProposte ?: 0,
                        isCapo = stats?.isCapo ?: false
                    )
                }
            } catch (e: Exception) {
                // Gestione errore silenziosa per la dashboard
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // NUOVO METODO: Creazione Sotto-Agente
    fun createSubAgent(nome: String, cognome: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, creationError = null, creationSuccess = false)
            try {
                val request = CreateSubAgentRequest(nome, cognome, email, password)
                // Assicurati che 'createSubAgent' sia definito in ManagerApiService (RetrofitClient)
                // Se non esiste, dovrai aggiungerlo all'interfaccia backend
                val response = RetrofitClient.managerService.createSubAgent(request)

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(creationSuccess = true)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Errore durante la creazione"
                    _uiState.value = _uiState.value.copy(creationError = errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(creationError = "Errore di connessione: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isCreating = false)
            }
        }
    }

    fun resetCreationState() {
        _uiState.value = _uiState.value.copy(creationSuccess = false, creationError = null)
    }
}