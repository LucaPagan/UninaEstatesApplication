package com.dieti.dietiestates25.ui.features.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.LoginRequest
import com.dieti.dietiestates25.data.remote.RegisterRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(email: String, pass: String, context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    SessionManager.saveUserSession(context, body.userId, body.nome)
                    _authState.value = AuthState.Success(body.userId)
                } else {
                    _authState.value = AuthState.Error("Credenziali non valide")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore di rete: ${e.message}")
            }
        }
    }

    fun register(req: RegisterRequest, context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.register(req)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    SessionManager.saveUserSession(context, body.userId, body.nome)
                    _authState.value = AuthState.Success(body.userId)
                } else {
                    _authState.value = AuthState.Error("Registrazione fallita")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Errore: ${e.message}")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}