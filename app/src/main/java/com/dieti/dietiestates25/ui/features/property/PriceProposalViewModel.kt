package com.dieti.dietiestates25.ui.features.property

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.OffertaRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PriceProposalViewModel : ViewModel() {

    private val _proposalState = MutableStateFlow<String>("")
    val proposalState = _proposalState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendProposal(
        utenteId: String,
        immobileId: String,
        amount: String,
        notes: String,
        context: Context
    ) {
        val priceInt = amount.toIntOrNull()
        if (priceInt == null || priceInt <= 0) {
            Toast.makeText(context, "Inserisci un importo valido", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _proposalState.value = "Invio in corso..."
            try {
                // Assumiamo che esista un endpoint per creare l'offerta
                // Se non esiste in UninaAPI, dovrai aggiungerlo:
                // @POST("api/offerte") suspend fun makeOffer(@Body req: OffertaRequest): Response<String>
                
                // Codice commentato finché non aggiorni UninaAPI, ma questa è la logica:
                /*
                val req = OffertaRequest(utenteId, immobileId, priceInt, notes)
                val response = RetrofitClient.instance.makeOffer(req)
                if (response.isSuccessful) {
                    _proposalState.value = "Successo"
                    Toast.makeText(context, "Offerta inviata!", Toast.LENGTH_SHORT).show()
                } else {
                    _proposalState.value = "Errore: ${response.code()}"
                }
                */
                
                // Simulazione per farti testare la UI
                kotlinx.coroutines.delay(1000)
                _proposalState.value = "Successo"
                Toast.makeText(context, "Offerta inviata (Simulazione)", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                _proposalState.value = "Errore: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}