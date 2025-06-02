package com.dieti.dietiestates25.ui.model // O il tuo percorso corretto per i model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.R // Per accedere alle risorse di esempio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Data class per rappresentare i dettagli di un appuntamento (può espandere Appointment se necessario)
data class AppointmentDetail(
    val id: String,
    val title: String,
    val date: LocalDate,
    val timeSlot: String, // Es. "10:00 - 11:00"
    val address: String,
    val propertyImageUrl: Int?, // ID risorsa drawable, nullable
    val description: String?,
    val participants: List<String>?,
    val iconType: AppointmentIconType = AppointmentIconType.VISIT,
    val notes: String? = null // Note aggiuntive per l'appuntamento
)

// ViewModel per AppointmentDetailScreen
class AppointmentDetailViewModel : ViewModel() {

    private val _currentAppointment = MutableStateFlow<AppointmentDetail?>(null)
    val currentAppointment: StateFlow<AppointmentDetail?> = _currentAppointment.asStateFlow()

    // Formatter riutilizzabili
    private val dateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.ITALIAN)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ITALIAN)


    fun loadAppointmentDetail(appointmentId: String?) {
        viewModelScope.launch {
            // --- SIMULAZIONE CARICAMENTO DATI ---
            // In un'app reale, qui faresti una chiamata a un repository/database
            // per ottenere i dettagli dell'appuntamento basati su appointmentId.
            if (appointmentId == null) {
                _currentAppointment.value = null // O gestisci l'errore
                return@launch
            }

            // Dati di esempio
            _currentAppointment.value = when (appointmentId) {
                "apt1" -> AppointmentDetail(
                    id = "apt1",
                    title = "Visita Appartamento Via Roma",
                    date = LocalDate.now().plusDays(2),
                    timeSlot = "10:00 - 11:00",
                    address = "Via Roma, 123, Napoli",
                    propertyImageUrl = R.drawable.property1, // Usa una tua risorsa drawable
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
                    propertyImageUrl = null, // Nessuna immagine per un meeting
                    description = "Riunione interna per discutere le nuove strategie di marketing per il trimestre.",
                    participants = listOf("Team Marketing", "Direzione Vendite"),
                    iconType = AppointmentIconType.MEETING
                )
                else -> AppointmentDetail( // Fallback o appuntamento di default
                    id = appointmentId,
                    title = "Appuntamento Generico",
                    date = LocalDate.now().plusDays(5),
                    timeSlot = "Orario da definire",
                    address = "Indirizzo non specificato",
                    propertyImageUrl = R.drawable.property2,
                    description = "Dettagli non disponibili per questo appuntamento.",
                    participants = null,
                    iconType = AppointmentIconType.GENERIC
                )
            }
            // --- FINE SIMULAZIONE ---
        }
    }

    fun getFormattedDate(appointmentDetail: AppointmentDetail?): String {
        return appointmentDetail?.date?.format(dateFormatter)?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ITALIAN) else it.toString()
        } ?: "Data non disponibile"
    }

    fun getFormattedTime(appointmentDetail: AppointmentDetail?): String {
        // Questa è una semplificazione, potresti voler estrarre l'ora di inizio da timeSlot
        return appointmentDetail?.timeSlot ?: "Orario non specificato"
    }

    // Eventuali azioni (es. modifica, cancella appuntamento) possono essere aggiunte qui
    fun rescheduleAppointment() {
        // Logica per riprogrammare
        Log.d("AppointmentDetailVM", "Riprogrammazione appuntamento: ${_currentAppointment.value?.title}")
    }

    fun cancelAppointment() {
        // Logica per cancellare
        Log.d("AppointmentDetailVM", "Cancellazione appuntamento: ${_currentAppointment.value?.title}")
        // Esempio: navigare indietro o mostrare conferma
    }
}