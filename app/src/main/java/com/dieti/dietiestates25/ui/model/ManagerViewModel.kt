package com.dieti.dietiestates25.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.ui.screen.ManagerTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ManagerViewModel : ViewModel() {

    private val _currentTab = MutableStateFlow(ManagerTab.OFFERS)
    val currentTab: StateFlow<ManagerTab> = _currentTab

    private val _offers = MutableStateFlow<List<String>>(emptyList())
    val offers: StateFlow<List<String>> = _offers

    private val _appointments = MutableStateFlow<List<String>>(emptyList())
    val appointments: StateFlow<List<String>> = _appointments

    private val _reports = MutableStateFlow<List<String>>(emptyList())
    val reports: StateFlow<List<String>> = _reports

    init {

        loadInitialData()
    }
    private fun loadInitialData() {

        viewModelScope.launch {
            _offers.value = listOf(
                "Offerta 1",
                "Offerta 2",
                "Offerta 3"
            )
            _appointments.value = listOf(
                "Appuntamento del 25/12",
                "Appuntamento del 30/12"
            )
            _reports.value = listOf(
                "Report Q1 2025",
                "Report Q2 2025"
            )
        }
    }

    fun setCurrentTab(tab: ManagerTab) {
        _currentTab.value = tab
    }
}

