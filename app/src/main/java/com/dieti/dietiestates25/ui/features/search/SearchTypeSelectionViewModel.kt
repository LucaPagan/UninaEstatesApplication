package com.dieti.dietiestates25.ui.features.search

import androidx.lifecycle.ViewModel
import com.dieti.dietiestates25.data.model.FilterModel

class SearchTypeSelectionViewModel : ViewModel() {
    // Logica semplice: al momento non serve mantenere stato complesso qui,
    // ma è predisposto per espansioni future (es. salvare ultima modalità di visualizzazione scelta)
    
    fun onSelectionMade(mode: String) {
        // Esempio: Analytics track "ViewModeSelected: $mode"
    }
}