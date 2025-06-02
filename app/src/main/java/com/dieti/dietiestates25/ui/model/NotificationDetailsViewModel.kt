package com.dieti.dietiestates25.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.util.Log

// Data class per i dettagli della notifica
data class NotificationDetail(
    val id: Int,
    val senderType: String, // Es. "Proposta d'acquisto", "Sistema", "Agenzia"
    val senderName: String, // Es. "Mario Rossi", "Supporto DietiEstates"
    val message: String,    // Il messaggio completo
    val rawMessageForFormatting: String? = null, // Messaggio originale se serve per formattazione speciale
    val date: LocalDate,
    var isFavorite: Boolean,
    val iconType: NotificationIconType,
    val isProposal: Boolean, // Indica se la notifica è una proposta che richiede azioni
    val proposalAmount: String? = null, // Es. "€250.000" - specifico per le proposte
    val propertyAddress: String? = null // Indirizzo immobile relativo alla proposta
    // Aggiungi altri campi specifici se necessario
)

class NotificationDetailViewModel : ViewModel() {

    private val _currentNotification = MutableStateFlow<NotificationDetail?>(null)
    val currentNotification: StateFlow<NotificationDetail?> = _currentNotification.asStateFlow()

    // Esempio di dati (sostituisci con il tuo data source reale)
    private val sampleNotifications = listOf(
        NotificationDetail(
            id = 1,
            senderType = "Proposta d'acquisto",
            senderName = "Mario Rossi",
            message = "Il sig. Mario Rossi ha inviato una nuova proposta d'acquisto per l'immobile situato in Via Toledo 123, per un importo di €280.000. La proposta è valida fino al 15 Giugno 2025.",
            rawMessageForFormatting = "Il sig. %s ha inviato una nuova proposta d'acquisto per l'immobile situato in %s, per un importo di %s. La proposta è valida fino al %s.",
            date = LocalDate.now().minusDays(1),
            isFavorite = false,
            iconType = NotificationIconType.PERSON,
            isProposal = true,
            proposalAmount = "€280.000",
            propertyAddress = "Via Toledo 123, Napoli"
        ),
        NotificationDetail(
            id = 2,
            senderType = "Sistema",
            senderName = "DietiEstates",
            message = "Manutenzione programmata per il server il giorno 10 Giugno 2025 dalle 02:00 alle 04:00. Alcune funzionalità potrebbero essere limitate.",
            date = LocalDate.now().minusDays(3),
            isFavorite = true,
            iconType = NotificationIconType.BADGE,
            isProposal = false
        ),
        NotificationDetail(
            id = 3,
            senderType = "Contatto Telefonico",
            senderName = "Agenzia Immobiliare Sole",
            message = "Hai una chiamata persa da Agenzia Immobiliare Sole. Ricontattare al più presto per aggiornamenti sull'immobile di Via Chiaia.",
            date = LocalDate.now(),
            isFavorite = false,
            iconType = NotificationIconType.PHONE,
            isProposal = false
        )
    )

    fun loadNotificationById(notificationId: Int?) {
        viewModelScope.launch {
            Log.d("NotificationDetailVM", "Attempting to load notification with ID: $notificationId")
            if (notificationId == null) {
                _currentNotification.value = null
                Log.w("NotificationDetailVM", "Notification ID is null.")
                return@launch
            }
            // Simula il caricamento da una sorgente dati
            val detail = sampleNotifications.find { it.id == notificationId }
            _currentNotification.value = detail
            if (detail == null) {
                Log.w("NotificationDetailVM", "No notification found for ID: $notificationId")
            } else {
                Log.d("NotificationDetailVM", "Notification loaded: ${detail.senderType}")
            }
        }
    }

    fun getFormattedMessage(notificationDetail: NotificationDetail?): String {
        if (notificationDetail == null) return "Nessun messaggio."

        // Esempio di formattazione più complessa se necessario
        if (notificationDetail.isProposal && notificationDetail.rawMessageForFormatting != null) {
            try {
                return String.format(
                    Locale.ITALIAN,
                    notificationDetail.rawMessageForFormatting,
                    notificationDetail.senderName,
                    notificationDetail.propertyAddress ?: "indirizzo non specificato",
                    notificationDetail.proposalAmount ?: "importo non specificato",
                    notificationDetail.date.plusDays(10).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ITALIAN)) // Esempio: scadenza proposta
                )
            } catch (e: Exception) {
                Log.e("NotificationDetailVM", "Error formatting proposal message", e)
                // Fallback al messaggio semplice se la formattazione fallisce
            }
        }
        return notificationDetail.message
    }

    fun acceptProposal() {
        // Logica per accettare la proposta
        // Potrebbe aggiornare lo stato della notifica/proposta nel backend/repository
        _currentNotification.value?.let {
            Log.d("NotificationDetailVM", "Proposta accettata per notifica ID: ${it.id}")
            // Esempio: aggiorna lo stato locale (sebbene dovrebbe venire da una fonte unica di verità)
            // _currentNotification.value = it.copy(isProposal = false, message = "Proposta Accettata!")
        }
    }

    fun rejectProposal() {
        // Logica per rifiutare la proposta
        _currentNotification.value?.let {
            Log.d("NotificationDetailVM", "Proposta rifiutata per notifica ID: ${it.id}")
            // _currentNotification.value = it.copy(isProposal = false, message = "Proposta Rifiutata.")
        }
    }

    // Questa funzione non è più necessaria qui se onToggleMasterFavorite viene passata dall'esterno.
    // Se vuoi che questo ViewModel gestisca anche il toggle (e poi lo comunichi al repository),
    // allora potresti tenerla e modificarla.
    /*
    fun toggleFavoriteCurrentNotification() {
        _currentNotification.value?.let { currentDetail ->
            val newFavoriteState = !currentDetail.isFavorite
            // Qui dovresti aggiornare la sorgente dati (es. Repository)
            // e poi ricaricare o aggiornare _currentNotification.
            // Per ora, simuliamo l'aggiornamento locale:
            _currentNotification.value = currentDetail.copy(isFavorite = newFavoriteState)
            Log.d("NotificationDetailVM", "Favorite toggled for ID ${currentDetail.id} to $newFavoriteState")
        }
    }
    */
}