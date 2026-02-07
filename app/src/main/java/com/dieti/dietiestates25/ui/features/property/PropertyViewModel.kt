package com.dieti.dietiestates25.ui.features.property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyViewModel : ViewModel() {

    private val api = RetrofitClient.retrofit.create(PropertyApiService::class.java)


    private val _property = MutableStateFlow<ImmobileDTO?>(null)
    val property = _property.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = api.getImmobileById(id)
                _property.value = result
            } catch (e: Exception) {
                Log.e("PropertyVM", "Errore caricamento immobile $id", e)
                _error.value = "Impossibile caricare i dettagli: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}