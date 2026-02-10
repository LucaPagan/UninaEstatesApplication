package com.dieti.dietiestates25.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterModel(
    val purchaseType: String? = null,
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val minSurface: Float? = null,
    val maxSurface: Float? = null,
    val minRooms: Int? = null,
    val maxRooms: Int? = null,
    val bathrooms: Int? = null,
    val condition: String? = null,

    // Parametri per la ricerca su mappa (Geo)
    val centerLat: Double? = null,
    val centerLon: Double? = null,
    val radiusKm: Double? = null
) : Parcelable