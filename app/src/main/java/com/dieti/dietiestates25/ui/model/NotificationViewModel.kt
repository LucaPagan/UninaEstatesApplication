// File: com/dieti/dietiestates25/ui/model/NotificationsViewModel.kt
package com.dieti.dietiestates25.ui.model // Assicurati che il package sia corretto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import kotlin.random.Random

class NotificationsViewModel : ViewModel() {
    enum class NotificationTab {
        RECENTI, VECCHIE, SALVATE
    }

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val allNotifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _currentTab = MutableStateFlow(NotificationTab.RECENTI)
    val currentTab: StateFlow<NotificationTab> = _currentTab.asStateFlow()

    private val _favoriteNotifications = MutableStateFlow<List<Notification>>(emptyList())

    val filteredNotifications: StateFlow<List<Notification>> =
        combine(_currentTab, _notifications, _favoriteNotifications) { tab, allNots, favNots ->
            val today = LocalDate.now()
            val fiveDaysAgo = today.minusDays(5)
            when (tab) {
                NotificationTab.RECENTI -> allNots
                    .filter { !it.date.isBefore(fiveDaysAgo) }
                    .sortedByDescending { it.date }
                NotificationTab.VECCHIE -> allNots
                    .sortedByDescending { it.date }
                NotificationTab.SALVATE -> favNots
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _isShowingAppointments = MutableStateFlow(false)
    val isShowingAppointments: StateFlow<Boolean> = _isShowingAppointments.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    init {
        loadInitialNotifications()
        loadInitialAppointments()
    }

    private fun loadInitialNotifications() {
        val today = LocalDate.now()
        val initialList = listOf(
            Notification(1, "Venditore", "C'è una proposta per te, affrettati a rispondere!", NotificationIconType.PHONE, today.minusDays(Random.nextInt(0, 3).toLong()), true),
            Notification(2, "Compratore", "Richiesta informazioni per l'immobile in Via Toledo.", NotificationIconType.PERSON, today.minusDays(Random.nextInt(1, 4).toLong()), false),
            Notification(3, "Agenzia", "Nuovo immobile disponibile nella tua zona di ricerca.", NotificationIconType.BADGE, today.minusDays(Random.nextInt(0, 2).toLong()), true),
            Notification(4, "Broker", "Visita confermata per domani alle 10:30.", NotificationIconType.PERSON, today.minusDays(Random.nextInt(4, 7).toLong()), false),
            Notification(5, "Sistema", "Aggiornamento importante della policy sulla privacy.", NotificationIconType.BADGE, today.minusDays(Random.nextInt(10, 20).toLong()), false),
            Notification(6, "Venditore", "Offerta ricevuta per l'appartamento in Via Roma.", NotificationIconType.PHONE, today, false),
            Notification(7, "Compratore", "Vorrei fissare un appuntamento.", NotificationIconType.PERSON, today.minusDays(6), false),
            Notification(8, "Agenzia", "Promozione estiva: sconti sulle commissioni!", NotificationIconType.BADGE, today.minusDays(10), false)
        )
        _notifications.value = initialList.sortedByDescending { it.date }
        updateFavorites()
    }

    fun setCurrentTab(tab: NotificationTab) {
        _currentTab.value = tab
    }

    fun toggleFavorite(notificationId: Int) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isFavorite = !it.isFavorite) else it
        }.sortedByDescending { it.date }
        updateFavorites()
    }

    private fun updateFavorites() {
        _favoriteNotifications.value = _notifications.value
            .filter { it.isFavorite }
            .sortedByDescending { it.date }
    }

    fun toggleAppointmentsView() {
        _isShowingAppointments.value = !_isShowingAppointments.value
    }

    private fun loadInitialAppointments() {
        val today = LocalDate.now()
        _appointments.value = listOf(
            Appointment(
                1,
                "Visita Via Toledo",
                "Cliente: Paolo Bianchi",
                AppointmentIconType.VISIT,
                today.plusDays(1),
                "Confermato",
                TimeSlots[0],
                clientName = "Paolo Bianchi",
                propertyAddress = "Via Toleso",
                isFavorite = false,
                notes = ""
            ),
            Appointment(2, "Incontro con Agenzia",
                "Discussione nuove proposte",
                AppointmentIconType.MEETING,
                today.plusDays(2),
                "Confermato",
                TimeSlots[2],
                clientName = "Paolo Bianchi",
                propertyAddress = "Via Toleso",
                isFavorite = false,
                notes = ""),
            Appointment(3, "Sopralluogo tecnico",
                "Viale Kennedy, 100",
                AppointmentIconType.GENERIC,
                today.plusDays(1),
                "Confermato",
                TimeSlots[1],
                clientName = "Paolo Bianchi",
                propertyAddress = "Via Toleso",
                isFavorite = false,
                notes = ""),
            Appointment(4, "Chiamata con Notaio",
                "Definizione contratto",
                AppointmentIconType.MEETING,
                today.plusDays(3),
                "Confermato",
                TimeSlots[3],
                clientName = "Paolo Bianchi",
                propertyAddress = "Via Toleso",
                isFavorite = false,
                notes = "")
        ).sortedBy { it.date }
    }
}