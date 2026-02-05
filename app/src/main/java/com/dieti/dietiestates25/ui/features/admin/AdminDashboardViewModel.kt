package com.dieti.dietiestates25.ui.features.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminDashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val userPrefs = UserPreferences(application)

    init {
        restoreSession()
    }

    private fun restoreSession() {
        viewModelScope.launch {
            // Se l'header è vuoto (es. dopo riavvio app), lo recuperiamo dal DataStore
            if (RetrofitClient.loggedUserEmail == null) {
                val savedEmail = userPrefs.userEmail.first()
                if (savedEmail != null) {
                    RetrofitClient.loggedUserEmail = savedEmail
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearUser()
            RetrofitClient.loggedUserEmail = null
        }
    }
}