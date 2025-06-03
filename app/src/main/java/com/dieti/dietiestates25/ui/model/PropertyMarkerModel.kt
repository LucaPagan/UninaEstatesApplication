package com.dieti.dietiestates25.ui.model

import com.dieti.dietiestates25.R
import com.google.android.gms.maps.model.LatLng

data class PropertyMarker(
    val id: String,
    val position: LatLng,
    val title: String,
    val price: String,
    val type: String,
    val imageRes: Int, // Risorsa drawable (es. R.drawable.property1)
    val description: String = "",
    val surface: String = "",
    val bathrooms: Int = 0,
    val bedrooms: Int = 0,
    val purchaseType : String = "",
    val address: String = "",
    val condition: String = "",
    val isAvailable: Boolean = true,
    val priceValue: Int = 850,
    val surfaceValue: Int = 85

) {
    companion object {
        // Helper per le immagini delle proprietà
        fun getPropertyImage(propertyId: String): Int {
            return when (propertyId) {
                "1" -> R.drawable.property1
                "2" -> R.drawable.property2
                "3" -> R.drawable.property1 // Fallback per property3
                else -> R.drawable.property1 // Default fallback
            }
        }
    }
}