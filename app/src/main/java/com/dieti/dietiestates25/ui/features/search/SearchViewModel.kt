package com.dieti.dietiestates25.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<ImmobileDTO>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    fun searchImmobili(queryLocalita: String, maxPrice: String) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                // Parsing sicuro del prezzo
                val priceInt = maxPrice.toIntOrNull()
                
                // Chiamata backend filtrata
                val results = RetrofitClient.instance.getAllImmobili(
                    localita = if(queryLocalita.isNotBlank()) queryLocalita else null,
                    prezzoMax = priceInt
                )
                _searchResults.value = results
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }
}