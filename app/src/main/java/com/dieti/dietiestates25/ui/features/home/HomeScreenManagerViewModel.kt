package com.dieti.dietiestates25.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Eventuali dati specifici per la dashboard del manager
data class ManagerDashboardData(
    val pendingRequestsCount: Int,
    val activeListingsCount: Int
)

class HomeScreenManagerViewModel : ViewModel() {
    
    private val _dashboardData = MutableStateFlow<ManagerDashboardData?>(null)
    val dashboardData = _dashboardData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulazione caricamento dati o chiamata API dedicata:
                // val data = RetrofitClient.instance.getManagerDashboard()
                
                // Mock dati per ora
                kotlinx.coroutines.delay(500)
                _dashboardData.value = ManagerDashboardData(
                    pendingRequestsCount = 5,
                    activeListingsCount = 12
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}