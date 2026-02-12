package com.dieti.dietiestates25.ui.features.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.NotificaDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.TrattativaSummaryDTO
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val _generalNotifications = MutableStateFlow<List<NotificaDTO>>(emptyList())
    val generalNotifications = _generalNotifications.asStateFlow()

    private val _negotiations = MutableStateFlow<List<TrattativaSummaryDTO>>(emptyList())
    val negotiations = _negotiations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadData() {
        val context = getApplication<Application>().applicationContext
        val userId = SessionManager.getUserId(context) ?: return

        viewModelScope.launch {
            _isLoading.value = true

            // 1. Caricamento Notifiche Generali (Indipendente)
            try {
                val notifResponse = RetrofitClient.notificationService.getNotificheUtente(userId)
                if (notifResponse.isSuccessful) {
                    _generalNotifications.value = notifResponse.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Ignoriamo l'errore qui per non fermare il caricamento delle offerte
            }

            // 2. Caricamento Offerte/Trattative (Indipendente)
            try {
                val offerResponse = RetrofitClient.notificationService.getTrattativeUtente(userId)
                if (offerResponse.isSuccessful) {
                    _negotiations.value = offerResponse.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}