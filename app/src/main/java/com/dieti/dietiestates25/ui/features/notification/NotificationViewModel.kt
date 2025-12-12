package com.dieti.dietiestates25.ui.features.notification

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.Appointment
import com.dieti.dietiestates25.data.model.AppointmentIconType
import com.dieti.dietiestates25.data.model.Notification
import com.dieti.dietiestates25.data.model.NotificationIconType
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    enum class NotificationTab {
        TUTTE, NON_LETTE
    }

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())

    private val _currentTab = MutableStateFlow(NotificationTab.TUTTE)
    val currentTab: StateFlow<NotificationTab> = _currentTab.asStateFlow()

    private val _isShowingAppointments = MutableStateFlow(false)
    val isShowingAppointments: StateFlow<Boolean> = _isShowingAppointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val filteredNotifications: StateFlow<List<Notification>> = combine(
        _notifications,
        _currentTab
    ) { notifications, tab ->
        when (tab) {
            NotificationTab.TUTTE -> notifications
            NotificationTab.NON_LETTE -> notifications.filter { !it.isRead }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    fun loadData(context: Context) {
        val userId = SessionManager.getUserId(context) ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Carica Notifiche
                try {
                    val notifResponse = RetrofitClient.instance.getUserNotifications(userId)
                    _notifications.value = notifResponse.map { dto ->
                        Notification(
                            id = dto.id,
                            title = dto.titolo,
                            senderType = "Sistema", // O mappato dal tipo se disponibile
                            message = dto.corpo ?: "Nessun contenuto",
                            iconType = NotificationIconType.BADGE, // Default
                            date = dto.data,
                            isRead = dto.letto, // 'letto' dal backend
                            isFavorite = false
                        )
                    }
                } catch (e: Exception) {
                    Log.e("NotificationVM", "Errore caricamento notifiche", e)
                }

                // Carica Appuntamenti
                try {
                    val apptResponse = RetrofitClient.instance.getUserAppointments(userId)
                    _appointments.value = apptResponse.map { dto ->
                        Appointment(
                            id = dto.id,
                            title = "Appuntamento",
                            description = null,
                            iconType = AppointmentIconType.MEETING,
                            date = dto.data,
                            status = mapStatus(dto.stato),
                            timeSlot = dto.ora,
                            clientName = null,
                            propertyAddress = null,
                            isFavorite = false,
                            notes = null,
                            propertyName = dto.titoloImmobile ?: "Immobile sconosciuto"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("NotificationVM", "Errore caricamento appuntamenti", e)
                }

            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mapStatus(backendStatus: String?): String {
        return when(backendStatus?.uppercase()) {
            "PROGRAMMATO", "SCHEDULED" -> "Programmato"
            "COMPLETATO", "COMPLETED" -> "Passato"
            "CANCELLATO", "CANCELLED" -> "Cancellato"
            else -> backendStatus ?: "Da confermare"
        }
    }

    fun setCurrentTab(tab: NotificationTab) {
        _currentTab.value = tab
    }

    fun toggleAppointmentsView() {
        _isShowingAppointments.value = !_isShowingAppointments.value
    }

    fun toggleFavorite(notificationId: Int) {
        // Questa funzione ora si aspetta un Int, ma gli ID sono String.
        // Necessario aggiornare la logica se si vuole supportare i preferiti localmente con String ID
    }

    // Overload per String ID
    fun toggleFavorite(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}