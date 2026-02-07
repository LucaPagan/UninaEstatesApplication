package com.dieti.dietiestates25.ui.features.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.R // Importante per accedere alle risorse
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.model.PropertyMarker
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.features.property.PropertyApiService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapSearchViewModel : ViewModel() {

    private val PropertyApi = RetrofitClient.retrofit.create(PropertyApiService::class.java)

    private val _properties = MutableStateFlow<List<PropertyMarker>>(emptyList())
    val properties = _properties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Cerca immobili basandosi sul testo (Comune/Zona) o sui filtri esistenti
    fun loadProperties(
        query: String,
        filters: FilterModel? = null,
        searchArea: LatLng? = null, // Se != null, facciamo ricerca geo
        radiusKm: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Mappatura filtri UI -> Backend
                val isVendita = when (filters?.purchaseType) {
                    "Compra" -> true
                    "Affitta" -> false
                    else -> null
                }

                // Se searchArea è definito, usiamo lat/lon/radius, altrimenti usiamo la query testuale
                val finalQuery = if (searchArea == null) query.ifBlank { null } else null
                val lat = searchArea?.latitude
                val lon = searchArea?.longitude
                val rad = if (searchArea != null) (radiusKm ?: 5.0) else null

                val results = PropertyApi.getImmobili(
                    query = finalQuery,
                    tipoVendita = isVendita,
                    minPrezzo = filters?.minPrice?.toInt(),
                    maxPrezzo = filters?.maxPrice?.toInt(),
                    minMq = filters?.minSurface?.toInt(),
                    maxMq = filters?.maxSurface?.toInt(),
                    bagni = filters?.bathrooms,
                    condizione = filters?.condition,
                    lat = lat,
                    lon = lon,
                    radiusKm = rad
                )

                _properties.value = results.mapNotNull { it.toPropertyMarker() }

            } catch (e: Exception) {
                Log.e("MapSearchViewModel", "Errore caricamento mappa", e)
                _error.value = "Errore di connessione: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Funzione di utilità per convertire DTO in UI Model (Marker)
    private fun ImmobileDTO.toPropertyMarker(): PropertyMarker? {
        if (lat == null || long == null) return null

        // Costruiamo il prezzo formattato
        val priceFormatted = if (tipoVendita) {
            "€${prezzo?.let { String.format("%,d", it) } ?: "N.D."}"
        } else {
            "€${prezzo}/mese"
        }

        // FIX: Costruzione URL sicura usando getFullUrl
        val finalImageUrl = immagini.firstOrNull()?.url?.let {
            RetrofitClient.getFullUrl(it)
        }

        return PropertyMarker(
            id = id,
            position = LatLng(lat, long),
            title = categoria ?: "Immobile",
            price = priceFormatted,
            type = categoria ?: "",
            // FIX CRITICO: 0 causava il crash ResourceNotFoundException.
            // Usiamo una risorsa valida come fallback/placeholder.
            imageRes = R.drawable.ic_launcher_foreground,
            imageUrl = finalImageUrl,
            description = descrizione ?: "",
            surface = "$mq m²",
            bathrooms = 0,
            bedrooms = 0,
            purchaseType = if (tipoVendita) "vendita" else "affitto",
            condition = "",
            priceValue = prezzo?.toFloat() ?: 0f,
            surfaceValue = mq?.toFloat() ?: 0f,
            parco = parco,
            scuola = scuola,
            servizioPubblico = servizioPubblico
        )
    }
}