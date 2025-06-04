package com.dieti.dietiestates25.ui.model

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

data class AppointmentDetail( // Già definito, lo lascio per completezza
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

    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN) // Modificato yyyy per anno completo

    // Mappa per convertire l'indice dello slot orario in una stringa rappresentativa
    private val timeSlotMap = mapOf(
        0 to "9:00 - 12:00",
        1 to "12:00 - 14:00",
        2 to "14:00 - 17:00",
        3 to "17:00 - 20:00"
    )

    fun loadAppointmentDetail(appointmentId: String?) {
        viewModelScope.launch {
            if (appointmentId == null) {
                _currentAppointment.value = null
                return@launch
            }
            _currentAppointment.value = when (appointmentId) {
                "apt1" -> AppointmentDetail(
                    id = "apt1",
                    title = "Visita Appartamento Via Roma",
                    date = LocalDate.now().plusDays(2),
                    timeSlot = "10:00 - 11:00", // Questo è un esempio, potrebbe non mappare direttamente agli indici
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
                    timeSlot = "15:30 - 16:30",
                    address = "Sede Agenzia DietiEstates, Via Toledo 45, Napoli",
                    propertyImageUrl = null,
                    description = "Riunione interna per discutere le nuove strategie di marketing per il trimestre.",
                    participants = listOf("Team Marketing", "Direzione Vendite"),
                    iconType = AppointmentIconType.MEETING
                )
                else -> AppointmentDetail(
                    id = appointmentId,
                    title = "Appuntamento Generico ID: $appointmentId",
                    date = LocalDate.now().plusDays(5),
                    timeSlot = timeSlotMap[0] ?: "Orario da definire", // Usa il primo slot come default
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

    fun rescheduleAppointmentAction() { // Nome precedente, ora apre il dialog
        Log.d("AppointmentDetailVM", "Azione Riprogramma chiamata per: ${_currentAppointment.value?.title}")
        // La logica di apertura del dialog sarà nella UI
    }

    fun cancelAppointment() {
        Log.d("AppointmentDetailVM", "Cancellazione appuntamento: ${_currentAppointment.value?.title}")
        // Qui aggiorneresti il backend/repository e poi lo stato dell'UI
        // Esempio: _currentAppointment.value = null o navigare indietro
    }

    // NUOVA FUNZIONE PER CONFERMARE LA RIPROGRAMMAZIONE
    fun confirmReschedule(newDate: LocalDate, timeSlotIndex: Int) {
        val newTimeSlotString = timeSlotMap[timeSlotIndex] ?: "Orario non specificato"
        _currentAppointment.value?.let { currentApt ->
            // In un'app reale: aggiorna il backend/repository
            Log.i("AppointmentDetailVM", "CONFERMA RIPROGRAMMAZIONE per appuntamento ID: ${currentApt.id}")
            Log.i("AppointmentDetailVM", "Vecchia data: ${currentApt.date}, Vecchio orario: ${currentApt.timeSlot}")
            Log.i("AppointmentDetailVM", "NUOVA data: $newDate, NUOVO orario: $newTimeSlotString (indice: $timeSlotIndex)")

            // Aggiorna lo stato locale per riflettere il cambiamento (simulazione)
            _currentAppointment.value = currentApt.copy(
                date = newDate,
                timeSlot = newTimeSlotString
            )
            // Qui potresti voler mostrare un messaggio di successo, chiudere il dialog, ecc.
        }
    }
}
