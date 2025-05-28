package com.dieti.dietiestates25.ui.model

import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.LatLng

data class PropertyMarker(
    val id: String, // Aggiunto ID univoco per la navigazione e come chiave
    val position: LatLng,
    val title: String,
    val price: String,
    val type: String,
    @DrawableRes val imageRes: Int // Aggiunta risorsa immagine per la preview
)