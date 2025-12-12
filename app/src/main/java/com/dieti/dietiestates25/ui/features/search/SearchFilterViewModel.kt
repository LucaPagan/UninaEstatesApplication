package com.dieti.dietiestates25.ui.features.search

import androidx.lifecycle.ViewModel
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.ui.components.PredefinedRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchFilterViewModel : ViewModel() {

    // --- Ranges ---
    val priceValueRange = 0f..2000000f
    val priceSteps = ((priceValueRange.endInclusive - priceValueRange.start) / 10000f - 1).toInt().coerceAtLeast(0)

    val surfaceValueRange = 0f..1000f
    val surfaceSteps = ((surfaceValueRange.endInclusive - surfaceValueRange.start) / 5f - 1).toInt().coerceAtLeast(0)

    // --- State ---
    private val _selectedPurchaseType = MutableStateFlow<String?>(null)
    val selectedPurchaseType = _selectedPurchaseType.asStateFlow()

    private val _minPriceText = MutableStateFlow("")
    val minPriceText = _minPriceText.asStateFlow()

    private val _maxPriceText = MutableStateFlow("")
    val maxPriceText = _maxPriceText.asStateFlow()

    private val _priceSliderPosition = MutableStateFlow(priceValueRange)
    val priceSliderPosition = _priceSliderPosition.asStateFlow()

    private val _minSurfaceText = MutableStateFlow("")
    val minSurfaceText = _minSurfaceText.asStateFlow()

    private val _maxSurfaceText = MutableStateFlow("")
    val maxSurfaceText = _maxSurfaceText.asStateFlow()

    private val _surfaceSliderPosition = MutableStateFlow(surfaceValueRange)
    val surfaceSliderPosition = _surfaceSliderPosition.asStateFlow()

    private val _minRooms = MutableStateFlow("")
    val minRooms = _minRooms.asStateFlow()

    private val _maxRooms = MutableStateFlow("")
    val maxRooms = _maxRooms.asStateFlow()

    private val _selectedBathrooms = MutableStateFlow<Int?>(null)
    val selectedBathrooms = _selectedBathrooms.asStateFlow()

    private val _selectedCondition = MutableStateFlow<String?>(null)
    val selectedCondition = _selectedCondition.asStateFlow()

    // --- Initialization ---
    fun initializeFilters(initialFilters: FilterModel?) {
        _selectedPurchaseType.value = initialFilters?.purchaseType
        
        _minPriceText.value = initialFilters?.minPrice?.toInt()?.toString() ?: ""
        _maxPriceText.value = initialFilters?.maxPrice?.toInt()?.toString() ?: ""
        _priceSliderPosition.value = (initialFilters?.minPrice ?: priceValueRange.start)..(initialFilters?.maxPrice ?: priceValueRange.endInclusive)

        _minSurfaceText.value = initialFilters?.minSurface?.toInt()?.toString() ?: ""
        _maxSurfaceText.value = initialFilters?.maxSurface?.toInt()?.toString() ?: ""
        _surfaceSliderPosition.value = (initialFilters?.minSurface ?: surfaceValueRange.start)..(initialFilters?.maxSurface ?: surfaceValueRange.endInclusive)

        _minRooms.value = initialFilters?.minRooms?.toString() ?: ""
        _maxRooms.value = initialFilters?.maxRooms?.toString() ?: ""

        _selectedBathrooms.value = initialFilters?.bathrooms
        _selectedCondition.value = initialFilters?.condition
    }

    // --- Actions ---

    fun onPurchaseTypeSelected(type: String?) {
        _selectedPurchaseType.value = type
    }

    // Price Logic
    fun onMinPriceChange(text: String) {
        _minPriceText.value = text
        updatePriceSliderFromText()
    }

    fun onMaxPriceChange(text: String) {
        _maxPriceText.value = text
        updatePriceSliderFromText()
    }

    fun onPriceSliderChange(range: ClosedFloatingPointRange<Float>) {
        _priceSliderPosition.value = range
        updatePriceTextFromSlider()
    }

    fun onPricePredefinedRangeSelected(range: PredefinedRange) {
        onPriceSliderChange(range.min..range.max)
    }

    private fun updatePriceSliderFromText() {
        val minP = _minPriceText.value.toFloatOrNull() ?: priceValueRange.start
        val maxP = _maxPriceText.value.toFloatOrNull() ?: priceValueRange.endInclusive
        if (minP <= maxP) {
            _priceSliderPosition.value = minP.coerceIn(priceValueRange)..maxP.coerceIn(priceValueRange)
        }
    }

    private fun updatePriceTextFromSlider() {
        val range = _priceSliderPosition.value
        // Aggiorna solo se diverso per evitare loop o comportamenti strani durante l'input
        // Qui semplifichiamo: aggiorniamo sempre il testo quando cambia lo slider
        // Nota: Questo potrebbe sovrascrivere l'input utente se non gestito con focus, 
        // ma nel ViewModel stiamo separando la logica.
        // Per replicare esattamente il comportamento della View originale (LaunchedEffect), 
        // l'aggiornamento deve essere fatto con cautela.
        
        val newMin = if (range.start <= priceValueRange.start) "" else range.start.toInt().toString()
        val newMax = if (range.endInclusive >= priceValueRange.endInclusive) "" else range.endInclusive.toInt().toString()
        
        // Evitiamo di sovrascrivere se l'utente sta digitando (controllo approssimativo)
        if (_minPriceText.value.toFloatOrNull() != range.start) _minPriceText.value = newMin
        if (_maxPriceText.value.toFloatOrNull() != range.endInclusive) _maxPriceText.value = newMax
    }


    // Surface Logic
    fun onMinSurfaceChange(text: String) {
        _minSurfaceText.value = text
        updateSurfaceSliderFromText()
    }

    fun onMaxSurfaceChange(text: String) {
        _maxSurfaceText.value = text
        updateSurfaceSliderFromText()
    }

    fun onSurfaceSliderChange(range: ClosedFloatingPointRange<Float>) {
        _surfaceSliderPosition.value = range
        updateSurfaceTextFromSlider()
    }

    fun onSurfacePredefinedRangeSelected(range: PredefinedRange) {
        onSurfaceSliderChange(range.min..range.max)
    }

    private fun updateSurfaceSliderFromText() {
        val minS = _minSurfaceText.value.toFloatOrNull() ?: surfaceValueRange.start
        val maxS = _maxSurfaceText.value.toFloatOrNull() ?: surfaceValueRange.endInclusive
        if (minS <= maxS) {
            _surfaceSliderPosition.value = minS.coerceIn(surfaceValueRange)..maxS.coerceIn(surfaceValueRange)
        }
    }

    private fun updateSurfaceTextFromSlider() {
        val range = _surfaceSliderPosition.value
        val newMin = if (range.start <= surfaceValueRange.start) "" else range.start.toInt().toString()
        val newMax = if (range.endInclusive >= surfaceValueRange.endInclusive) "" else range.endInclusive.toInt().toString()

        if (_minSurfaceText.value.toFloatOrNull() != range.start) _minSurfaceText.value = newMin
        if (_maxSurfaceText.value.toFloatOrNull() != range.endInclusive) _maxSurfaceText.value = newMax
    }

    // Rooms & Bathrooms
    fun onMinRoomsChange(text: String) { _minRooms.value = text.filter { it.isDigit() } }
    fun onMaxRoomsChange(text: String) { _maxRooms.value = text.filter { it.isDigit() } }

    fun onBathroomsSelected(count: Int) {
        _selectedBathrooms.value = if (_selectedBathrooms.value == count) null else count
    }

    // Condition
    fun onConditionSelected(condition: String) {
        _selectedCondition.value = if (_selectedCondition.value == condition) null else condition
    }

    // Reset & Apply
    fun resetAllFilters() {
        _selectedPurchaseType.value = null
        _selectedBathrooms.value = null
        _selectedCondition.value = null
        _minPriceText.value = ""
        _maxPriceText.value = ""
        _priceSliderPosition.value = priceValueRange
        _minSurfaceText.value = ""
        _maxSurfaceText.value = ""
        _surfaceSliderPosition.value = surfaceValueRange
        _minRooms.value = ""
        _maxRooms.value = ""
    }

    fun getAppliedFilters(): FilterModel {
        val finalMinPrice = when {
            _minPriceText.value.isNotBlank() -> _minPriceText.value.toFloatOrNull()
            _priceSliderPosition.value.start > priceValueRange.start -> _priceSliderPosition.value.start
            else -> null
        }
        val finalMaxPrice = when {
            _maxPriceText.value.isNotBlank() -> _maxPriceText.value.toFloatOrNull()
            _priceSliderPosition.value.endInclusive < priceValueRange.endInclusive -> _priceSliderPosition.value.endInclusive
            else -> null
        }
        val finalMinSurface = when {
            _minSurfaceText.value.isNotBlank() -> _minSurfaceText.value.toFloatOrNull()
            _surfaceSliderPosition.value.start > surfaceValueRange.start -> _surfaceSliderPosition.value.start
            else -> null
        }
        val finalMaxSurface = when {
            _maxSurfaceText.value.isNotBlank() -> _maxSurfaceText.value.toFloatOrNull()
            _surfaceSliderPosition.value.endInclusive < surfaceValueRange.endInclusive -> _surfaceSliderPosition.value.endInclusive
            else -> null
        }

        return FilterModel(
            purchaseType = _selectedPurchaseType.value,
            minPrice = finalMinPrice,
            maxPrice = finalMaxPrice,
            minSurface = finalMinSurface,
            maxSurface = finalMaxSurface,
            minRooms = _minRooms.value.toIntOrNull(),
            maxRooms = _maxRooms.value.toIntOrNull(),
            bathrooms = _selectedBathrooms.value,
            condition = _selectedCondition.value
        )
    }

    fun hasActiveFilters(): Boolean {
        return _selectedPurchaseType.value != null ||
                _minPriceText.value.isNotBlank() || _maxPriceText.value.isNotBlank() ||
                _minSurfaceText.value.isNotBlank() || _maxSurfaceText.value.isNotBlank() ||
                _minRooms.value.isNotBlank() || _maxRooms.value.isNotBlank() ||
                _selectedBathrooms.value != null ||
                _selectedCondition.value != null ||
                _priceSliderPosition.value.start > priceValueRange.start ||
                _priceSliderPosition.value.endInclusive < priceValueRange.endInclusive ||
                _surfaceSliderPosition.value.start > surfaceValueRange.start ||
                _surfaceSliderPosition.value.endInclusive < surfaceValueRange.endInclusive
    }
}