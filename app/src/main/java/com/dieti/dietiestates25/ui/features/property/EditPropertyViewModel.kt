package com.dieti.dietiestates25.ui.features.property

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.ImmobileDetailDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPropertyViewModel : ViewModel() {
    private val _property = MutableStateFlow<ImmobileDetailDTO?>(null)
    val property = _property.asStateFlow()

    private val _updateState = MutableStateFlow<String>("")
    val updateState = _updateState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.instance.getImmobileDetail(id)
                if (res.isSuccessful) {
                    _property.value = res.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProperty(id: String, req: ImmobileCreateRequest) {
        viewModelScope.launch {
            _updateState.value = "Salvataggio..."
            try {
                // Assicurati che l'API supporti PUT /api/immobili/{id}
                // RetrofitClient.instance.updateImmobile(id, req)
                // Placeholder simulato
                _updateState.value = "Modifica salvata (Simulazione)"
            } catch (e: Exception) {
                _updateState.value = "Errore: ${e.message}"
            }
        }
    }
}