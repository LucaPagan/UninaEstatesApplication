package com.dieti.dietiestates25.ui.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
// Importa i nuovi componenti generici
import com.dieti.dietiestates25.ui.components.FilterSection
import com.dieti.dietiestates25.ui.components.RangeFilterInput
import com.dieti.dietiestates25.ui.components.SelectableOptionButton
import com.dieti.dietiestates25.ui.components.SingleChoiceToggleGroup
import com.dieti.dietiestates25.ui.components.PredefinedRange
import com.dieti.dietiestates25.ui.navigation.Screen

// Rimuovi defaultOutlineTextFieldColors se non è più usato direttamente qui
// import com.dieti.dietiestates25.ui.components.defaultOutlineTextFieldColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterScreen(
    navController: NavController,
    idUtente: String = "utente",
    comune: String = "napoli",
    ricerca: String = "varcaturo", // Questo valore potrebbe essere usato per pre-impostare filtri o mostrare contesto
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val dimensions = Dimensions

        // Status bar handling
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Light icons on dark primary
            }
        }

        // State per i filtri selezionati
        var selectedPurchaseType by remember { mutableStateOf<String?>(null) }
        val purchaseOptions = listOf("Compra", "Affitta")

        var selectedBathrooms by remember { mutableStateOf<Int?>(null) }
        val bathroomOptions = listOf(1, 2, 3) // Per i pulsanti diretti

        var selectedCondition by remember { mutableStateOf<String?>(null) }
        val conditionOptionsFirstRow = listOf("Nuovo", "Ottimo")
        val conditionOptionsSecondRow = listOf("Buono", "Da ristrutturare")


        // --- State per PREZZO ---
        val priceValueRange = 0f..2000000f
        val priceSteps = ((priceValueRange.endInclusive - priceValueRange.start) / 10000f - 1).toInt().coerceAtLeast(0)
        var minPriceText by remember { mutableStateOf("") }
        var maxPriceText by remember { mutableStateOf("") }
        var priceSliderPosition by remember { mutableStateOf(priceValueRange.start..priceValueRange.endInclusive) }

        // --- State per SUPERFICIE ---
        val surfaceValueRange = 0f..1000f
        val surfaceSteps = ((surfaceValueRange.endInclusive - surfaceValueRange.start) / 5f - 1).toInt().coerceAtLeast(0)
        var minSurfaceText by remember { mutableStateOf("") }
        var maxSurfaceText by remember { mutableStateOf("") }
        var surfaceSliderPosition by remember { mutableStateOf(surfaceValueRange.start..surfaceValueRange.endInclusive) }

        // Sincronizzazione per PREZZO
        LaunchedEffect(priceSliderPosition) {
            minPriceText = if (priceSliderPosition.start <= priceValueRange.start) "" else priceSliderPosition.start.toInt().toString()
            maxPriceText = if (priceSliderPosition.endInclusive >= priceValueRange.endInclusive) "" else priceSliderPosition.endInclusive.toInt().toString()
        }
        fun updatePriceSliderFromText() {
            val minP = minPriceText.toFloatOrNull() ?: priceValueRange.start
            val maxP = maxPriceText.toFloatOrNull() ?: priceValueRange.endInclusive
            if (minP <= maxP) {
                priceSliderPosition = minP.coerceIn(priceValueRange)..maxP.coerceIn(priceValueRange)
            } else {
                // Opzionale: gestisci input invalido, es. non aggiornare o mostrare errore
            }
        }

        // Sincronizzazione per SUPERFICIE
        LaunchedEffect(surfaceSliderPosition) {
            minSurfaceText = if (surfaceSliderPosition.start <= surfaceValueRange.start) "" else surfaceSliderPosition.start.toInt().toString()
            maxSurfaceText = if (surfaceSliderPosition.endInclusive >= surfaceValueRange.endInclusive) "" else surfaceSliderPosition.endInclusive.toInt().toString()
        }
        fun updateSurfaceSliderFromText() {
            val minS = minSurfaceText.toFloatOrNull() ?: surfaceValueRange.start
            val maxS = maxSurfaceText.toFloatOrNull() ?: surfaceValueRange.endInclusive
            if (minS <= maxS) {
                surfaceSliderPosition = minS.coerceIn(surfaceValueRange)..maxS.coerceIn(surfaceValueRange)
            }
        }

        val predefinedPriceRanges = listOf(
            PredefinedRange("€0-100k", 0f, 100000f),
            PredefinedRange("€100k-250k", 100000f, 250000f),
            PredefinedRange("€250k-500k", 250000f, 500000f),
            PredefinedRange(">€500k", 500000f, priceValueRange.endInclusive)
        )
        val predefinedSurfaceRanges = listOf(
            PredefinedRange("0-50mq", 0f, 50f),
            PredefinedRange("50-100mq", 50f, 100f),
            PredefinedRange("100-150mq", 100f, 150f),
            PredefinedRange(">150mq", 150f, surfaceValueRange.endInclusive)
        )

        // State per Locali
        var minRooms by remember { mutableStateOf("") }
        var maxRooms by remember { mutableStateOf("") }


        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SELEZIONA FILTRI",
                            color = colorScheme.onPrimary,
                            style = typography.titleMedium,
                            letterSpacing = 1.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(dimensions.iconSizeExtraLarge)
                                .padding(dimensions.spacingSmall)
                                .background(colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint = colorScheme.onPrimary,
                                modifier = Modifier.size(dimensions.iconSizeMedium)
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = {
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
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.onPrimary)
                        ) {
                            Text("Reset", style = typography.labelLarge)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.surface)
                        .padding(dimensions.paddingMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.ApartmentListingScreen.withArgs(idUtente, comune, ricerca)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.buttonHeight),
                        shape = RoundedCornerShape(dimensions.spacingSmall),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = dimensions.elevationLarge,
                            pressedElevation = dimensions.elevationMedium
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.Search, "Cerca", tint = colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                            Text("Mostra risultati", color = colorScheme.onPrimary, style = typography.labelLarge)
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
                    .padding(horizontal = dimensions.paddingMedium)
            ) {
                Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                SingleChoiceToggleGroup(
                    options = purchaseOptions,
                    selectedOption = selectedPurchaseType,
                    onOptionSelected = { selectedPurchaseType = it },
                    dimensions = dimensions,
                    typography = typography,
                    colorScheme = colorScheme
                )

                Spacer(modifier = Modifier.height(dimensions.spacingLarge))

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
                        OutlinedTextField( // Sarebbe bene usare un defaultOutlineTextFieldColors anche qui
                            value = minRooms,
                            onValueChange = { minRooms = it },
                            label = { Text("Minimo") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(dimensions.spacingSmall),
                            colors = com.dieti.dietiestates25.ui.components.defaultOutlineTextFieldColors(colorScheme, typography), // Chiamata corretta
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = typography.bodyMedium.copy(color = colorScheme.onSurface)
                        )
                        OutlinedTextField(
                            value = maxRooms,
                            onValueChange = { maxRooms = it },
                            label = { Text("Massimo") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(dimensions.spacingSmall),
                            colors = com.dieti.dietiestates25.ui.components.defaultOutlineTextFieldColors(colorScheme, typography), // Chiamata corretta
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = typography.bodyMedium.copy(color = colorScheme.onSurface)
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
                            isSelected = selectedBathrooms != null && selectedBathrooms!! > 3,
                            onClick = { selectedBathrooms = if (selectedBathrooms != null && selectedBathrooms!! > 3) null else 4 },
                            modifier = Modifier.weight(1f),
                            dimensions = dimensions,
                            typography = typography,
                            colorScheme = colorScheme
                        )
                    }
                }

                FilterSection(
                    title = "Stato immobile",
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
                Spacer(modifier = Modifier.height(dimensions.spacingMedium)) // Spazio finale
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun PreviewFilterScreen() {
    val navController = rememberNavController()
    SearchFilterScreen(
        navController = navController,
        idUtente = "utente",
        ricerca = "varcaturo"
    )
}