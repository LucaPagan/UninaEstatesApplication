package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import kotlin.math.roundToInt
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.MaterialTheme.colorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentListingScreen(navController: NavController, idUtente: String, comune: String) {
    DietiEstatesTheme {
        val colorScheme = colorScheme
        val typography = MaterialTheme.typography

        val scrollState = rememberScrollState()

        // Gestione dello sheet dei filtri
        var showFilterSheet by remember { mutableStateOf(false) }

        // Stato dei filtri
        var priceRange by remember { mutableStateOf(100000f..300000f) }
        var selectedRooms by remember { mutableStateOf(emptySet<Int>()) }
        var selectedBathrooms by remember { mutableStateOf(emptySet<Int>()) }
        var selectedFloors by remember { mutableStateOf(emptySet<Int>()) }
        var hasElevator by remember { mutableStateOf(false) }
        var hasParking by remember { mutableStateOf(false) }
        var hasBalcony by remember { mutableStateOf(false) }
        var hasTerrace by remember { mutableStateOf(false) }
        var minArea by remember { mutableStateOf(50) }
        var maxArea by remember { mutableStateOf(150) }
        var filtersApplied by remember { mutableStateOf(false) }

        // Gradient background like WelcomeScreen
        val gradientColors = arrayOf(
            0.0f to colorScheme.primary,
            0.20f to colorScheme.background,
            0.60f to colorScheme.background,
            1.0f to colorScheme.primary
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Header with more elegant style
                HeaderBar(
                    navController = navController,
                    colorScheme = colorScheme,
                    typography = typography,
                    onFilterClick = { showFilterSheet = true },
                    filtersApplied = filtersApplied,
                    comune = comune
                )

                // Spacer
                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp),
                ) {
                    // Welcome message to user
                    Text(
                        text = "Trova la tua casa dei sogni",
                        style = typography.titleLarge,
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Apartment listings with enhanced visuals
                    ApartmentCard(
                        navController = navController,
                        price = "€180.000",
                        address = "Appartamento Napoli, Via Gennaro 49",
                        rooms = "2 Locali",
                        area = "62 mq",
                        floor = "2 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di ascensore",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        navController = navController,
                        price = "€195.000",
                        address = "Appartamento Napoli, Soccavo, Via Montevergine 20",
                        rooms = "3 Locali",
                        area = "93 mq",
                        floor = "3 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di ascensore",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        navController = navController,
                        price = "€240.000",
                        address = "Appartamento Napoli, Vomero, Via Torsanlo Tasso 185",
                        rooms = "3 Locali",
                        area = "86 mq",
                        floor = "1 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di posto auto",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        navController = navController,
                        price = "€210.000",
                        address = "Appartamento Napoli, Posillipo, Via Petrarca 25",
                        rooms = "4 Locali",
                        area = "105 mq",
                        floor = "4 piano",
                        bathrooms = "2 bagni",
                        features = "Dotato di ascensore e terrazzo",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        navController = navController,
                        price = "€175.000",
                        address = "Appartamento Napoli, Fuorigrotta, Via Leopardi 12",
                        rooms = "2 Locali",
                        area = "75 mq",
                        floor = "2 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di balcone",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    // Bottom padding to ensure last item is visible above bottom bar
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Filtri Bottom Sheet with welcome style theme
            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    containerColor = colorScheme.background,
                    tonalElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    FilterSheet(
                        colorScheme = colorScheme,
                        typography = typography,
                        priceRange = priceRange,
                        onPriceRangeChange = { priceRange = it },
                        selectedRooms = selectedRooms,
                        onRoomsSelected = { selectedRooms = it },
                        selectedBathrooms = selectedBathrooms,
                        onBathroomsSelected = { selectedBathrooms = it },
                        selectedFloors = selectedFloors,
                        onFloorsSelected = { selectedFloors = it },
                        hasElevator = hasElevator,
                        onHasElevatorChange = { hasElevator = it },
                        hasParking = hasParking,
                        onHasParkingChange = { hasParking = it },
                        hasBalcony = hasBalcony,
                        onHasBalconyChange = { hasBalcony = it },
                        hasTerrace = hasTerrace,
                        onHasTerraceChange = { hasTerrace = it },
                        minArea = minArea,
                        maxArea = maxArea,
                        onAreaRangeChange = { min, max -> minArea = min; maxArea = max },
                        onApplyFilters = {
                            filtersApplied = true
                            showFilterSheet = false
                        },
                        onResetFilters = {
                            priceRange = 100000f..300000f
                            selectedRooms = emptySet()
                            selectedBathrooms = emptySet()
                            selectedFloors = emptySet()
                            hasElevator = false
                            hasParking = false
                            hasBalcony = false
                            hasTerrace = false
                            minArea = 50
                            maxArea = 150
                            filtersApplied = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderBar(
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography,
    onFilterClick: () -> Unit,
    filtersApplied: Boolean,
    comune: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        color = colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .size(42.dp)
                    .background(colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "Napoli - $comune",
                    color = colorScheme.onPrimary,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Immobili disponibili",
                    color = colorScheme.onPrimary.copy(alpha = 0.7f),
                    style = typography.bodySmall
                )
            }

            Box {
                IconButton(
                    onClick = { onFilterClick() },
                    modifier = Modifier
                        .size(42.dp)
                        .background(colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtra",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (filtersApplied) {
                    Badge(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 4.dp),
                        containerColor = colorScheme.tertiary
                    ) { }
                }
            }
        }
    }
}

@Composable
fun LuxuryPriceRangeSlider(
    colorScheme: ColorScheme,
    priceRange: ClosedFloatingPointRange<Float>,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    val minPrice = 50000f
    val maxPrice = 400000f

    // Stati per le animazioni
    var isDragging by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isDragging) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    // Formattazione del prezzo
    val formatPrice = { value: Float -> "€${(value / 1000).roundToInt()}K" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header con valori correnti
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Budget immobiliare",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${formatPrice(priceRange.start)} - ${formatPrice(priceRange.endInclusive)}",
                style = MaterialTheme.typography.titleMedium,
                color = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card contenitore per lo slider con effetto elevazione
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isDragging) 8.dp else 4.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Componente slider personalizzato
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                ) {
                    // Slider di base per la logica
                    RangeSlider(
                        value = priceRange,
                        onValueChange = { newRange ->
                            // Arrotonda i valori ai multipli di 500 più vicini
                            val startValue = (newRange.start / 500f).roundToInt() * 500f
                            val endValue = (newRange.endInclusive / 500f).roundToInt() * 500f
                            onPriceRangeChange(startValue..endValue)
                        },
                        valueRange = minPrice..maxPrice,
                        steps = 700,
                        colors = SliderDefaults.colors(
                            thumbColor = colorScheme.secondary,
                            activeTrackColor = colorScheme.secondary.copy(alpha = 0.5f),
                            inactiveTrackColor = colorScheme.surfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { isDragging = true },
                                    onDragEnd = { isDragging = false },
                                    onDragCancel = { isDragging = false },
                                    onDrag = { _, _ -> }
                                )
                            }
                    )

                    // Etichette con prezzi attuali sopra i thumb
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        val boxWidth = maxWidth

                        // Label per il prezzo iniziale
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = ((priceRange.start - minPrice) / (maxPrice - minPrice) *
                                            (boxWidth.value - 32f)).dp
                                )
                        ) {
                            Surface(
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp)),
                                color = colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = formatPrice(priceRange.start),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Label per il prezzo finale
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = ((priceRange.endInclusive - minPrice) / (maxPrice - minPrice) *
                                            (boxWidth.value - 32f)).dp
                                )
                        ) {
                            Surface(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .shadow(4.dp, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp)),
                                color = colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = formatPrice(priceRange.endInclusive),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatPrice(minPrice),
                        fontSize = 10.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatPrice(maxPrice),
                        fontSize = 10.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Range preimpostati per una selezione rapida
        Text(
            text = "Selezioni rapide",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetRangeChip(
                text = "€100K - €200K",
                selected = priceRange.start == 100000f && priceRange.endInclusive == 200000f,
                onClick = { onPriceRangeChange(100000f..200000f) },
                modifier = Modifier.weight(1f)
            )

            PresetRangeChip(
                text = "€200K - €300K",
                selected = priceRange.start == 200000f && priceRange.endInclusive == 300000f,
                onClick = { onPriceRangeChange(200000f..300000f) },
                modifier = Modifier.weight(1f)
            )

            PresetRangeChip(
                text = "€300K+",
                selected = priceRange.start == 300000f && priceRange.endInclusive == 400000f,
                onClick = { onPriceRangeChange(300000f..400000f) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PresetRangeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected)
        colorScheme.secondaryContainer
    else
        colorScheme.surfaceVariant

    val textColor = if (selected)
        colorScheme.onSecondaryContainer
    else
        colorScheme.onSurfaceVariant

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    colorScheme: ColorScheme,
    typography: Typography,
    priceRange: ClosedFloatingPointRange<Float>,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    selectedRooms: Set<Int>,
    onRoomsSelected: (Set<Int>) -> Unit,
    selectedBathrooms: Set<Int>,
    onBathroomsSelected: (Set<Int>) -> Unit,
    selectedFloors: Set<Int>,
    onFloorsSelected: (Set<Int>) -> Unit,
    hasElevator: Boolean,
    onHasElevatorChange: (Boolean) -> Unit,
    hasParking: Boolean,
    onHasParkingChange: (Boolean) -> Unit,
    hasBalcony: Boolean,
    onHasBalconyChange: (Boolean) -> Unit,
    hasTerrace: Boolean,
    onHasTerraceChange: (Boolean) -> Unit,
    minArea: Int,
    maxArea: Int,
    onAreaRangeChange: (Int, Int) -> Unit,
    onApplyFilters: () -> Unit,
    onResetFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Titolo con stile del welcome screen
        Text(
            text = "Filtri di ricerca",
            style = typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Text(
            text = "Personalizza la tua ricerca",
            style = typography.bodyMedium,
            color = colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = colorScheme.onBackground.copy(alpha = 0.1f)
        )

        // Prezzo con stile aggiornato
        Text(
            text = "Prezzo",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            LuxuryPriceRangeSlider(
                colorScheme = colorScheme,
                priceRange = priceRange,
                onPriceRangeChange = onPriceRangeChange
            )
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = colorScheme.onBackground.copy(alpha = 0.1f)
        )

        // Numero di locali con stile aggiornato
        Text(
            text = "Numero di locali",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..5) {
                val isSelected = i in selectedRooms
                val text = when (i) {
                    1 -> "Mono"
                    2 -> "Bilo"
                    3 -> "Trilo"
                    4 -> "Quadri"
                    else -> "$i+"
                }

                FilterChip(
                    modifier = Modifier.weight(1f),
                    text = text,
                    isSelected = isSelected,
                    onClick = {
                        val newSet = selectedRooms.toMutableSet()
                        if (isSelected) {
                            newSet.remove(i)
                        } else {
                            newSet.add(i)
                        }
                        onRoomsSelected(newSet)
                    },
                    colorScheme = colorScheme
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = colorScheme.onBackground.copy(alpha = 0.1f)
        )

        // Numero di bagni con stile aggiornato
        Text(
            text = "Bagni",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..3) {
                val isSelected = i in selectedBathrooms
                val text = if (i < 3) "$i" else "$i+"

                FilterChip(
                    modifier = Modifier.weight(1f),
                    text = text,
                    isSelected = isSelected,
                    onClick = {
                        val newSet = selectedBathrooms.toMutableSet()
                        if (isSelected) {
                            newSet.remove(i)
                        } else {
                            newSet.add(i)
                        }
                        onBathroomsSelected(newSet)
                    },
                    colorScheme = colorScheme
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = colorScheme.onBackground.copy(alpha = 0.1f)
        )

        // Piano con stile aggiornato
        Text(
            text = "Piano",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0..3) {
                val isSelected = i in selectedFloors
                val text = when (i) {
                    0 -> "PT"
                    else -> "$i"
                }

                FilterChip(
                    modifier = Modifier.weight(1f),
                    text = text,
                    isSelected = isSelected,
                    onClick = {
                        val newSet = selectedFloors.toMutableSet()
                        if (isSelected) {
                            newSet.remove(i)
                        } else {
                            newSet.add(i)
                        }
                        onFloorsSelected(newSet)
                    },
                    colorScheme = colorScheme
                )
            }

            FilterChip(
                modifier = Modifier.weight(1f),
                text = "4+",
                isSelected = 4 in selectedFloors,
                onClick = {
                    val newSet = selectedFloors.toMutableSet()
                    if (4 in newSet) {
                        newSet.remove(4)
                    } else {
                        newSet.add(4)
                    }
                    onFloorsSelected(newSet)
                },
                colorScheme = colorScheme
            )
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = colorScheme.onBackground.copy(alpha = 0.1f)
        )

        // Superficie
        Text(
            text = "Superficie (mq)",
            style = typography.titleMedium,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$minArea - $maxArea mq",
            style = typography.bodyMedium,
            color = colorScheme.onBackground

        )

        Spacer(modifier = Modifier.height(8.dp))

        var sliderPosition by remember { mutableStateOf(minArea.toFloat()..maxArea.toFloat()) }
        RangeSlider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onAreaRangeChange(
                    it.start.roundToInt(),
                    it.endInclusive.roundToInt()
                )
            },
            valueRange = 30f..200f,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = colorScheme.primary,
                activeTrackColor = colorScheme.primary
            )
        )

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = colorScheme.onBackground.copy(alpha = 0.1f)
        )

        // Caratteristiche
        Text(
            text = "Caratteristiche",
            style = typography.titleMedium,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                CharacteristicCheckbox(
                    text = "Ascensore",
                    checked = hasElevator,
                    onCheckedChange = onHasElevatorChange,
                    colorScheme = colorScheme,
                    typography = typography
                )

                Spacer(modifier = Modifier.height(8.dp))

                CharacteristicCheckbox(
                    text = "Posto auto",
                    checked = hasParking,
                    onCheckedChange = onHasParkingChange,
                    colorScheme = colorScheme,
                    typography = typography
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                CharacteristicCheckbox(
                    text = "Balcone",
                    checked = hasBalcony,
                    onCheckedChange = onHasBalconyChange,
                    colorScheme = colorScheme,
                    typography = typography
                )

                Spacer(modifier = Modifier.height(8.dp))

                CharacteristicCheckbox(
                    text = "Terrazzo",
                    checked = hasTerrace,
                    onCheckedChange = onHasTerraceChange,
                    colorScheme = colorScheme,
                    typography = typography
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pulsanti
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onResetFilters,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colorScheme.primary
                )
            ) {
                Text("Resetta filtri")
            }

            Button(
                onClick = onApplyFilters,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Text("Applica filtri")
            }
        }

        // Spazio alla fine per evitare che il contenuto venga oscurato dalla keyboard
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CharacteristicCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colorScheme.primary,
                uncheckedColor = colorScheme.onBackground.copy(alpha = 0.5f)
            )
        )

        Text(
            text = text,
            style = typography.bodyMedium,
            color = colorScheme.onBackground
        )
    }
}

@Composable
fun FilterChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .border(
                width = 1.dp,
                color = if (isSelected) colorScheme.primary else colorScheme.onBackground.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = if (isSelected) colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) colorScheme.primary else colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
fun ApartmentCard(
    navController: NavController,
    price: String,
    address: String,
    rooms: String,
    area: String,
    floor: String,
    bathrooms: String,
    features: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Apartment image (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.property1),
                    contentDescription = "Property Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            // Apartment details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = price,
                        style = typography.titleMedium
                    )

                    // Visualizza button
                    Button(
                        onClick = {
                            navController.navigate(Screen.PropertyScreen.route)
                        },
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.secondary,
                            contentColor = colorScheme.onSecondary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Visualizza",
                            style = typography.labelSmall
                        )
                    }
                }

                Text(
                    text = address,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Property details
                Text(
                    text = "$rooms, $area, $floor, $bathrooms, $features",
                    style = typography.bodySmall,
                    color = colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApartmentListingScreen() {
    val navController = rememberNavController()
    ApartmentListingScreen(navController = navController, idUtente = "Danilo", comune = "Napoli")
}