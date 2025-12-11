package com.dieti.dietiestates25.data.model

import java.time.LocalDate

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