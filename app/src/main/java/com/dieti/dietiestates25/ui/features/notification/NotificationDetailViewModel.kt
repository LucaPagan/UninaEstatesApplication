package com.dieti.dietiestates25.ui.features.notification

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.NotificationDetail
import com.dieti.dietiestates25.data.model.NotificationIconType
import com.dieti.dietiestates25.data.remote.NotificationDetailDTO
import com.dieti.dietiestates25.data.remote.ProposalResponseRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class NotificationDetailViewModel : ViewModel() {

    private val _currentNotification = MutableStateFlow<NotificationDetail?>(null)
    val currentNotification: StateFlow<NotificationDetail?> = _currentNotification.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Accetta String (UUID) invece di Int
    fun loadNotificationById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getNotificationDetail(id)
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    _currentNotification.value = mapDtoToDomain(dto)
                } else {
                    Log.e("NotifDetailVM", "Errore caricamento: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NotifDetailVM", "Eccezione caricamento notifica", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mapDtoToDomain(dto: NotificationDetailDTO): NotificationDetail {
        return NotificationDetail(
            id = dto.id,
            title = dto.titolo, // Mappa il titolo
            senderType = mapSenderType(dto.mittenteTipo),
            senderName = dto.mittenteNome ?: "DietiEstates",
            message = dto.corpo ?: "Nessun contenuto disponibile", // Gestione null
            iconType = mapIconType(dto.mittenteTipo),
            isProposal = dto.isProposta,
            proposalPrice = dto.prezzoProposto, // Mappa il prezzo
            isFavorite = false, // Backend non supporta preferiti per singola notifica
            timestamp = dto.data
        )
    }

    private fun mapIconType(type: String?): NotificationIconType {
        return when (type?.uppercase()) {
            "AGENTE" -> NotificationIconType.PERSON
            "AGENZIA" -> NotificationIconType.BADGE
            else -> NotificationIconType.PHONE
        }
    }

    private fun mapSenderType(type: String?): String {
        return when (type?.uppercase()) {
            "AGENTE" -> "Agente Immobiliare"
            "AGENZIA" -> "Agenzia"
            "SISTEMA" -> "Avviso di Sistema"
            else -> "Notifica"
        }
    }

    // --- GESTIONE PROPOSTE ---

    fun acceptProposal(context: Context) {
        val id = _currentNotification.value?.id ?: return
        sendProposalResponse(id, true, context)
    }

    fun rejectProposal(context: Context) {
        val id = _currentNotification.value?.id ?: return
        sendProposalResponse(id, false, context)
    }

    // Placeholder per chiamate UI senza context (da evitare ma presenti per compatibilità)
    fun acceptProposal() { Log.w("NotifDetailVM", "Context mancante") }
    fun rejectProposal() { Log.w("NotifDetailVM", "Context mancante") }

    private fun sendProposalResponse(id: String, accepted: Boolean, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.respondToProposal(
                    id,
                    ProposalResponseRequest(accettata = accepted)
                )

                if (response.isSuccessful) {
                    val msg = if (accepted) "Proposta accettata!" else "Proposta rifiutata."
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    // Opzionale: Ricarica per aggiornare lo stato UI
                    loadNotificationById(id)
                } else {
                    Toast.makeText(context, "Errore nell'invio della risposta", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("NotifDetailVM", "Errore risposta proposta", e)
                Toast.makeText(context, "Errore di connessione", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getFormattedMessage(detail: NotificationDetail?): String {
        return detail?.message ?: "Nessun dettaglio disponibile."
    }

    fun getFormattedPrice(price: Double?): String {
        return if (price != null) {
            NumberFormat.getCurrencyInstance(Locale.ITALY).format(price)
        } else {
            ""
        }
    }
}