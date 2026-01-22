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
    object Loading : HomeUiState()
    data class Success(val immobili: List<ImmobileDTO>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

// Cambiato in AndroidViewModel per avere accesso al Context (Application)
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
            // 1. Ripristina la sessione se persa (es. dopo riavvio processo)
            if (RetrofitClient.loggedUserEmail == null) {
                val savedEmail = userPrefs.userEmail.first()
                if (savedEmail != null) {
                    RetrofitClient.loggedUserEmail = savedEmail
                    Log.d("HomeViewModel", "Sessione ripristinata: $savedEmail")
                }
            }

            // 2. Ora scarica i dati
            fetchImmobili()
        }
    }

    fun fetchImmobili() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val response = api.getImmobili()
                _uiState.value = HomeUiState.Success(response)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Errore fetch immobili", e)
                val msg = if (e.message?.contains("ECONNREFUSED") == true) {
                    "Impossibile connettersi al Server. Verifica che il backend sia avviato."
                } else {
                    "Errore: ${e.message}"
                }
                _uiState.value = HomeUiState.Error(msg)
            }
        }
    }
}