package com.dieti.dietiestates25.ui.features.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.AdminApiService
import com.dieti.dietiestates25.data.remote.AdminOptionDTO
import com.dieti.dietiestates25.data.remote.AgenziaOptionDTO
import com.dieti.dietiestates25.data.remote.ChangeMyPasswordRequest
import com.dieti.dietiestates25.data.remote.CreateAdminRequest
import com.dieti.dietiestates25.data.remote.CreateAgenteRequest
import com.dieti.dietiestates25.data.remote.CreateAgenziaRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminOperationState {
    data object Idle : AdminOperationState()
    data object Loading : AdminOperationState()
    data class Success(val message: String) : AdminOperationState()
    data class Error(val message: String) : AdminOperationState()
}

// Torniamo a ViewModel standard (non serve più AndroidViewModel per il Context)
class AdminScreenViewModel : ViewModel() {

    private val api: AdminApiService = RetrofitClient.retrofit.create(AdminApiService::class.java)

    private val _operationState = MutableStateFlow<AdminOperationState>(AdminOperationState.Idle)
    val operationState = _operationState.asStateFlow()

    private val _agenzieOptions = MutableStateFlow<List<AgenziaOptionDTO>>(emptyList())
    val agenzieOptions = _agenzieOptions.asStateFlow()

    private val _adminOptions = MutableStateFlow<List<AdminOptionDTO>>(emptyList())
    val adminOptions = _adminOptions.asStateFlow()

    fun resetState() {
        _operationState.value = AdminOperationState.Idle
    }

    // --- FETCH DATA ---
    fun fetchAgencies() {
        viewModelScope.launch {
            try { _agenzieOptions.value = api.getAgenciesOptions() } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun fetchAdministrators() {
        viewModelScope.launch {
            try { _adminOptions.value = api.getAdministratorsOptions() } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // --- CREATE AGENZIA (Senza Geocoding Client-Side) ---
    fun createAgency(nome: String, indirizzo: String, adminId: String) {
        viewModelScope.launch {
            _operationState.value = AdminOperationState.Loading

            if (adminId.isBlank()) {
                _operationState.value = AdminOperationState.Error("Seleziona un Amministratore")
                return@launch
            }

            try {
                // Inviamo solo l'indirizzo testuale, il backend penserà alle coordinate
                val request = CreateAgenziaRequest(
                    nome = nome,
                    indirizzo = indirizzo,
                    adminId = adminId
                )

                val response = api.createAgency(request)
                handleResponse(response, "Agenzia creata con successo!")
                fetchAgencies() // Refresh
            } catch (e: Exception) { handleError(e) }
        }
    }

    fun createAgent(nome: String, cognome: String, email: String, pass: String, agenziaId: String, isCapo: Boolean) {
        viewModelScope.launch {
            _operationState.value = AdminOperationState.Loading
            try {
                if (agenziaId.isBlank()) {
                    _operationState.value = AdminOperationState.Error("Seleziona un'agenzia")
                    return@launch
                }
                val response = api.createAgent(
                    CreateAgenteRequest(
                        nome,
                        cognome,
                        email,
                        pass,
                        agenziaId,
                        isCapo
                    )
                )
                handleResponse(response, "Agente creato!")
            } catch (e: Exception) { handleError(e) }
        }
    }

    fun createAdmin(email: String, pass: String) {
        viewModelScope.launch {
            _operationState.value = AdminOperationState.Loading
            try {
                val response = api.createAdmin(CreateAdminRequest(email, pass))
                handleResponse(response, "Amministratore creato!")
                fetchAdministrators() // Refresh
            } catch (e: Exception) { handleError(e) }
        }
    }

    fun changeMyPassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            _operationState.value = AdminOperationState.Loading
            try {
                val response = api.changeMyPassword(ChangeMyPasswordRequest(oldPass, newPass))
                handleResponse(response, "Password aggiornata!")
            } catch (e: Exception) { handleError(e) }
        }
    }

    private fun handleResponse(response: retrofit2.Response<Any>, successMsg: String) {
        if (response.isSuccessful) {
            _operationState.value = AdminOperationState.Success(successMsg)
        } else {
            val error = response.errorBody()?.string() ?: "Errore ${response.code()}"
            _operationState.value = AdminOperationState.Error(error)
        }
    }

    private fun handleError(e: Exception) {
        _operationState.value = AdminOperationState.Error("Errore rete: ${e.message}")
    }
}