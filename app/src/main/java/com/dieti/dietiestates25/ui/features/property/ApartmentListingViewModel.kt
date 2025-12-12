package com.dieti.dietiestates25.ui.features.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.parsePriceToFloat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ApartmentListingViewModel : ViewModel() {

    // Lista originale scaricata dal server
    private val _allImmobili = MutableStateFlow<List<ImmobileDTO>>(emptyList())

    // Filtri correnti applicati
    private val _currentFilters = MutableStateFlow<FilterModel?>(null)

    // Stato di caricamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Lista filtrata combinata: si aggiorna automaticamente se cambia la lista o cambiano i filtri
    val filteredImmobili: StateFlow<List<ImmobileDTO>> = combine(_allImmobili, _currentFilters) { immobili, filters ->
        if (filters == null) {
            immobili
        } else {
            applyFilters(immobili, filters)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtri esposti per la UI (per sapere se il badge deve essere attivo)
    val currentFilters: StateFlow<FilterModel?> = _currentFilters

    fun fetchImmobili(comune: String, ricerca: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Esempio di chiamata (adatta in base al tuo Repository/RetrofitClient effettivo)
                // Qui assumo che RetrofitClient.apiService.getImmobili(...) restituisca la lista
                val result = RetrofitClient.apiService.cercaImmobili(comune, ricerca) // Adatta nome metodo
                if (result.isSuccessful) {
                    _allImmobili.value = result.body() ?: emptyList()
                } else {
                    // Gestione errore
                    _allImmobili.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _allImmobili.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateFilters(newFilters: FilterModel?) {
        _currentFilters.value = newFilters
    }

    private fun applyFilters(list: List<ImmobileDTO>, filters: FilterModel): List<ImmobileDTO> {
        return list.filter { property ->
            // 1. Tipologia (Purchase Type / Vendita vs Affitto)
            // Nota: ImmobileDTO deve avere un campo corrispondente o bisogna adattare la logica
            if (filters.purchaseType != null) {
                // property.tipologia?.let { if (it != filters.purchaseType) return@filter false }
            }

            // 2. Prezzo
            // Assumiamo che property.prezzo sia una Stringa tipo "100000" o "100.000"
            // Usiamo l'utility parsePriceToFloat se disponibile, o convertiamo
            val priceVal = property.prezzo?.replace(".", "")?.replace(",", ".")?.toFloatOrNull()

            if (filters.minPrice != null && (priceVal == null || priceVal < filters.minPrice)) return@filter false
            if (filters.maxPrice != null && (priceVal == null || priceVal > filters.maxPrice)) return@filter false

            // 3. Superficie (Area)
            // Assumiamo che property.superficie sia un Int o String
            val areaVal = property.superficie?.toFloat() // o property.areaMq
            if (filters.minSurface != null && (areaVal == null || areaVal < filters.minSurface)) return@filter false
            if (filters.maxSurface != null && (areaVal == null || areaVal > filters.maxSurface)) return@filter false

            // 4. Stanze
            val roomsVal = property.stanze // Assumiamo sia Int
            if (filters.minRooms != null && (roomsVal == null || roomsVal < filters.minRooms)) return@filter false
            if (filters.maxRooms != null && (roomsVal == null || roomsVal > filters.maxRooms)) return@filter false

            // 5. Bagni
            val bathsVal = property.bagni // Assumiamo sia Int
            if (filters.bathrooms != null && (bathsVal == null || bathsVal < filters.bathrooms)) return@filter false

            // 6. Condizione
            if (filters.condition != null) {
                // property.condizione?.let { if (it != filters.condition) return@filter false }
            }

            true
        }
    }
}