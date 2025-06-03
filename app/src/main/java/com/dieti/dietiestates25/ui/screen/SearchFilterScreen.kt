package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.components.FilterSection
import com.dieti.dietiestates25.ui.components.PredefinedRange
import com.dieti.dietiestates25.ui.components.RangeFilterInput
import com.dieti.dietiestates25.ui.components.SelectableOptionButton
import com.dieti.dietiestates25.ui.components.SingleChoiceToggleGroup
import com.dieti.dietiestates25.ui.components.defaultOutlineTextFieldColors
import com.dieti.dietiestates25.ui.model.FilterModel
import com.dieti.dietiestates25.ui.model.FilterOriginScreen
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.theme.TealDeep

@Composable
fun SearchFilterScreen(
    navController: NavController,
    idUtente: String = "utente",
    comune: String = "napoli",
    ricercaQueryText: String,
    initialFilters: FilterModel? = null, // Nuovo parametro per i filtri iniziali
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onApplyFilters: (FilterModel) -> Unit,
    isFullScreenContext: Boolean = true,
    originScreen: FilterOriginScreen? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val focusManager = LocalFocusManager.current

    // Inizializza gli stati con i valori dei filtri iniziali
    var selectedPurchaseType by remember { mutableStateOf(initialFilters?.purchaseType) }
    val purchaseOptions = listOf("Compra", "Affitta")

    val priceValueRange = 0f..2000000f
    val priceSteps = ((priceValueRange.endInclusive - priceValueRange.start) / 10000f - 1).toInt().coerceAtLeast(0)

    // Inizializza i prezzi dai filtri iniziali
    var minPriceText by remember {
        mutableStateOf(initialFilters?.minPrice?.toInt()?.toString() ?: "")
    }
    var maxPriceText by remember {
        mutableStateOf(initialFilters?.maxPrice?.toInt()?.toString() ?: "")
    }
    var priceSliderPosition by remember {
        mutableStateOf(
            (initialFilters?.minPrice ?: priceValueRange.start)..
                    (initialFilters?.maxPrice ?: priceValueRange.endInclusive)
        )
    }

    val surfaceValueRange = 0f..1000f
    val surfaceSteps = ((surfaceValueRange.endInclusive - surfaceValueRange.start) / 5f - 1).toInt().coerceAtLeast(0)

    // Inizializza le superfici dai filtri iniziali
    var minSurfaceText by remember {
        mutableStateOf(initialFilters?.minSurface?.toInt()?.toString() ?: "")
    }
    var maxSurfaceText by remember {
        mutableStateOf(initialFilters?.maxSurface?.toInt()?.toString() ?: "")
    }
    var surfaceSliderPosition by remember {
        mutableStateOf(
            (initialFilters?.minSurface ?: surfaceValueRange.start)..
                    (initialFilters?.maxSurface ?: surfaceValueRange.endInclusive)
        )
    }

    // Inizializza le stanze dai filtri iniziali
    var minRooms by remember {
        mutableStateOf(initialFilters?.minRooms?.toString() ?: "")
    }
    var maxRooms by remember {
        mutableStateOf(initialFilters?.maxRooms?.toString() ?: "")
    }

    // Inizializza i bagni dai filtri iniziali
    var selectedBathrooms by remember { mutableStateOf(initialFilters?.bathrooms) }
    val bathroomOptions = listOf(1, 2, 3)

    // Inizializza la condizione dai filtri iniziali
    var selectedCondition by remember { mutableStateOf(initialFilters?.condition) }
    val conditionOptionsFirstRow = listOf("Nuovo", "Ottimo")
    val conditionOptionsSecondRow = listOf("Buono", "Da ristrutturare")

    // --- Sincronizzazione e Logica ---
    LaunchedEffect(priceSliderPosition) {
        // Aggiorna i campi di testo solo se non sono stati modificati manualmente
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
        // Aggiorna i campi di testo solo se non sono stati modificati manualmente
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
            PredefinedRange("€0-100k", 0f, 100000f),
            PredefinedRange("€100k-250k", 100000f, 250000f),
            PredefinedRange("€250k-500k", 250000f, 500000f),
            PredefinedRange(">€500k", 500000f, priceValueRange.endInclusive)
        )
    }

    val predefinedSurfaceRanges = remember(surfaceValueRange.endInclusive) {
        listOf(
            PredefinedRange("0-50mq", 0f, 50f),
            PredefinedRange("50-100mq", 50f, 100f),
            PredefinedRange("100-150mq", 100f, 150f),
            PredefinedRange(">150mq", 150f, surfaceValueRange.endInclusive)
        )
    }

    fun resetAllFilters() {
        selectedPurchaseType = null
        selectedBathrooms = null
        selectedCondition = null
        minPriceText = ""
        maxPriceText = ""
        priceSliderPosition = priceValueRange.start..priceValueRange.endInclusive
        minSurfaceText = ""
        maxSurfaceText = ""
        surfaceSliderPosition = surfaceValueRange.start..surfaceValueRange.endInclusive
        minRooms = ""
        maxRooms = ""
    }

    // Funzione per verificare se ci sono filtri attivi
    fun hasActiveFilters(): Boolean {
        return selectedPurchaseType != null ||
                minPriceText.isNotBlank() || maxPriceText.isNotBlank() ||
                minSurfaceText.isNotBlank() || maxSurfaceText.isNotBlank() ||
                minRooms.isNotBlank() || maxRooms.isNotBlank() ||
                selectedBathrooms != null ||
                selectedCondition != null ||
                priceSliderPosition.start > priceValueRange.start ||
                priceSliderPosition.endInclusive < priceValueRange.endInclusive ||
                surfaceSliderPosition.start > surfaceValueRange.start ||
                surfaceSliderPosition.endInclusive < surfaceValueRange.endInclusive
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        // Custom TopBar
        SearchFilterTopBar(
            title = "FILTRI RICERCA",
            onNavigateBack = onNavigateBack,
            onReset = { resetAllFilters() },
            isFullScreenContext = isFullScreenContext,
            hasActiveFilters = hasActiveFilters(),
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )

        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(colorScheme.background)
                    .padding(
                        start = dimensions.paddingMedium,
                        end = dimensions.paddingMedium,
                        top = dimensions.paddingSmall,
                        bottom = dimensions.paddingLarge
                    )
            ) {
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
                    onPredefinedRangeSelected = { range -> priceSliderPosition = range.min..range.max },
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
                    onPredefinedRangeSelected = { range -> surfaceSliderPosition = range.min..range.max },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                    ) {
                        val textFieldColors = defaultOutlineTextFieldColors(colorScheme, typography)
                        OutlinedTextField(
                            value = minRooms,
                            onValueChange = { minRooms = it.filter { char -> char.isDigit() } },
                            label = { Text("Min.") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(dimensions.spacingSmall),
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = typography.bodyMedium.copy(color = colorScheme.onSurface),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = maxRooms,
                            onValueChange = { maxRooms = it.filter { char -> char.isDigit() } },
                            label = { Text("Max.") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(dimensions.spacingSmall),
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = typography.bodyMedium.copy(color = colorScheme.onSurface),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                    ) {
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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                        ) {
                            conditionOptionsFirstRow.forEach { condition ->
                                SelectableOptionButton(
                                    text = condition,
                                    isSelected = selectedCondition == condition,
                                    onClick = { selectedCondition = if (selectedCondition == condition) null else condition },
                                    modifier = Modifier.weight(1f),
                                    dimensions = dimensions,
                                    typography = typography,
                                    colorScheme = colorScheme
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                        ) {
                            conditionOptionsSecondRow.forEach { condition ->
                                SelectableOptionButton(
                                    text = condition,
                                    isSelected = selectedCondition == condition,
                                    onClick = { selectedCondition = if (selectedCondition == condition) null else condition },
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
            shadowElevation = if (isFullScreenContext) dimensions.elevationMedium else 0.dp,
            color = colorScheme.surface
        ) {
            AppSecondaryButton(
                text = "Mostra risultati",
                onClick = {
                    println("DEBUG - originScreen: $originScreen")

                    val finalMinPrice = when {
                        minPriceText.isNotBlank() -> minPriceText.toFloatOrNull()
                        priceSliderPosition.start > priceValueRange.start -> priceSliderPosition.start
                        else -> null
                    }
                    val finalMaxPrice = when {
                        maxPriceText.isNotBlank() -> maxPriceText.toFloatOrNull()
                        priceSliderPosition.endInclusive < priceValueRange.endInclusive -> priceSliderPosition.endInclusive
                        else -> null
                    }
                    val finalMinSurface = when {
                        minSurfaceText.isNotBlank() -> minSurfaceText.toFloatOrNull()
                        surfaceSliderPosition.start > surfaceValueRange.start -> surfaceSliderPosition.start
                        else -> null
                    }
                    val finalMaxSurface = when {
                        maxSurfaceText.isNotBlank() -> maxSurfaceText.toFloatOrNull()
                        surfaceSliderPosition.endInclusive < surfaceValueRange.endInclusive -> surfaceSliderPosition.endInclusive
                        else -> null
                    }

                    val appliedFilters = FilterModel(
                        purchaseType = selectedPurchaseType,
                        minPrice = finalMinPrice,
                        maxPrice = finalMaxPrice,
                        minSurface = finalMinSurface,
                        maxSurface = finalMaxSurface,
                        minRooms = minRooms.toIntOrNull(),
                        maxRooms = maxRooms.toIntOrNull(),
                        bathrooms = selectedBathrooms,
                        condition = selectedCondition
                    )
                    onApplyFilters(appliedFilters)

                    when (originScreen) {
                        FilterOriginScreen.APARTMENT_LISTING -> {
                            navController.navigate(
                                Screen.ApartmentListingScreen.buildRoute(
                                    idUtente, comune, ricercaQueryText, appliedFilters
                                )
                            )
                        }
                        FilterOriginScreen.MAP_SEARCH -> {
                            navController.navigate(
                                Screen.MapSearchScreen.buildRoute(
                                    idUtente, comune, ricercaQueryText, appliedFilters
                                )
                            )
                        }
                        null -> {
                            navController.navigate(
                                Screen.SearchTypeSelectionScreen.buildRoute(idUtente, comune, ricercaQueryText, appliedFilters)
                            ) {
                                popUpTo(Screen.ApartmentListingScreen.route) {
                                    inclusive = true
                                }
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

@Composable
private fun SearchFilterTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    onReset: () -> Unit,
    isFullScreenContext: Boolean,
    hasActiveFilters: Boolean,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column {
        // Status Bar Spacer solo se fullscreen
        if (isFullScreenContext) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(TealDeep)
            )
        }

        // TopBar Content
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (isFullScreenContext) colorScheme.primary else colorScheme.surface,
            shadowElevation = if (!isFullScreenContext) dimensions.elevationSmall else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp) // Standard TopAppBar height
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Navigation Icon
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(dimensions.iconSizeLarge + dimensions.spacingSmall)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = if (isFullScreenContext) colorScheme.onPrimary else colorScheme.onSurface
                    )
                }

                // Title - Centered
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = typography.titleMedium.copy(letterSpacing = 0.5.sp),
                        color = if (isFullScreenContext) colorScheme.onPrimary else colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }

                // Reset Button - Mostra solo se ci sono filtri attivi
                if (hasActiveFilters) {
                    TextButton(
                        onClick = onReset,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isFullScreenContext) colorScheme.onPrimary else colorScheme.primary
                        )
                    ) {
                        Text(
                            "RESET",
                            style = typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                } else {
                    // Placeholder per mantenere il layout centrato
                    Spacer(modifier = Modifier.width(64.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun PreviewFilterScreen() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        SearchFilterScreen(
            navController = navController,
            idUtente = "utentePreview",
            comune = "Napoli",
            ricercaQueryText = "Vomero",
            initialFilters = FilterModel(
                purchaseType = "Affitta",
                minPrice = 500f,
                maxPrice = 1000f,
                bathrooms = 2
            ),
            onNavigateBack = {},
            onApplyFilters = { filterModel -> println("Preview - Filtri applicati: $filterModel") },
            isFullScreenContext = true
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun PreviewFilterScreenEmpty() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        SearchFilterScreen(
            navController = navController,
            idUtente = "utentePreview",
            comune = "Napoli",
            ricercaQueryText = "Vomero",
            onNavigateBack = {},
            onApplyFilters = { filterModel -> println("Preview - Filtri applicati: $filterModel") },
            isFullScreenContext = true
        )
    }
}