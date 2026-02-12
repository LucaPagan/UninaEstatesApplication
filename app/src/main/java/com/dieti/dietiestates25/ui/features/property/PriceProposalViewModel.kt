package com.dieti.dietiestates25.ui.features.property

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.OffertaRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager // Import del SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

// Spostato fuori dalla classe per essere visibile da altri file
sealed class ProposalState {
    object Idle : ProposalState()
    object Loading : ProposalState()
    object Success : ProposalState()
    data class Error(val message: String) : ProposalState()
}

class PriceProposalViewModel : ViewModel() {

    private val _proposalState = MutableStateFlow<ProposalState>(ProposalState.Idle)
    val proposalState = _proposalState.asStateFlow()

    private val _immobileData = MutableStateFlow<ImmobileDTO?>(null)
    val immobileData = _immobileData.asStateFlow()

    fun loadImmobileData(immobileId: String) {
        viewModelScope.launch {
            try {
                // FIX: getImmobileById restituisce direttamente ImmobileDTO, non Response<ImmobileDTO>.
                val immobile = RetrofitClient.propertyService.getImmobileById(immobileId)
                _immobileData.value = immobile
            } catch (e: Exception) {
                // Errore silenzioso: in caso di fallimento manteniamo i valori di default della UI
            }
        }
    }

    fun sendProposal(
        immobileId: String,
        amount: String,
        notes: String = "",
        context: Context
    ) {
        val priceInt = amount.replace(".", "").toIntOrNull()

        if (priceInt == null || priceInt <= 0) {
            _proposalState.value = ProposalState.Error("Inserisci un importo valido")
            return
        }

        // BEST PRACTICE: Recuperiamo l'ID dell'utente dal SessionManager.
        val utenteId = SessionManager.getUserId(context)

        if (utenteId.isNullOrBlank()) {
            _proposalState.value = ProposalState.Error("Sessione scaduta o non valida. Effettua il login.")
            return
        }

        viewModelScope.launch {
            _proposalState.value = ProposalState.Loading
            try {
                val request = OffertaRequest(
                    utenteId = utenteId, // ID recuperato dal SessionManager
                    immobileId = immobileId,
                    importo = priceInt,
                    corpo = notes.ifBlank { null }
                )

                val response = RetrofitClient.offertaService.inviaOfferta(request)

                if (response.isSuccessful) {
                    _proposalState.value = ProposalState.Success
                    Toast.makeText(context, "Offerta inviata con successo!", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Errore sconosciuto"

                    // GESTIONE SPECIFICA DEL MESSAGGIO DUPLICATO
                    if (errorBody.contains("già inviato un'offerta", ignoreCase = true)) {
                        _proposalState.value = ProposalState.Error(errorBody)
                    } else {
                        _proposalState.value = ProposalState.Error("Errore server: $errorBody")
                    }
                }
            } catch (e: Exception) {
                // Log per debug
                Log.e("PriceProposalViewModel", "Errore invio offerta", e)

                // WORKAROUND CRUCIALE:
                // Se il backend risponde con una stringa raw (es. "Offerta inviata") e non JSON,
                // Gson lancia MalformedJsonException o JsonSyntaxException.
                // Poiché questo accade SOLO se il server ha risposto (quindi operazione riuscita),
                // trattiamo questi specifici errori come Successo.
                val isParsingError = e.javaClass.simpleName.contains("MalformedJsonException") ||
                        e.javaClass.simpleName.contains("JsonSyntaxException") ||
                        (e.message?.contains("JSON", ignoreCase = true) == true)

                if (isParsingError) {
                    _proposalState.value = ProposalState.Success
                    Toast.makeText(context, "Offerta inviata con successo!", Toast.LENGTH_SHORT).show()
                } else if (e is IOException) {
                    _proposalState.value = ProposalState.Error("Errore di connessione.")
                } else {
                    _proposalState.value = ProposalState.Error("Errore imprevisto: ${e.message}")
                }
            }
        }
    }

    fun resetState() {
        _proposalState.value = ProposalState.Idle
    }
}