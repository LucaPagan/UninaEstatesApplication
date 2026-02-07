package com.dieti.dietiestates25.ui.features.property

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.AmbienteDto
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EditUiState {
    object Loading : EditUiState()
    data class Content(val immobile: ImmobileDTO) : EditUiState()
    data class Error(val msg: String) : EditUiState()
    object SuccessOperation : EditUiState() // Per quando il salvataggio o delete va a buon fine
}

class EditPropertyViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.retrofit.create(PropertyApiService::class.java)

    private val _uiState = MutableStateFlow<EditUiState>(EditUiState.Loading)
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            try {
                _uiState.value = EditUiState.Loading
                val result = api.getImmobileById(id)
                _uiState.value = EditUiState.Content(result)
            } catch (e: Exception) {
                _uiState.value = EditUiState.Error("Errore caricamento: ${e.message}")
            }
        }
    }

    fun updateProperty(
        id: String,
        originalDto: ImmobileDTO,
        newPrice: Int,
        newMq: Int,
        newDesc: String,
        newBeds: Int,
        newBaths: Int,
        newRooms: Int
    ) {
        viewModelScope.launch {
            try {
                // Ricostruiamo la lista ambienti basandoci sui contatori della UI
                val nuoviAmbienti = mutableListOf<AmbienteDto>()
                if (newBeds > 0) nuoviAmbienti.add(AmbienteDto("Letto", newBeds))
                if (newBaths > 0) nuoviAmbienti.add(AmbienteDto("Bagno", newBaths))
                if (newRooms > 0) nuoviAmbienti.add(AmbienteDto("Vani", newRooms))

                // Creiamo la request mantenendo i dati vecchi per quelli che non modifichiamo nella UI
                val request = ImmobileCreateRequest(
                    tipoVendita = originalDto.tipoVendita,
                    categoria = originalDto.categoria,
                    indirizzo = originalDto.indirizzo,
                    localita = originalDto.localita,
                    mq = newMq,
                    piano = originalDto.piano,
                    ascensore = originalDto.ascensore,
                    arredamento = originalDto.arredamento,
                    climatizzazione = originalDto.climatizzazione,
                    esposizione = originalDto.esposizione,
                    statoProprieta = originalDto.statoProprieta,
                    annoCostruzione = originalDto.annoCostruzione,
                    prezzo = newPrice,
                    speseCondominiali = originalDto.speseCondominiali,
                    descrizione = newDesc,
                    ambienti = nuoviAmbienti
                )

                val response = api.updateImmobile(id, request)
                if (response.isSuccessful) {
                    _uiState.value = EditUiState.SuccessOperation
                } else {
                    _uiState.value = EditUiState.Error("Errore salvataggio: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = EditUiState.Error("Errore rete: ${e.message}")
            }
        }
    }

    fun deleteProperty(id: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteImmobile(id)
                if (response.isSuccessful) {
                    _uiState.value = EditUiState.SuccessOperation
                } else {
                    _uiState.value = EditUiState.Error("Errore cancellazione: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = EditUiState.Error("Errore rete: ${e.message}")
            }
        }
    }

    // Reset stato per permettere nuova navigazione se serve
    fun resetState() {
        _uiState.value = EditUiState.Loading
    }
}