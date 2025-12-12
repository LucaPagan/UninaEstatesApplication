package com.dieti.dietiestates25.data.model

data class NotificationDetail(
    val id: String, // Aggiornato a String (UUID)
    val title: String, // Nuovo campo Titolo
    val senderType: String,
    val senderName: String,
    val message: String, // Mappa 'corpo'
    val iconType: NotificationIconType,
    val isProposal: Boolean,
    val proposalPrice: Double?, // Nuovo campo per il prezzo della proposta
    val isFavorite: Boolean,
    val timestamp: String
)