package com.dieti.dietiestates25.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.screen.Property

class SharedViewModel : ViewModel() {
    // Lista delle proprietà osservabile
    private val _properties = mutableStateListOf<Property>()
    val properties: List<Property> get() = _properties

    // Ricerche recenti
    private val _recentSearches = mutableStateListOf<String>()
    val recentSearches: List<String> get() = _recentSearches

    // Query di ricerca corrente
    val currentSearchQuery = mutableStateOf("")

    init {
        // Carica dati di esempio
        loadSampleProperties()
    }

    private fun loadSampleProperties() {
        _properties.addAll(
            listOf(
                Property(1, "400.000", "Appartamento in centro", R.drawable.property1),
                Property(2, "320.000", "Villa con giardino", R.drawable.property2),
                Property(3, "250.000", "Attico vista mare", R.drawable.property1),
                Property(4, "180.000", "Bilocale ristrutturato", R.drawable.property2)
            )
        )
    }

    // Carica le ricerche recenti dalle preferenze
    fun loadRecentSearches(searches: List<String>) {
        _recentSearches.clear()
        _recentSearches.addAll(searches)
    }

    // Aggiunge una nuova ricerca
    fun addSearch(query: String) {
        if (query.isBlank()) return

        // Rimuovi se già presente per evitare duplicati
        _recentSearches.remove(query)

        // Aggiungi all'inizio
        _recentSearches.add(0, query)

        // Mantieni solo le ultime 10 ricerche
        if (_recentSearches.size > 10) {
            _recentSearches.removeAt(_recentSearches.lastIndex)
        }
    }

    // Ottieni dettagli di una proprietà per ID
    fun getPropertyById(id: Int): Property? {
        return _properties.find { it.id == id }
    }

    // Filtra proprietà in base a una query
    fun searchProperties(query: String): List<Property> {
        if (query.isBlank()) return _properties

        return _properties.filter {
            it.price.contains(query, ignoreCase = true) ||
                    it.type.contains(query, ignoreCase = true)
        }
    }
}