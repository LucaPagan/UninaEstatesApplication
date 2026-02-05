package com.dieti.dietiestates25.ui.features.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.remote.DietiEstatesApi
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val api: DietiEstatesApi = RetrofitClient.retrofit.create(DietiEstatesApi::class.java)

    // Risultati della ricerca (se la ricerca viene effettuata in questa schermata)
    private val _searchResults = MutableStateFlow<List<ImmobileDTO>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    // Suggerimenti Comuni (Autocomplete)
    private val _citySuggestions = MutableStateFlow<List<String>>(emptyList())
    val citySuggestions = _citySuggestions.asStateFlow()

    // Ricerche Recenti (Storico)
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Job per il debounce della ricerca comuni
    private var searchJob: Job? = null

    // Inizializza caricando lo storico dal backend
    init {
        fetchRecentSearches()
    }

    // --- SUGGERIMENTI COMUNI (con Debounce) ---
    fun fetchCitySuggestions(query: String) {
        if (query.length < 2) {
            _citySuggestions.value = emptyList()
            return
        }

        // Cancella il job precedente se l'utente sta ancora scrivendo
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Attendi 300ms di inattività
            try {
                // Log.d("SearchViewModel", "Richiedo comuni per: $query")
                val results = api.getComuni(query)
                _citySuggestions.value = results
            } catch (e: Exception) {
                Log.e("SearchVM", "Error fetching cities", e)
            }
        }
    }

    // --- RICERCA IMMOBILI (Core) ---
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

                // Chiamata al backend. Nota: Il backend salva automaticamente la ricerca in cronologia
                // se 'query' non è vuota e l'utente è loggato.
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

                // Aggiorniamo la UI della cronologia se la query è valida
                if (query.isNotBlank()) {
                    // Possiamo aggiornare localmente per velocità o ricaricare dal backend
                    fetchRecentSearches()
                }

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Errore ricerca", e)
                _error.value = "Errore durante la ricerca: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- GESTIONE STORICO (Backend Integration) ---

    fun fetchRecentSearches() {
        viewModelScope.launch {
            try {
                // Recupera le ricerche dal backend (richiede utente loggato)
                if (RetrofitClient.loggedUserEmail != null) {
                    val history = api.getRicercheRecenti()
                    _recentSearches.value = history
                }
            } catch (e: Exception) {
                Log.e("SearchVM", "Error fetching history", e)
                // Fallisce silenziosamente per non disturbare l'utente
            }
        }
    }

    fun clearRecentSearch(query: String) {
        viewModelScope.launch {
            // 1. Aggiornamento ottimistico della UI (rimuove subito l'elemento)
            val currentList = _recentSearches.value.toMutableList()
            currentList.remove(query)
            _recentSearches.value = currentList

            // 2. Chiamata al backend per cancellare
            try {
                api.cancellaRicerca(query)
            } catch (e: Exception) {
                Log.e("SearchVM", "Error deleting history item", e)
                // Se fallisce, ricarichiamo la lista reale per sincronizzare
                fetchRecentSearches()
            }
        }
    }
}