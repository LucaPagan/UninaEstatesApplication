package com.dieti.dietiestates25.ui.features.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val api = RetrofitClient.retrofit.create(com.dieti.dietiestates25.data.remote.DietiEstatesApi::class.java)

    private val _searchResults = MutableStateFlow<List<ImmobileDTO>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _citySuggestions = MutableStateFlow<List<String>>(emptyList())
    val citySuggestions = _citySuggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private var searchJob: Job? = null

    // Chiama il backend per ottenere i comuni mentre l'utente digita
    fun fetchCitySuggestions(query: String) {
        if (query.length < 2) {
            _citySuggestions.value = emptyList()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            try {
                Log.d("SearchViewModel", "Richiedo comuni per: $query")
                val cities = api.getComuni(query)
                Log.d("SearchViewModel", "Comuni ricevuti: $cities")
                _citySuggestions.value = cities
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Errore suggerimenti", e)
            }
        }
    }

    fun searchImmobili(query: String, filters: FilterModel? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val isVendita = when (filters?.purchaseType) {
                    "Compra" -> true
                    "Affitta" -> false
                    else -> null
                }

                // Se la query corrisponde esattamente a un comune, non mandarla come 'query' generica
                // ma potresti filtrare lato backend. Per ora la mandiamo come query testuale.
                val results = api.getImmobili(
                    query = query.ifBlank { null },
                    tipoVendita = isVendita,
                    minPrezzo = filters?.minPrice?.toInt(),
                    maxPrezzo = filters?.maxPrice?.toInt(),
                    minMq = filters?.minSurface?.toInt(),
                    maxMq = filters?.maxSurface?.toInt(),
                    bagni = filters?.bathrooms,
                    condizione = filters?.condition
                )

                _searchResults.value = results

                if (query.isNotBlank()) {
                    addToHistory(query)
                }

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Errore ricerca", e)
                _error.value = "Errore durante la ricerca: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun addToHistory(query: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(query)
        current.add(0, query)
        if (current.size > 10) current.removeAt(current.lastIndex)
        _recentSearches.value = current
    }

    fun clearHistoryItem(query: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(query)
        _recentSearches.value = current
    }
}