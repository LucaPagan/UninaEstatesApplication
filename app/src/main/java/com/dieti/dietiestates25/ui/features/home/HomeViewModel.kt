package com.dieti.dietiestates25.ui.features.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.DietiEstatesApi
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val immobili: List<ImmobileDTO>,
        val ricercheRecenti: List<String> = emptyList() // Aggiunto campo per le ricerche
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val api: DietiEstatesApi = RetrofitClient.retrofit.create(DietiEstatesApi::class.java)
    private val userPrefs = UserPreferences(application)

    init {
        initializeAndFetch()
    }

    private fun initializeAndFetch() {
        viewModelScope.launch {
            if (RetrofitClient.loggedUserEmail == null) {
                val savedEmail = userPrefs.userEmail.first()
                if (savedEmail != null) {
                    RetrofitClient.loggedUserEmail = savedEmail
                }
            }
            fetchData()
        }
    }

    fun fetchImmobili() {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Eseguiamo le chiamate in parallelo o sequenza rapida
                val immobiliResponse = api.getImmobili()

                // Fetch ricerche recenti (solo se loggato, ma l'API gestisce lato server l'auth)
                val ricercheResponse = try {
                    if (RetrofitClient.loggedUserEmail != null) {
                        api.getRicercheRecenti()
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    Log.e("HomeVM", "Errore cronologia", e)
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
    }

    fun getImmobileMainImageUrl(immobile: ImmobileDTO): String? {
        val relativeUrl = immobile.immagini.firstOrNull()?.url ?: return null
        return RetrofitClient.getFullUrl(relativeUrl)
    }
}