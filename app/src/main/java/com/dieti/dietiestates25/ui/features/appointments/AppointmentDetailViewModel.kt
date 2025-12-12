package com.dieti.dietiestates25.ui.features.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.AppuntamentoDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentDetailViewModel : ViewModel() {

    private val _appointment = MutableStateFlow<AppuntamentoDTO?>(null)
    val appointment = _appointment.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchAppointmentDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Endpoint GET: api/appuntamenti/{id}
                val response = RetrofitClient.instance.getAppointment(id)
                if (response.isSuccessful && response.body() != null) {
                    _appointment.value = response.body()
                } else {
                    _error.value = "Errore nel caricamento: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Errore di rete: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funzione helper opzionale per la Preview (se volessi caricare dati finti)
    fun loadAppointmentDetail(id: String) {
        fetchAppointmentDetail(id)
    }
}