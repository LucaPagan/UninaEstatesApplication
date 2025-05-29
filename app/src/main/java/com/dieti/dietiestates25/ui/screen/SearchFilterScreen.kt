@file:Suppress("DEPRECATION")

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.FilterSection
import com.dieti.dietiestates25.ui.components.PredefinedRange
import com.dieti.dietiestates25.ui.components.RangeFilterInput
import com.dieti.dietiestates25.ui.components.SelectableOptionButton
import com.dieti.dietiestates25.ui.components.SingleChoiceToggleGroup
import com.dieti.dietiestates25.ui.components.defaultOutlineTextFieldColors
import com.dieti.dietiestates25.ui.model.FilterModel
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.findActivity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterScreen(
    navController: NavController,
    idUtente: String = "utente",
    comune: String = "napoli",
    ricercaQueryText: String,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    onApplyFilters: (FilterModel) -> Unit,
    isFullScreenContext: Boolean = true
) {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val dimensions = Dimensions

    val focusManager = LocalFocusManager.current

        var selectedPurchaseType by remember { mutableStateOf<String?>(null) }
        val purchaseOptions = listOf("Compra", "Affitta")

        val priceValueRange = 0f..2000000f
        val priceSteps = ((priceValueRange.endInclusive - priceValueRange.start) / 10000f - 1).toInt().coerceAtLeast(0)
        var minPriceText by remember { mutableStateOf("") }
        var maxPriceText by remember { mutableStateOf("") }
        var priceSliderPosition by remember { mutableStateOf(priceValueRange.start..priceValueRange.endInclusive) }

        val surfaceValueRange = 0f..1000f
        val surfaceSteps = ((surfaceValueRange.endInclusive - surfaceValueRange.start) / 5f - 1).toInt().coerceAtLeast(0)
        var minSurfaceText by remember { mutableStateOf("") }
        var maxSurfaceText by remember { mutableStateOf("") }
        var surfaceSliderPosition by remember { mutableStateOf(surfaceValueRange.start..surfaceValueRange.endInclusive) }

        var minRooms by remember { mutableStateOf("") }
        var maxRooms by remember { mutableStateOf("") }

        var selectedBathrooms by remember { mutableStateOf<Int?>(null) }
        val bathroomOptions = listOf(1, 2, 3)

        var selectedCondition by remember { mutableStateOf<String?>(null) }
        val conditionOptionsFirstRow = listOf("Nuovo", "Ottimo")
        val conditionOptionsSecondRow = listOf("Buono", "Da ristrutturare")

        // --- Sincronizzazione e Logica (invariate) ---
        LaunchedEffect(priceSliderPosition) {
            minPriceText = if (priceSliderPosition.start <= priceValueRange.start) "" else priceSliderPosition.start.toInt().toString()
            maxPriceText = if (priceSliderPosition.endInclusive >= priceValueRange.endInclusive) "" else priceSliderPosition.endInclusive.toInt().toString()
        }
        fun updatePriceSliderFromText() {
            val minP = minPriceText.toFloatOrNull() ?: priceValueRange.start
            val maxP = maxPriceText.toFloatOrNull() ?: priceValueRange.endInclusive
            if (minP <= maxP) priceSliderPosition = minP.coerceIn(priceValueRange)..maxP.coerceIn(priceValueRange)
        }

        LaunchedEffect(surfaceSliderPosition) {
            minSurfaceText = if (surfaceSliderPosition.start <= surfaceValueRange.start) "" else surfaceSliderPosition.start.toInt().toString()
            maxSurfaceText = if (surfaceSliderPosition.endInclusive >= surfaceValueRange.endInclusive) "" else surfaceSliderPosition.endInclusive.toInt().toString()
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


        Scaffold(
            modifier =
                if (isFullScreenContext) Modifier
                .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            } else Modifier
                .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "FILTRI RICERCA",
                            style = typography.titleMedium.copy(letterSpacing = 0.5.sp),
                            color = if (isFullScreenContext) colorScheme.onPrimary else colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth(), // Semplificato
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(dimensions.iconSizeLarge + dimensions.spacingSmall)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack, "Indietro",
                                tint = if (isFullScreenContext) colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { resetAllFilters() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (isFullScreenContext) colorScheme.onPrimary else colorScheme.primary
                            )
                        ) {
                            Text("RESET", style = typography.labelLarge.copy(fontWeight = FontWeight.Medium))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isFullScreenContext) colorScheme.primary else colorScheme.surface
                    ),
                    scrollBehavior = if (isFullScreenContext) TopAppBarDefaults.pinnedScrollBehavior() else null,

                    windowInsets = if (isFullScreenContext) {
                        TopAppBarDefaults.windowInsets
                    } else {
                        WindowInsets(0.dp)
                    }
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = if (isFullScreenContext) dimensions.elevationMedium else 0.dp,
                    color = colorScheme.surface
                ) {
                    Button(
                        onClick = {
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.paddingMedium)
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Search, "Cerca", tint = colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                            Text("MOSTRA RISULTATI", style = typography.labelLarge.copy(fontWeight = FontWeight.Medium))
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
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
            onNavigateBack = {},
            onApplyFilters = { filterModel -> println("Preview - Filtri applicati: $filterModel") },
            isFullScreenContext = true // Simula contesto full-screen per la preview
        )
    }
}