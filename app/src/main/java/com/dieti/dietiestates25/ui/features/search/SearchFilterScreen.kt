package com.dieti.dietiestates25.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.components.FilterSection
import com.dieti.dietiestates25.ui.components.PredefinedRange
import com.dieti.dietiestates25.ui.components.RangeFilterInput
import com.dieti.dietiestates25.ui.components.SelectableOptionButton
import com.dieti.dietiestates25.ui.components.SingleChoiceToggleGroup
import com.dieti.dietiestates25.ui.components.defaultOutlineTextFieldColors
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.model.FilterOriginScreen
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun SearchFilterScreen(
    navController: NavController,
    idUtente: String = "utente",
    comune: String = "napoli",
    ricercaQueryText: String,
    initialFilters: FilterModel? = null,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onApplyFilters: (FilterModel) -> Unit,
    isFullScreenContext: Boolean = true,
    originScreen: FilterOriginScreen? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val focusManager = LocalFocusManager.current

    // Inizializza gli stati
    var selectedPurchaseType by remember { mutableStateOf(initialFilters?.purchaseType) }
    val purchaseOptions = listOf("Compra", "Affitta")

    val priceValueRange = 0f..2000000f
    val priceSteps = ((priceValueRange.endInclusive - priceValueRange.start) / 10000f - 1).toInt().coerceAtLeast(0)

    var minPriceText by remember { mutableStateOf(initialFilters?.minPrice?.toInt()?.toString() ?: "") }
    var maxPriceText by remember { mutableStateOf(initialFilters?.maxPrice?.toInt()?.toString() ?: "") }
    var priceSliderPosition by remember {
        mutableStateOf((initialFilters?.minPrice ?: priceValueRange.start)..(initialFilters?.maxPrice ?: priceValueRange.endInclusive))
    }

    val surfaceValueRange = 0f..1000f
    val surfaceSteps = ((surfaceValueRange.endInclusive - surfaceValueRange.start) / 5f - 1).toInt().coerceAtLeast(0)

    var minSurfaceText by remember { mutableStateOf(initialFilters?.minSurface?.toInt()?.toString() ?: "") }
    var maxSurfaceText by remember { mutableStateOf(initialFilters?.maxSurface?.toInt()?.toString() ?: "") }
    var surfaceSliderPosition by remember {
        mutableStateOf((initialFilters?.minSurface ?: surfaceValueRange.start)..(initialFilters?.maxSurface ?: surfaceValueRange.endInclusive))
    }

    var minRooms by remember { mutableStateOf(initialFilters?.minRooms?.toString() ?: "") }
    var maxRooms by remember { mutableStateOf(initialFilters?.maxRooms?.toString() ?: "") }

    var selectedBathrooms by remember { mutableStateOf(initialFilters?.bathrooms) }
    val bathroomOptions = listOf(1, 2, 3)

    var selectedCondition by remember { mutableStateOf(initialFilters?.condition) }
    val conditionOptionsFirstRow = listOf("Nuovo", "Ottimo")
    val conditionOptionsSecondRow = listOf("Buono", "Da ristrutturare")

    // --- Logica Slider/Text Sync ---
    LaunchedEffect(priceSliderPosition) {
        if (minPriceText.isEmpty() || minPriceText.toFloatOrNull() != priceSliderPosition.start) {
            minPriceText = if (priceSliderPosition.start <= priceValueRange.start) "" else priceSliderPosition.start.toInt().toString()
        }
        if (maxPriceText.isEmpty() || maxPriceText.toFloatOrNull() != priceSliderPosition.endInclusive) {
            maxPriceText = if (priceSliderPosition.endInclusive >= priceValueRange.endInclusive) "" else priceSliderPosition.endInclusive.toInt().toString()
        }
    }
    fun updatePriceSliderFromText() {
        val minP = minPriceText.toFloatOrNull() ?: priceValueRange.start
        val maxP = maxPriceText.toFloatOrNull() ?: priceValueRange.endInclusive
        if (minP <= maxP) priceSliderPosition = minP.coerceIn(priceValueRange)..maxP.coerceIn(priceValueRange)
    }

    LaunchedEffect(surfaceSliderPosition) {
        if (minSurfaceText.isEmpty() || minSurfaceText.toFloatOrNull() != surfaceSliderPosition.start) {
            minSurfaceText = if (surfaceSliderPosition.start <= surfaceValueRange.start) "" else surfaceSliderPosition.start.toInt().toString()
        }
        if (maxSurfaceText.isEmpty() || maxSurfaceText.toFloatOrNull() != surfaceSliderPosition.endInclusive) {
            maxSurfaceText = if (surfaceSliderPosition.endInclusive >= surfaceValueRange.endInclusive) "" else surfaceSliderPosition.endInclusive.toInt().toString()
        }
    }
    fun updateSurfaceSliderFromText() {
        val minS = minSurfaceText.toFloatOrNull() ?: surfaceValueRange.start
        val maxS = maxSurfaceText.toFloatOrNull() ?: surfaceValueRange.endInclusive
        if (minS <= maxS) surfaceSliderPosition = minS.coerceIn(surfaceValueRange)..maxS.coerceIn(surfaceValueRange)
    }

    val predefinedPriceRanges = remember(priceValueRange.endInclusive) {
        listOf(
            PredefinedRange("€0-100k", 0f, 100000f), PredefinedRange("€100k-250k", 100000f, 250000f),
            PredefinedRange("€250k-500k", 250000f, 500000f), PredefinedRange(">€500k", 500000f, priceValueRange.endInclusive)
        )
    }
    val predefinedSurfaceRanges = remember(surfaceValueRange.endInclusive) {
        listOf(
            PredefinedRange("0-50mq", 0f, 50f), PredefinedRange("50-100mq", 50f, 100f),
            PredefinedRange("100-150mq", 100f, 150f), PredefinedRange(">150mq", 150f, surfaceValueRange.endInclusive)
        )
    }

    fun hasActiveFilters(): Boolean {
        return selectedPurchaseType != null || minPriceText.isNotBlank() || maxPriceText.isNotBlank() ||
                minSurfaceText.isNotBlank() || maxSurfaceText.isNotBlank() || minRooms.isNotBlank() || maxRooms.isNotBlank() ||
                selectedBathrooms != null || selectedCondition != null ||
                priceSliderPosition.start > priceValueRange.start || priceSliderPosition.endInclusive < priceValueRange.endInclusive ||
                surfaceSliderPosition.start > surfaceValueRange.start || surfaceSliderPosition.endInclusive < surfaceValueRange.endInclusive
    }

    fun resetAllFilters() {
        selectedPurchaseType = null; selectedBathrooms = null; selectedCondition = null
        minPriceText = ""; maxPriceText = ""; priceSliderPosition = priceValueRange.start..priceValueRange.endInclusive
        minSurfaceText = ""; maxSurfaceText = ""; surfaceSliderPosition = surfaceValueRange.start..surfaceValueRange.endInclusive
        minRooms = ""; maxRooms = ""
    }

    // FIX CRITICO PER IL GIALLO:
    // Impostiamo esplicitamente 'surface' (Bianco nel tuo tema)
    // E, FONDAMENTALE, elevazione a 0.dp per impedire il mix con surfaceTint (Giallo).
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tasto Indietro/Chiudi
                if (isFullScreenContext) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = colorScheme.onBackground
                        )
                    }
                } else {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi",
                            tint = colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "FILTRI RICERCA",
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onSurface
                )

                // Tasto RESET
                if (hasActiveFilters()) {
                    TextButton(onClick = { resetAllFilters() }) {
                        Text(
                            "RESET",
                            style = typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            HorizontalDivider(color = colorScheme.outlineVariant.copy(alpha = 0.5f))

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall)
                ) {
                    // SEZIONI FILTRI
                    FilterSection(
                        title = "Tipo di Transazione",
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    ) {
                        SingleChoiceToggleGroup(
                            options = purchaseOptions,
                            selectedOption = selectedPurchaseType,
                            onOptionSelected = { selectedPurchaseType = it },
                            dimensions = dimensions,
                            typography = typography,
                            colorScheme = colorScheme
                        )
                    }

                    RangeFilterInput(
                        title = "Prezzo",
                        minTextFieldValue = minPriceText,
                        onMinTextFieldChange = { minPriceText = it; updatePriceSliderFromText() },
                        maxTextFieldValue = maxPriceText,
                        onMaxTextFieldChange = { maxPriceText = it; updatePriceSliderFromText() },
                        sliderPosition = priceSliderPosition,
                        onSliderPositionChange = { priceSliderPosition = it },
                        valueRange = priceValueRange,
                        predefinedRanges = predefinedPriceRanges,
                        onPredefinedRangeSelected = { priceSliderPosition = it.min..it.max },
                        unitSuffix = "€",
                        steps = priceSteps,
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    )

                    RangeFilterInput(
                        title = "Superficie",
                        minTextFieldValue = minSurfaceText,
                        onMinTextFieldChange = { minSurfaceText = it; updateSurfaceSliderFromText() },
                        maxTextFieldValue = maxSurfaceText,
                        onMaxTextFieldChange = { maxSurfaceText = it; updateSurfaceSliderFromText() },
                        sliderPosition = surfaceSliderPosition,
                        onSliderPositionChange = { surfaceSliderPosition = it },
                        valueRange = surfaceValueRange,
                        predefinedRanges = predefinedSurfaceRanges,
                        onPredefinedRangeSelected = { surfaceSliderPosition = it.min..it.max },
                        unitSuffix = "mq",
                        steps = surfaceSteps,
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    )

                    FilterSection(
                        title = "Locali",
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)) {
                            OutlinedTextField(
                                value = minRooms,
                                onValueChange = { minRooms = it.filter { c -> c.isDigit() } },
                                label = { Text("Min.") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(dimensions.spacingSmall),
                                colors = defaultOutlineTextFieldColors(colorScheme, typography),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = maxRooms,
                                onValueChange = { maxRooms = it.filter { c -> c.isDigit() } },
                                label = { Text("Max.") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(dimensions.spacingSmall),
                                colors = defaultOutlineTextFieldColors(colorScheme, typography),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }

                    FilterSection(
                        title = "Bagni",
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                            bathroomOptions.forEach { count ->
                                SelectableOptionButton(
                                    text = count.toString(),
                                    isSelected = selectedBathrooms == count,
                                    onClick = { selectedBathrooms = if (selectedBathrooms == count) null else count },
                                    modifier = Modifier.weight(1f),
                                    dimensions = dimensions,
                                    typography = typography,
                                    colorScheme = colorScheme
                                )
                            }
                            SelectableOptionButton(
                                text = ">3",
                                isSelected = selectedBathrooms == 4,
                                onClick = { selectedBathrooms = if (selectedBathrooms == 4) null else 4 },
                                modifier = Modifier.weight(1f),
                                dimensions = dimensions,
                                typography = typography,
                                colorScheme = colorScheme
                            )
                        }
                    }

                    FilterSection(
                        title = "Stato Immobile",
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                                conditionOptionsFirstRow.forEach { c ->
                                    SelectableOptionButton(
                                        text = c,
                                        isSelected = selectedCondition == c,
                                        onClick = { selectedCondition = if (selectedCondition == c) null else c },
                                        modifier = Modifier.weight(1f),
                                        dimensions = dimensions,
                                        typography = typography,
                                        colorScheme = colorScheme
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                                conditionOptionsSecondRow.forEach { c ->
                                    SelectableOptionButton(
                                        text = c,
                                        isSelected = selectedCondition == c,
                                        onClick = { selectedCondition = if (selectedCondition == c) null else c },
                                        modifier = Modifier.weight(1f),
                                        dimensions = dimensions,
                                        typography = typography,
                                        colorScheme = colorScheme
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(dimensions.paddingLarge))
                }
            }

            // Bottom Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp, // Importante anche qui
                shadowElevation = dimensions.elevationMedium
            ) {
                AppSecondaryButton(
                    text = "Mostra risultati",
                    onClick = {
                        val finalMinPrice =
                            if (minPriceText.isNotBlank()) minPriceText.toFloatOrNull() else if (priceSliderPosition.start > priceValueRange.start) priceSliderPosition.start else null
                        val finalMaxPrice =
                            if (maxPriceText.isNotBlank()) maxPriceText.toFloatOrNull() else if (priceSliderPosition.endInclusive < priceValueRange.endInclusive) priceSliderPosition.endInclusive else null
                        val finalMinSurface =
                            if (minSurfaceText.isNotBlank()) minSurfaceText.toFloatOrNull() else if (surfaceSliderPosition.start > surfaceValueRange.start) surfaceSliderPosition.start else null
                        val finalMaxSurface =
                            if (maxSurfaceText.isNotBlank()) maxSurfaceText.toFloatOrNull() else if (surfaceSliderPosition.endInclusive < surfaceValueRange.endInclusive) surfaceSliderPosition.endInclusive else null

                        val appliedFilters = FilterModel(
                            purchaseType = selectedPurchaseType,
                            minPrice = finalMinPrice, maxPrice = finalMaxPrice,
                            minSurface = finalMinSurface, maxSurface = finalMaxSurface,
                            minRooms = minRooms.toIntOrNull(), maxRooms = maxRooms.toIntOrNull(),
                            bathrooms = selectedBathrooms, condition = selectedCondition
                        )

                        onApplyFilters(appliedFilters)

                        if (!isFullScreenContext) {
                            return@AppSecondaryButton
                        }

                        when (originScreen) {
                            FilterOriginScreen.APARTMENT_LISTING -> {
                                navController.navigate(
                                    Screen.ApartmentListingScreen.buildRoute(
                                        idUtente,
                                        comune,
                                        ricercaQueryText,
                                        appliedFilters
                                    )
                                )
                            }

                            FilterOriginScreen.MAP_SEARCH -> {
                                navController.navigate(
                                    Screen.MapSearchScreen.buildRoute(
                                        idUtente,
                                        comune,
                                        ricercaQueryText,
                                        appliedFilters
                                    )
                                )
                            }

                            null -> {
                                navController.navigate(
                                    Screen.SearchTypeSelectionScreen.buildRoute(
                                        idUtente,
                                        comune,
                                        ricercaQueryText,
                                        appliedFilters
                                    )
                                ) {
                                    popUpTo(Screen.ApartmentListingScreen.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensions.paddingMedium)
                        .height(dimensions.buttonHeight),
                )
            }
        }
    }
}