package com.dieti.dietiestates25.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _immobili = MutableStateFlow<List<ImmobileDTO>>(emptyList())
    val immobili = _immobili.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchImmobili()
    }

    fun fetchImmobili(localita: String? = null, tipologia: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = RetrofitClient.instance.getAllImmobili(localita, tipologia)
                _immobili.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}