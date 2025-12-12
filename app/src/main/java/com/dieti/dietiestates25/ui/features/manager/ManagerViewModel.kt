package com.dieti.dietiestates25.ui.features.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.Appointment
import com.dieti.dietiestates25.data.model.AppointmentIconType
import com.dieti.dietiestates25.data.remote.AgenziaDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.time.LocalDate

class ManagerViewModel : ViewModel() {

    enum class ManagerTab {
        OFFERS, APPOINTMENTS, REPORTS
    }

    private val _currentTab = MutableStateFlow(ManagerTab.OFFERS)
    val currentTab: StateFlow<ManagerTab> = _currentTab.asStateFlow()

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers: StateFlow<List<Offer>> = _offers.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    private val _selectedItem = MutableStateFlow<Any?>(null)
    val selectedItem: StateFlow<Any?> = _selectedItem.asStateFlow()

    val filteredItems: StateFlow<List<Any>> =
        combine(_currentTab, _offers, _appointments, _reports) { tab, offers, appointments, reports ->
            when (tab) {
                ManagerTab.OFFERS -> offers
                ManagerTab.APPOINTMENTS -> appointments
                ManagerTab.REPORTS -> reports
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        loadInitialData()
    }

    private val _agenzie = MutableStateFlow<List<AgenziaDTO>>(emptyList())
    val agenzie = _agenzie.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchAgenzie()
    }

    fun fetchAgenzie() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Recupera la lista delle agenzie dal backend
                val result = RetrofitClient.instance.getAllAgenzie()
                _agenzie.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                _agenzie.value = emptyList() // Gestione errore base
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _offers.value = listOf(
                Offer(1, "Mario Rossi", 120000.0, "Via Roma 10", LocalDate.now().minusDays(1), "In attesa"),
                Offer(2, "Giulia Verdi", 118500.0, "Via Milano 22", LocalDate.now(), "Accettata"),
                Offer(3, "Luca Bianchi", 121000.0, "Via Napoli 5", LocalDate.now().minusDays(2), "Rifiutata")
            )

            _appointments.value = listOf(
                Appointment(
                    "1",
                    "Visita",
                    "Appartamento in Via Firenze 7",
                    AppointmentIconType.VISIT,
                    "2025, 9, 3",
                    "Conferma in attesa",
                    "9-12",
                    "Mario Rossi",
                    "Via Roma 10",
                    false,
                    "",
                    propertyName = "Appartamento in Via Firenze 7"
                ),
                Appointment(
                    "2",
                    "Appuntamento",
                    "Appartamento in Via Torino 21",
                    AppointmentIconType.MEETING,
                    "2025, 9, 5",
                    "Confermato",
                    timeSlot = "12-14",
                    clientName = "Giulia Verdi",
                    propertyAddress = "Via Milano 22",
                    isFavorite = true,
                    notes = "",
                    propertyName = "Appartamento in Via Torino 21"
                )
            )

            _reports.value = listOf(
                Report(1, "Report vendite Agosto 2025", "Analisi vendite immobiliari",
                    LocalDate.of(2025, 8, 31), "Vendite in crescita del 12%"),
                Report(2, "Report manutenzione Q2 2025", "Controlli e riparazioni completati",
                    LocalDate.of(2025, 6, 30), "Tutte le manutenzioni completate in tempo")
            )
        }
    }

    fun setCurrentTab(tab: ManagerTab) {
        _currentTab.value = tab
    }

    fun setSelectedItem(item: Any?) {
        _selectedItem.value = item
    }
}

// --- Models ---
data class Offer(
    val id: Int,
    val buyerName: String,
    val price: Double,
    val propertyAddress: String,
    val date: LocalDate,
    val status: String
)

data class Report(
    val id: Int,
    val title: String,
    val description: String,
    val date: LocalDate,
    val summary: String
)