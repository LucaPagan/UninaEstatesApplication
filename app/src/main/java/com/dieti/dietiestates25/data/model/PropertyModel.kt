package com.dieti.dietiestates25.data.model

import androidx.annotation.DrawableRes // Importa questa annotazione

data class PropertyModel(
    val id: Int,
    val price: String,
    val type: String, // Es. "Appartamento", "Villa"
    @DrawableRes val imageRes: Int, // Risorsa drawable per l'immagine
    val location: String, // Es. "Napoli, Vomero"
    // Potresti aggiungere altri campi rilevanti qui in futuro, come:
    val areaMq: Int?,
    val rooms: Int?,
    val bathrooms: Int?,
    val purchaseType: String?,
    // val description: String?,
    // val isFavorite: Boolean = false
)