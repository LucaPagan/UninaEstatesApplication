package com.dieti.dietiestates25.ui.features.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class YourPropertyViewModel : ViewModel() {
    private val _myProperties = MutableStateFlow<List<ImmobileDTO>>(emptyList())
    val myProperties = _myProperties.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchMyProperties(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Idealmente useresti un endpoint dedicato: GET /api/immobili?proprietarioId=userId
                // Se non esiste, scarichiamo tutto e filtriamo in memoria (non ottimale ma funzionante per ora)
                val allProperties = RetrofitClient.instance.getAllImmobili()
                
                // Filtra solo quelli dell'utente
                _myProperties.value = allProperties.filter { it.proprietarioId == userId }
                
            } catch (e: Exception) {
                e.printStackTrace()
                _myProperties.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}