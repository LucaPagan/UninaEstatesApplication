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
import java.time.LocalDate

class AppointmentBookingViewModel : ViewModel() {

    private val _bookingState = MutableStateFlow<String>("")
    val bookingState = _bookingState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun bookAppointment(
        utenteId: String,
        immobileId: String,
        date: LocalDate,
        timeSlotIndex: Int?, // Riceve l'indice dello slot selezionato
        context: Context
    ) {
        if (timeSlotIndex == null) {
            Toast.makeText(context, "Seleziona un orario", Toast.LENGTH_SHORT).show()
            return
        }

        // Mappa l'indice dello slot a un orario specifico (es. inizio dello slot)
        // TimeSlots = listOf("9-12", "12-14", "14-17", "17-20")
        val timeSlotsStartHours = listOf("09:00", "12:00", "14:00", "17:00")
        val selectedTime = timeSlotsStartHours.getOrElse(timeSlotIndex) { "09:00" }

        // Formatta la data come stringa YYYY-MM-DD
        val dateString = date.toString()

        viewModelScope.launch {
            _isLoading.value = true
            _bookingState.value = "Caricamento..."
            try {
                // Aggiungiamo i secondi all'orario se il backend usa LocalTime (HH:mm:ss)
                // Usiamo un formato standard HH:mm:ss per sicurezza
                val formattedTime = if (selectedTime.length == 5) "$selectedTime:00" else selectedTime

                // NOTA: 'agenteId' è richiesto dal backend DTO (vedi GlobalDTO.kt fornito in precedenza).
                // Se il backend non assegna automaticamente, bisogna passare un ID valido o gestire lato server.
                // Qui assumiamo che il backend possa gestire un agenteId vuoto o che vada passato.
                // Se fallisce, potrebbe essere necessario recuperare l'agenteId dall'immobile prima.
                val req = AppuntamentoRequest(
                    utenteId = utenteId,
                    immobileId = immobileId,
                    agenteId = "00000000-0000-0000-0000-000000000000", // UUID Placeholder/Null se il backend lo accetta o lo assegna
                    data = dateString,
                    orario = formattedTime
                )

                val response = RetrofitClient.instance.createAppointment(req)
                if (response.isSuccessful) {
                    _bookingState.value = "Successo"
                    Toast.makeText(context, "Richiesta inviata!", Toast.LENGTH_SHORT).show()
                } else {
                    _bookingState.value = "Errore: ${response.code()}"
                    Toast.makeText(context, "Errore prenotazione: ${response.code()}", Toast.LENGTH_SHORT).show()
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