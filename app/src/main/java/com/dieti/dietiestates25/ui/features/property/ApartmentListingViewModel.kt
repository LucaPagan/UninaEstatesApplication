package com.dieti.dietiestates25.ui.features.property

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApartmentListingViewModel : ViewModel() {

    private val api = RetrofitClient.retrofit.create(PropertyApiService::class.java)


    private val _immobili = MutableStateFlow<List<ImmobileDTO>>(emptyList())
    val immobili = _immobili.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadImmobili(
        comune: String,
        ricerca: String,
        filters: FilterModel? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Costruiamo la query di ricerca unendo comune e testo libero
                val fullQuery = if (ricerca.isNotBlank()) "$comune $ricerca" else comune

                val isVendita = when (filters?.purchaseType) {
                    "Compra" -> true
                    "Affitta" -> false
                    else -> null
                }

                val results = api.getImmobili(
                    query = fullQuery,
                    tipoVendita = isVendita,
                    minPrezzo = filters?.minPrice?.toInt(),
                    maxPrezzo = filters?.maxPrice?.toInt(),
                    minMq = filters?.minSurface?.toInt(),
                    maxMq = filters?.maxSurface?.toInt(),
                    bagni = filters?.bathrooms,
                    condizione = filters?.condition,
                    // Parametri geografici (se presenti nei filtri passati dalla mappa)
                )

                _immobili.value = results

            } catch (e: Exception) {
                Log.e("ApartmentListVM", "Errore caricamento immobili", e)
                _error.value = "Errore di connessione: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}