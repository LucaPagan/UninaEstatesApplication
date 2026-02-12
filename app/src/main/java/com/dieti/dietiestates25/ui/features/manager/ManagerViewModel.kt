package com.dieti.dietiestates25.ui.features.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ManagerUiState(
    val notificationCount: Int = 0,
    val proposalCount: Int = 0,
    val isCapo: Boolean = false,
    val isLoading: Boolean = false
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
                        // Logica isCapo simulata o da prendere dal profilo utente
                        isCapo = idUtente.contains("admin") || idUtente == "capo_agenzia"
                    )
                }
            } catch (e: Exception) {
                // Gestione errore silenziosa per la dashboard, mantiene i valori a 0
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}