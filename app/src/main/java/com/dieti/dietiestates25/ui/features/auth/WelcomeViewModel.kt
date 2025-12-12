package com.dieti.dietiestates25.ui.features.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WelcomeViewModel : ViewModel() {
    
    // Controlla se c'è una sessione attiva
    fun checkSession(context: Context): String? {
        return SessionManager.getUserId(context)
    }
}