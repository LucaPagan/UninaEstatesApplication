package com.dieti.dietiestates25.ui.features.manager

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Stato della UI
data class ManagerUiState(
    val notificationCount: Int = 0,
    val proposalCount: Int = 0,
    val isCapo: Boolean = false, // Flag per i permessi speciali (Crea Agente)
    val isLoading: Boolean = false
)

class ManagerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerUiState())
    val uiState: StateFlow<ManagerUiState> = _uiState.asStateFlow()

    fun loadDashboardData(idUtente: String) {
        // SIMULAZIONE CARICAMENTO DATI
        _uiState.value = _uiState.value.copy(isLoading = true)

        // Qui in futuro ci saranno le chiamate API al backend
        // ManagerApiService.getDashboardStats(idUtente)

        // Mock dei dati per vedere l'interfaccia
        val mockNotificationCount = 5
        val mockProposalCount = 12

        // Logica fittizia per determinare se è il "CAPO"
        // Esempio: Se l'ID contiene "admin" o è un ID specifico, è il capo.
        val isUserCapo = idUtente.contains("admin", ignoreCase = true) || idUtente == "capo_agenzia"

        _uiState.value = ManagerUiState(
            notificationCount = mockNotificationCount,
            proposalCount = mockProposalCount,
            isCapo = isUserCapo,
            isLoading = false
        )
    }
}