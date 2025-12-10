package com.dieti.dietiestates25.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // Utile se vuoi passare l'intero oggetto come argomento di navigazione complesso (con librerie apposite)
// Per i query params, non strettamente necessario ma buona pratica per modelli di dati.
data class FilterModel(
    val purchaseType: String? = null,
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val minSurface: Float? = null,
    val maxSurface: Float? = null,
    val minRooms: Int? = null,
    val maxRooms: Int? = null,
    val bathrooms: Int? = null, // Assumendo singola scelta per i bagni
    val condition: String? = null
) : Parcelable // Implementa Parcelable se la passi come argomento complesso.