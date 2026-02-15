package com.dieti.dietiestates25.ui.features.manager

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Assicurati che EsitoRichiestaRequest sia definito nel tuo progetto Android
// data class EsitoRichiestaRequest(val id: String)

class ManagerNotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val _richieste = MutableStateFlow<List<RichiestaDTO>>(emptyList())
    val richieste = _richieste.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadRichieste() {
        val context = getApplication<Application>().applicationContext
        val agenteId = SessionManager.getUserId(context)

        if (agenteId == null) {
            Toast.makeText(context, "Errore: Agente non identificato", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.managerService.getRichiestePendenti(agenteId)
                if (response.isSuccessful) {
                    _richieste.value = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Errore caricamento: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Errore di connessione", Toast.LENGTH_SHORT).show()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // FIX: Aggiunto callback onSuccess per navigare indietro SOLO dopo il completamento
    fun accettaRichiesta(idRichiesta: String, onSuccess: () -> Unit = {}) {
        val context = getApplication<Application>().applicationContext
        val agenteId = SessionManager.getUserId(context) ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = EsitoRichiestaRequest(id = idRichiesta)
                val response = RetrofitClient.managerService.accettaRichiesta(agenteId, request)

                if (response.isSuccessful && response.body()?.message != null) {
                    _richieste.value = _richieste.value.filter { it.id != idRichiesta }
                    Toast.makeText(getApplication(), response.body()?.message, Toast.LENGTH_SHORT).show()
                    // Navighiamo indietro solo ora che siamo sicuri che il server ha ricevuto tutto
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msg = response.body()?.error ?: "Errore nella richiesta (${response.code()})"
                    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Se è stato cancellato (navigazione via), lo ignoriamo silenziosamente
                if (e is CancellationException) {
                    return@launch
                }
                // Altrimenti stampiamo l'errore reale
                e.printStackTrace()
                Toast.makeText(getApplication(), "Errore di rete: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // FIX: Aggiunto callback onSuccess anche qui
    fun rifiutaRichiesta(idRichiesta: String, onSuccess: () -> Unit = {}) {
        val context = getApplication<Application>().applicationContext
        val agenteId = SessionManager.getUserId(context) ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = EsitoRichiestaRequest(id = idRichiesta)
                val response = RetrofitClient.managerService.rifiutaRichiesta(request)

                if (response.isSuccessful && response.body()?.message != null) {
                    _richieste.value = _richieste.value.filter { it.id != idRichiesta }
                    Toast.makeText(getApplication(), response.body()?.message, Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    val msg = response.body()?.error ?: "Errore nella richiesta (${response.code()})"
                    Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    return@launch
                }
                e.printStackTrace()
                Toast.makeText(getApplication(), "Errore di rete: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }
}