package com.dieti.dietiestates25.ui.features.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.features.property.PropertyApiService
import com.dieti.dietiestates25.ui.features.search.SearchApiService
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val immobili: List<ImmobileDTO>,
        val ricercheRecenti: List<String> = emptyList()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val PropertyApi = RetrofitClient.retrofit.create(PropertyApiService::class.java)
    private val SearchApi = RetrofitClient.retrofit.create(SearchApiService::class.java)

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            // 1. Ripristino robusto (Usa getUserEmail)
            checkAndRestoreSession()

            // 2. Fetch
            fetchDataInternal()
        }
    }

    private suspend fun fetchDataInternal() {
        try {
            // FIX 403: Gestione token scaduto
            val immobiliResponse = try {
                PropertyApi.getImmobili()
            } catch (e: HttpException) {
                if (e.code() == 403 || e.code() == 401) {
                    Log.w("HomeVM", "Token invalido (${e.code()}). Riprovo da anonimo.")
                    RetrofitClient.authToken = null
                    RetrofitClient.loggedUserEmail = null
                    PropertyApi.getImmobili()
                } else {
                    throw e
                }
            }

            // Fetch ricerche recenti (solo se il token è valido)
            val ricercheResponse = try {
                if (RetrofitClient.authToken != null) {
                    SearchApi.getRicercheRecenti()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                // Ignora errori sulla cronologia, non bloccanti per la home
                emptyList()
            }

            _uiState.value = HomeUiState.Success(
                immobili = immobiliResponse,
                ricercheRecenti = ricercheResponse
            )
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Errore fetch dati", e)
            val msg = if (e.message?.contains("ECONNREFUSED") == true) {
                "Impossibile connettersi al Server."
            } else {
                "Errore: ${e.message}"
            }
            _uiState.value = HomeUiState.Error(msg)
        }
    }

    private fun checkAndRestoreSession() {
        // Se RetrofitClient non è configurato, proviamo a ripristinarlo dal SessionManager
        if (RetrofitClient.loggedUserEmail == null || RetrofitClient.authToken == null) {
            val context = getApplication<Application>().applicationContext

            val savedToken = SessionManager.getAuthToken(context)
            val savedEmail = SessionManager.getUserEmail(context) // FIX: Uso getUserEmail

            if (savedEmail != null && savedToken != null) {
                RetrofitClient.loggedUserEmail = savedEmail
                RetrofitClient.authToken = savedToken
                Log.d("HomeViewModel", "Sessione ripristinata correttamente: $savedEmail")
            }
        }
    }

    fun getImmobileMainImageUrl(immobile: ImmobileDTO): String? {
        val relativeUrl = immobile.immagini.firstOrNull()?.url ?: return null
        return RetrofitClient.getFullUrl(relativeUrl)
    }
}