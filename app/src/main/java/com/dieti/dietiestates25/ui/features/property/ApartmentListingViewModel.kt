package com.dieti.dietiestates25.ui.features.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApartmentListingViewModel : ViewModel() {
    private val _immobili = MutableStateFlow<List<ImmobileDTO>>(emptyList())
    val immobili = _immobili.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchImmobili(comune: String?, ricerca: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Logica di filtro: se "comune" è "Tutti", lo ignoriamo.
                // Se "ricerca" non è vuota, la usiamo come filtro localita o tipologia (dipende dal backend)
                val filtroLocalita = if (comune != null && comune != "Tutti") comune else ricerca?.trim()

                // Nota: se il parametro ricerca è usato per altro, adatta la chiamata
                val result = RetrofitClient.instance.getAllImmobili(
                    localita = if (!filtroLocalita.isNullOrEmpty()) filtroLocalita else null
                )
                _immobili.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _immobili.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}