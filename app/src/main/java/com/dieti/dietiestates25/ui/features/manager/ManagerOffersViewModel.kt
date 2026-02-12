package com.dieti.dietiestates25.ui.features.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.TrattativaSummaryDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManagerOffersViewModel : ViewModel() {
    // Usiamo il DTO unificato delle trattative
    private val _offerte = MutableStateFlow<List<TrattativaSummaryDTO>>(emptyList())
    val offerte = _offerte.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadOfferte(agenteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Chiama il nuovo endpoint che restituisce storico completo
                val response = RetrofitClient.managerService.getTrattativeManager(agenteId)
                if (response.isSuccessful) {
                    _offerte.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}