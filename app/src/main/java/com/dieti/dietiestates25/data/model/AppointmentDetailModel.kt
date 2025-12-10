package com.dieti.dietiestates25.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.util.Log

data class AppointmentDetail(
    val id: String,
    val title: String,
    val date: LocalDate,
    val timeSlot: String,
    val address: String,
    val propertyImageUrl: Int?,
    val description: String?,
    val participants: List<String>?,
    val iconType: AppointmentIconType = AppointmentIconType.VISIT,
    val notes: String? = null
)

class AppointmentDetailViewModel : ViewModel() {

    private val _currentAppointment = MutableStateFlow<AppointmentDetail?>(null)
    val currentAppointment: StateFlow<AppointmentDetail?> = _currentAppointment.asStateFlow()

    // Evento per segnalare alla UI di navigare indietro dopo la cancellazione
    private val _appointmentCancelledEvent = MutableStateFlow(false)
    val appointmentCancelledEvent: StateFlow<Boolean> = _appointmentCancelledEvent.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN)
    val timeSlotOptions = listOf("9-12", "12-14", "14-17", "17-20")

    fun loadAppointmentDetail(appointmentId: String?) {
        viewModelScope.launch {
            if (appointmentId == null) {
                _currentAppointment.value = null
                return@launch
            }
            // SIMULAZIONE CARICAMENTO DATI (come prima)
            _currentAppointment.value = when (appointmentId) {
                "apt1" -> AppointmentDetail(
                    id = "apt1",
                    title = "Visita Appartamento Via Roma",
                    date = LocalDate.now().plusDays(2),
                    timeSlot = timeSlotOptions[0],
                    address = "Via Roma, 123, Napoli",
                    propertyImageUrl = R.drawable.property1,
                    description = "Visione dell'appartamento trilocale con il Sig. Rossi. Portare planimetrie.",
                    participants = listOf("Mario Rossi (Cliente)", "Luigi Verdi (Agente)"),
                    iconType = AppointmentIconType.VISIT,
                    notes = "Il cliente è particolarmente interessato alla luminosità e al balcone."
                )
                "apt2" -> AppointmentDetail(
                    id = "apt2",
                    title = "Meeting Agenzia",
                    date = LocalDate.now().plusDays(1),
                    timeSlot = timeSlotOptions[2],
                    address = "Sede Agenzia DietiEstates, Via Toledo 45, Napoli",
                    propertyImageUrl = null,
                    description = "Riunione interna per discutere le nuove strategie di marketing per il trimestre.",
                    participants = listOf("Team Marketing", "Direzione Vendite"),
                    iconType = AppointmentIconType.MEETING
                )
                else -> AppointmentDetail(
                    id = appointmentId,
                    title = "Appuntamento Generico",
                    date = LocalDate.now().plusDays(5),
                    timeSlot = timeSlotOptions.first(),
                    address = "Indirizzo non specificato",
                    propertyImageUrl = R.drawable.property2,
                    description = "Dettagli non disponibili per questo appuntamento.",
                    participants = null,
                    iconType = AppointmentIconType.GENERIC
                )
            }
        }
    }

    fun getFormattedDate(appointmentDetail: AppointmentDetail?): String {
        return appointmentDetail?.date?.format(dateFormatter)?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ITALIAN) else it.toString()
        } ?: "Data non disponibile"
    }

    fun getFormattedTime(appointmentDetail: AppointmentDetail?): String {
        return appointmentDetail?.timeSlot ?: "Orario non specificato"
    }

    fun getTimeSlotIndex(timeSlotString: String?): Int? {
        return timeSlotString?.let { timeSlotOptions.indexOf(it).takeIf { index -> index != -1 } }
    }

    // rescheduleAppointmentAction RIMOSSA perché non utilizzata

    fun cancelAppointment() {
        _currentAppointment.value?.let { currentApt ->
            Log.d("AppointmentDetailVM", "Cancellazione appuntamento: ${currentApt.title}, ID: ${currentApt.id}")
            // In un'app reale:
            // 1. Chiamare il repository per cancellare l'appuntamento dal backend/database.
            // 2. Se l'operazione ha successo:
            _currentAppointment.value = null // Rimuovi dalla visualizzazione corrente
            _appointmentCancelledEvent.value = true // Emetti evento per navigare indietro
            // Se l'operazione fallisce, gestire l'errore (es. mostrare un messaggio)
        }
    }

    // Funzione per resettare l'evento dopo che è stato gestito dalla UI
    fun onCancellationEventConsumed() {
        _appointmentCancelledEvent.value = false
    }

    fun confirmReschedule(newDate: LocalDate, timeSlotIndex: Int) {
        val newTimeSlotString = timeSlotOptions.getOrElse(timeSlotIndex) { "Orario non valido" }
        _currentAppointment.value?.let { currentApt ->
            Log.i("AppointmentDetailVM", "CONFERMA RIPROGRAMMAZIONE per appuntamento ID: ${currentApt.id}")
            Log.i("AppointmentDetailVM", "Vecchia data: ${currentApt.date}, Vecchio orario: ${currentApt.timeSlot}")
            Log.i("AppointmentDetailVM", "NUOVA data: $newDate, NUOVO orario: $newTimeSlotString (indice: $timeSlotIndex)")
            _currentAppointment.value = currentApt.copy(
                date = newDate,
                timeSlot = newTimeSlotString
            )
        }
    }
}
