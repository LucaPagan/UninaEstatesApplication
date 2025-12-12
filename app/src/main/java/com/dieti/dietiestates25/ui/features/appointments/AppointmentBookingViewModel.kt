package com.dieti.dietiestates25.ui.features.appointments

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.AppuntamentoRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentBookingViewModel : ViewModel() {

    private val _bookingState = MutableStateFlow<String>("")
    val bookingState = _bookingState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun bookAppointment(
        utenteId: String,
        immobileId: String,
        date: String, // "YYYY-MM-DD"
        time: String, // "HH:mm"
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _bookingState.value = "Caricamento..."
            try {
                // Aggiungiamo i secondi all'orario se il backend usa LocalTime (HH:mm:ss)
                val formattedTime = if (time.length == 5) "$time:00" else time
                
                val req = AppuntamentoRequest(
                    utenteId = utenteId,
                    immobileId = immobileId,
                    agenteId = "", // Il backend dovrebbe gestire l'assegnazione
                    data = date,
                    orario = formattedTime
                )
                
                val response = RetrofitClient.instance.createAppointment(req)
                if (response.isSuccessful) {
                   _bookingState.value = "Successo"
                   Toast.makeText(context, "Richiesta inviata!", Toast.LENGTH_SHORT).show()
                } else {
                   _bookingState.value = "Errore: ${response.code()}"
                   Toast.makeText(context, "Errore: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                _bookingState.value = "Errore: ${e.message}"
                Toast.makeText(context, "Errore di rete: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetState() {
        _bookingState.value = ""
    }
}