package com.dieti.dietiestates25.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.model.PropertyMarker
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MapSearchViewModel : ViewModel() {

    private val _properties = MutableStateFlow<List<PropertyMarker>>(emptyList())
    val properties = _properties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchProperties(comune: String?, ricerca: String?, filters: FilterModel?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Scarica tutti gli immobili (o filtrati per località se possibile)
                val filtroLocalita = if (comune != null && comune != "Tutti") comune else ricerca
                val rawList = RetrofitClient.instance.getAllImmobili(
                    localita = if (!filtroLocalita.isNullOrBlank()) filtroLocalita else null
                )

                // 2. Applica i filtri avanzati lato client (poiché il backend ha filtri limitati)
                val filteredList = rawList.filter { dto ->
                    matchesFilters(dto, filters)
                }

                // 3. Converti in PropertyMarker (generando coordinate fittizie se mancano)
                _properties.value = filteredList.map { dto -> mapToMarker(dto) }

            } catch (e: Exception) {
                e.printStackTrace()
                _properties.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun matchesFilters(dto: ImmobileDTO, filters: FilterModel?): Boolean {
        if (filters == null) return true

        // Filtro Tipo Contratto (Vendita/Affitto)
        if (filters.purchaseType != null) {
            val isVenditaFilter = filters.purchaseType.equals("Compra", ignoreCase = true) || 
                                  filters.purchaseType.equals("Vendita", ignoreCase = true)
            if (dto.isVendita != isVenditaFilter) return false
        }

        // Filtro Prezzo
        if (filters.minPrice != null && dto.prezzo < filters.minPrice) return false
        if (filters.maxPrice != null && dto.prezzo > filters.maxPrice) return false

        // Filtro Superficie
        if (dto.mq != null) {
            if (filters.minSurface != null && dto.mq < filters.minSurface) return false
            if (filters.maxSurface != null && dto.mq > filters.maxSurface) return false
        }

        return true
    }

    private fun mapToMarker(dto: ImmobileDTO): PropertyMarker {
        // GENERAZIONE COORDINATE CASUALI PER DEMO (Napoli)
        // In produzione, dovresti usare Geocoder o salvare lat/long nel DB
        val baseLat = 40.8518
        val baseLng = 14.2681
        val randomLat = baseLat + (Random.nextDouble() - 0.5) * 0.05 // +/- 2-3km
        val randomLng = baseLng + (Random.nextDouble() - 0.5) * 0.05

        // URL Immagine
        val imgUrl = dto.coverImageId?.let { RetrofitClient.getImageUrl(it) }

        return PropertyMarker(
            id = dto.id,
            position = LatLng(randomLat, randomLng),
            title = dto.titolo,
            price = "€ ${dto.prezzo}",
            type = dto.tipologia ?: "Immobile",
            imageRes = 0, // Usiamo imageUrl
            imageUrl = imgUrl, // Aggiungi questo campo alla data class PropertyMarker se non c'è
            description = dto.descrizione,
            surface = "${dto.mq} m²",
            bathrooms = 1, // Dato non presente in DTO lista, default
            bedrooms = 2,  // Dato non presente in DTO lista, default
            purchaseType = if (dto.isVendita) "vendita" else "affitto",
            condition = "buono",
            priceValue = dto.prezzo.toFloat(),
            surfaceValue = (dto.mq ?: 0).toFloat()
        )
    }
}