package com.dieti.dietiestates25.ui.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

class NotificationDetailViewModel : ViewModel() {
    data class NotificationDetail(
        val id: Int,
        val senderType: String,
        val senderName: String,
        val message: String,
        val propertyAddress: String,
        val propertyPrice: Double,
        val offerAmount: Double? = null
    )

    private val _currentNotification = MutableStateFlow(
        NotificationDetail(
            id = 1,
            senderType = "Compratore",
            senderName = "Mario Rossi",
            message = "Il %SENDER_TYPE% %SENDER_NAME% ha fatto un'offerta di %OFFER_AMOUNT% per l'immobile da lei messo in vendita situato in %PROPERTY_ADDRESS%. L'immobile era stato inizialmente listato a %PROPERTY_PRICE%. Valuti attentamente la proposta e decida se accettare o rifiutare. Può contattare direttamente il proponente per ulteriori chiarimenti o negoziazioni. Questa è una fase cruciale della transazione, quindi prenda il tempo necessario per una decisione informata.",
            propertyAddress = "Via Ripuaria 48, Pozzuoli (NA)",
            propertyPrice = 150000.0,
            offerAmount = 120000.0
        )
    )
    val currentNotification = _currentNotification.asStateFlow()

    fun getFormattedMessage(detail: NotificationDetail): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.ITALY)
        return detail.message
            .replace("%SENDER_TYPE%", detail.senderType)
            .replace("%SENDER_NAME%", detail.senderName)
            .replace("%PROPERTY_ADDRESS%", detail.propertyAddress)
            .replace("%PROPERTY_PRICE%", currencyFormat.format(detail.propertyPrice))
            .replace("%OFFER_AMOUNT%", detail.offerAmount?.let { currencyFormat.format(it) } ?: "N/A")
    }

    fun acceptProposal() {
        println("Proposta accettata: ID ${currentNotification.value.id}")
        // Implementa logica di accettazione
    }

    fun rejectProposal() {
        println("Proposta rifiutata: ID ${currentNotification.value.id}")
        // Implementa logica di rifiuto
    }
}