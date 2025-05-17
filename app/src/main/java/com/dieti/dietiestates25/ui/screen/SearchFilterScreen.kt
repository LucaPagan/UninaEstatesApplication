package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterScreen(
    navController: NavController,
    idUtente: String = "utente",
    ricerca: String = "varcaturo",
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        // State per i filtri selezionati
        var selectedPurchaseType by remember { mutableStateOf<String?>(null) }
        var selectedBathrooms by remember { mutableStateOf<Int?>(null) }
        var selectedCondition by remember { mutableStateOf<String?>(null) }

        // State per i valori dei campi di input
        var minPrice by remember { mutableStateOf("") }
        var maxPrice by remember { mutableStateOf("") }
        var minSurface by remember { mutableStateOf("") }
        var maxSurface by remember { mutableStateOf("") }
        var minRooms by remember { mutableStateOf("") }
        var maxRooms by remember { mutableStateOf("") }

        // State per i valori delle slider
        val priceRange = remember { mutableStateOf(0f..1000000f) }
        val currentPriceRange = remember { mutableStateOf(0f..500000f) }

        val surfaceRange = remember { mutableStateOf(0f..500f) }
        val currentSurfaceRange = remember { mutableStateOf(0f..100f) }

        // Osservatori per aggiornare i campi di input dalle slider
        LaunchedEffect(currentPriceRange.value) {
            minPrice = currentPriceRange.value.start.toInt().toString()
            maxPrice = currentPriceRange.value.endInclusive.toInt().toString()
        }

        LaunchedEffect(currentSurfaceRange.value) {
            minSurface = currentSurfaceRange.value.start.toInt().toString()
            maxSurface = currentSurfaceRange.value.endInclusive.toInt().toString()
        }

        // Osservatori per aggiornare le slider dai campi di input
        LaunchedEffect(minPrice) {
            if (minPrice.isNotEmpty()) {
                val minPriceValue = minPrice.toFloatOrNull() ?: 0f
                if (minPriceValue <= currentPriceRange.value.endInclusive) {
                    currentPriceRange.value = minPriceValue..currentPriceRange.value.endInclusive
                }
            }
        }

        LaunchedEffect(maxPrice) {
            if (maxPrice.isNotEmpty()) {
                val maxPriceValue = maxPrice.toFloatOrNull() ?: priceRange.value.endInclusive
                if (maxPriceValue >= currentPriceRange.value.start) {
                    currentPriceRange.value = currentPriceRange.value.start..maxPriceValue
                }
            }
        }

        LaunchedEffect(minSurface) {
            if (minSurface.isNotEmpty()) {
                val minSurfaceValue = minSurface.toFloatOrNull() ?: 0f
                if (minSurfaceValue <= currentSurfaceRange.value.endInclusive) {
                    currentSurfaceRange.value = minSurfaceValue..currentSurfaceRange.value.endInclusive
                }
            }
        }

        LaunchedEffect(maxSurface) {
            if (maxSurface.isNotEmpty()) {
                val maxSurfaceValue = maxSurface.toFloatOrNull() ?: surfaceRange.value.endInclusive
                if (maxSurfaceValue >= currentSurfaceRange.value.start) {
                    currentSurfaceRange.value = currentSurfaceRange.value.start..maxSurfaceValue
                }
            }
        }

        // Funzione di reset per tutti i filtri
        fun resetFilters() {
            selectedPurchaseType = null
            selectedBathrooms = null
            selectedCondition = null
            minPrice = ""
            maxPrice = ""
            minSurface = ""
            maxSurface = ""
            minRooms = ""
            maxRooms = ""
            currentPriceRange.value = 0f..500000f
            currentSurfaceRange.value = 0f..100f
            coroutineScope.launch {
                scrollState.animateScrollTo(0)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "FILTRI DI RICERCA",
                            color = colorScheme.onPrimary,
                            style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            letterSpacing = 1.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(40.dp)
                                .background(colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint = colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { resetFilters() },
                            colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.onPrimary)
                        ) {
                            Text(
                                text = "Reset",
                                style = typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            // Navigazione ai risultati con i filtri applicati
                            // TODO: Implementare la logica di navigazione con parametri
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cerca",
                                tint = colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "MOSTRA RISULTATI",
                                color = colorScheme.onPrimary,
                                style = typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(colorScheme.background)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Sezione tipo di acquisto (Compra/Affitta)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Tipo di annuncio",
                            style = typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Compra", "Affitta").forEach { option ->
                                val selected = option == selectedPurchaseType
                                Button(
                                    onClick = {
                                        selectedPurchaseType = if (selected) null else option
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selected) colorScheme.primary else Color.Transparent,
                                        contentColor = if (selected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = if (selected) 2.dp else 0.dp,
                                        pressedElevation = 0.dp
                                    )
                                ) {
                                    Text(
                                        text = option,
                                        style = typography.labelLarge,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Sezione Prezzo con slider
                FilterCard(title = "Prezzo", typography = typography, colorScheme = colorScheme) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Slider per il prezzo
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            RangeSlider(
                                value = currentPriceRange.value,
                                onValueChange = { range ->
                                    currentPriceRange.value = range
                                },
                                valueRange = priceRange.value,
                                steps = 50,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = colorScheme.primary,
                                    activeTrackColor = colorScheme.primary,
                                    inactiveTrackColor = colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "€${currentPriceRange.value.start.toInt()}",
                                    style = typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "€${currentPriceRange.value.endInclusive.toInt()}",
                                    style = typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Preset buttons for price
                        Text(
                            text = "Preset rapidi",
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PresetButton(
                                text = "< 100K",
                                onClick = {
                                    currentPriceRange.value = 0f..100000f
                                    minPrice = "0"
                                    maxPrice = "100000"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = currentPriceRange.value.endInclusive <= 100000f && currentPriceRange.value.start == 0f,
                                modifier = Modifier.weight(1f)
                            )

                            PresetButton(
                                text = "100K-300K",
                                onClick = {
                                    currentPriceRange.value = 100000f..300000f
                                    minPrice = "100000"
                                    maxPrice = "300000"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = currentPriceRange.value.start == 100000f && currentPriceRange.value.endInclusive == 300000f,
                                modifier = Modifier.weight(1f)
                            )

                            PresetButton(
                                text = "> 300K",
                                onClick = {
                                    currentPriceRange.value = 300000f..1000000f
                                    minPrice = "300000"
                                    maxPrice = "1000000"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = currentPriceRange.value.start == 300000f && currentPriceRange.value.endInclusive == 1000000f,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Input fields for price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = minPrice,
                                onValueChange = { minPrice = it },
                                label = { Text("Prezzo min") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                leadingIcon = {
                                    Text(
                                        text = "€",
                                        style = typography.bodyLarge,
                                        color = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            )

                            OutlinedTextField(
                                value = maxPrice,
                                onValueChange = { maxPrice = it },
                                label = { Text("Prezzo max") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                leadingIcon = {
                                    Text(
                                        text = "€",
                                        style = typography.bodyLarge,
                                        color = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                // Sezione Superficie con slider
                FilterCard(title = "Superficie (mq)", typography = typography, colorScheme = colorScheme) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Slider per superficie
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            RangeSlider(
                                value = currentSurfaceRange.value,
                                onValueChange = { range ->
                                    currentSurfaceRange.value = range
                                },
                                valueRange = surfaceRange.value,
                                steps = 50,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = colorScheme.primary,
                                    activeTrackColor = colorScheme.primary,
                                    inactiveTrackColor = colorScheme.onSurface.copy(alpha = 0.2f)
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${currentSurfaceRange.value.start.toInt()} mq",
                                    style = typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${currentSurfaceRange.value.endInclusive.toInt()} mq",
                                    style = typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Preset buttons for surface
                        Text(
                            text = "Preset rapidi",
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PresetButton(
                                text = "< 50 mq",
                                onClick = {
                                    currentSurfaceRange.value = 0f..50f
                                    minSurface = "0"
                                    maxSurface = "50"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = currentSurfaceRange.value.endInclusive <= 50f && currentSurfaceRange.value.start == 0f,
                                modifier = Modifier.weight(1f)
                            )

                            PresetButton(
                                text = "50-100 mq",
                                onClick = {
                                    currentSurfaceRange.value = 50f..100f
                                    minSurface = "50"
                                    maxSurface = "100"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = currentSurfaceRange.value.start == 50f && currentSurfaceRange.value.endInclusive == 100f,
                                modifier = Modifier.weight(1f)
                            )

                            PresetButton(
                                text = "> 100 mq",
                                onClick = {
                                    currentSurfaceRange.value = 100f..500f
                                    minSurface = "100"
                                    maxSurface = "500"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = currentSurfaceRange.value.start == 100f && currentSurfaceRange.value.endInclusive == 500f,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Input fields for surface
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = minSurface,
                                onValueChange = { minSurface = it },
                                label = { Text("Superficie min") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                trailingIcon = {
                                    Text(
                                        text = "mq",
                                        style = typography.bodyMedium,
                                        color = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }
                            )

                            OutlinedTextField(
                                value = maxSurface,
                                onValueChange = { maxSurface = it },
                                label = { Text("Superficie max") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                trailingIcon = {
                                    Text(
                                        text = "mq",
                                        style = typography.bodyMedium,
                                        color = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                // Sezione Locali
                FilterCard(title = "Locali", typography = typography, colorScheme = colorScheme) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Preset buttons for rooms
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PresetButton(
                                text = "1-2",
                                onClick = {
                                    minRooms = "1"
                                    maxRooms = "2"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = minRooms == "1" && maxRooms == "2",
                                modifier = Modifier.weight(1f)
                            )

                            PresetButton(
                                text = "3-4",
                                onClick = {
                                    minRooms = "3"
                                    maxRooms = "4"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = minRooms == "3" && maxRooms == "4",
                                modifier = Modifier.weight(1f)
                            )

                            PresetButton(
                                text = "5+",
                                onClick = {
                                    minRooms = "5"
                                    maxRooms = ""
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                isSelected = minRooms == "5" && maxRooms == "",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Input fields for rooms
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = minRooms,
                                onValueChange = { minRooms = it },
                                label = { Text("Locali min") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = maxRooms,
                                onValueChange = { maxRooms = it },
                                label = { Text("Locali max") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                }

                // Sezione Bagni
                FilterCard(title = "Bagni", typography = typography, colorScheme = colorScheme) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, 4).forEach { count ->
                                val isSelected = selectedBathrooms == count
                                BathroomButton(
                                    text = if (count < 4) count.toString() else "4+",
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedBathrooms = if (isSelected) null else count
                                    },
                                    colorScheme = colorScheme,
                                    typography = typography,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Sezione Stato immobile
                FilterCard(title = "Stato immobile", typography = typography, colorScheme = colorScheme) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Prima riga di opzioni
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PropertyConditionButton(
                                text = "Nuovo",
                                isSelected = selectedCondition == "Nuovo",
                                onClick = {
                                    selectedCondition = if (selectedCondition == "Nuovo") null else "Nuovo"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                modifier = Modifier.weight(1f)
                            )

                            PropertyConditionButton(
                                text = "Ottimo",
                                isSelected = selectedCondition == "Ottimo",
                                onClick = {
                                    selectedCondition = if (selectedCondition == "Ottimo") null else "Ottimo"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Seconda riga di opzioni
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PropertyConditionButton(
                                text = "Buono",
                                isSelected = selectedCondition == "Buono",
                                onClick = {
                                    selectedCondition = if (selectedCondition == "Buono") null else "Buono"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                modifier = Modifier.weight(1f)
                            )

                            PropertyConditionButton(
                                text = "Da ristrutturare",
                                isSelected = selectedCondition == "Da ristrutturare",
                                onClick = {
                                    selectedCondition = if (selectedCondition == "Da ristrutturare") null else "Da ristrutturare"
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Spazio aggiuntivo dopo l'ultimo filtro per garantire che il contenuto non sia coperto dal bottone
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

/**
 * Componente card per filtrare le sezioni
 */
@Composable
fun FilterCard(
    title: String,
    typography: Typography,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

/**
 * Bottone per i preset rapidi
 */
@Composable
fun PresetButton(
    text: String,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.2f)
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Bottone per i bagni
 */
@Composable
fun BathroomButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant.copy(alpha = 0.7f),
            contentColor = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 4.dp else 0.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Bottone per le condizioni immobile
 */
@Composable
fun PropertyConditionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.3f)
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            style = typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterScreen() {
    val navController = rememberNavController()
    SearchFilterScreen(
        navController = navController,
        idUtente = "utente",
        ricerca = "varcaturo"
    )
}