import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.features.manager.EsitoRichiestaRequest
import com.dieti.dietiestates25.ui.features.manager.RichiestaDTO
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                Toast.makeText(context, "Errore di connessione", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun accettaRichiesta(idRichiesta: String) {
        val context = getApplication<Application>().applicationContext
        val agenteId = SessionManager.getUserId(context)

        if (agenteId == null) {
            Toast.makeText(context, "Errore: Agente non identificato", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = EsitoRichiestaRequest(id = idRichiesta)
                val response = RetrofitClient.managerService.accettaRichiesta(agenteId, request)

                if (response.isSuccessful) {
                    _richieste.value = _richieste.value.filter { it.id != idRichiesta }
                    Toast.makeText(getApplication(), "Richiesta accettata", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Errore sconosciuto"
                    Toast.makeText(getApplication(), "Errore: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // FIX JSON: Se è un errore di parsing JSON, la chiamata è comunque andata a buon fine!
                if (e.message?.contains("JSON", ignoreCase = true) == true ||
                    e.message?.contains("expected", ignoreCase = true) == true ||
                    e is java.io.EOFException) {

                    _richieste.value = _richieste.value.filter { it.id != idRichiesta }
                    Toast.makeText(getApplication(), "Richiesta accettata", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(getApplication(), "Errore di rete", Toast.LENGTH_SHORT).show()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rifiutaRichiesta(idRichiesta: String) {
        val context = getApplication<Application>().applicationContext
        val agenteId = SessionManager.getUserId(context)

        if (agenteId == null) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = EsitoRichiestaRequest(id = idRichiesta)
                // Usiamo l'endpoint che non richiede agenteId (o aggiungilo se lo richiede)
                val response = RetrofitClient.managerService.rifiutaRichiesta(request)

                if (response.isSuccessful) {
                    _richieste.value = _richieste.value.filter { it.id != idRichiesta }
                    Toast.makeText(getApplication(), "Richiesta rifiutata", Toast.LENGTH_SHORT).show()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Errore sconosciuto"
                    Toast.makeText(getApplication(), "Errore: $errorMsg", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // FIX JSON: Applichiamo la stessa protezione anche qui
                if (e.message?.contains("JSON", ignoreCase = true) == true ||
                    e.message?.contains("expected", ignoreCase = true) == true ||
                    e is java.io.EOFException) {

                    _richieste.value = _richieste.value.filter { it.id != idRichiesta }
                    Toast.makeText(getApplication(), "Richiesta rifiutata", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(getApplication(), "Errore di rete", Toast.LENGTH_SHORT).show()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}