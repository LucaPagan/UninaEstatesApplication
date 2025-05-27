package com.dieti.dietiestates25.ui.screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign // Importato se usato
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.model.FilterModel // Assicurati che il path sia corretto
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.theme.Typography // Assicurati che il path sia corretto
import com.dieti.dietiestates25.ui.utils.findActivity // La tua funzione helper
import com.google.android.gms.maps.CameraUpdateFactory // <<-- IMPORT AGGIUNTO
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch // <<-- IMPORT AGGIUNTO per launch

// Data class per i marker delle proprietà (mantenuta per l'esempio)
data class PropertyMarker(
    val position: LatLng,
    val title: String,
    val price: String,
    val type: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    navController: NavController,
    idUtente: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    // Stato per la mappa
    val napoli = LatLng(40.8518, 14.2681)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(napoli, 12f)
    }

    // Stati per la ricerca e i filtri
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var appliedFilters by remember { mutableStateOf<FilterModel?>(null) } // Per il badge

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope() // <<-- AGGIUNTO CoroutineScope

    // Proprietà di esempio per i marker
    val sampleProperties = remember {
        listOf(
            PropertyMarker(LatLng(40.8518, 14.2681), "Appartamento Centro", "€850/mese", "2 locali"),
            PropertyMarker(LatLng(40.8400, 14.2500), "Monolocale Vomero", "€650/mese", "1 locale"),
            PropertyMarker(LatLng(40.8600, 14.2800), "Trilocale Chiaia", "€1200/mese", "3 locali")
        )
    }

    // Gestione Status Bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            activity?.window?.let { window ->
                window.statusBarColor = colorScheme.primary.toArgb() // Colore primario per la status bar
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Icone chiare
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Scaffold(
        modifier = Modifier.statusBarsPadding(), // Padding per la status bar
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cerca indirizzo, città...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .height(dimensions.buttonHeight - dimensions.spacingMedium), // Altezza più contenuta
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = colorScheme.onPrimary,
                            unfocusedTextColor = colorScheme.onPrimary.copy(alpha = 0.9f),
                            cursorColor = colorScheme.onPrimary,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = colorScheme.onPrimary.copy(alpha = 0.15f),
                            unfocusedContainerColor = colorScheme.onPrimary.copy(alpha = 0.15f),
                            focusedLeadingIconColor = colorScheme.onPrimary,
                            unfocusedLeadingIconColor = colorScheme.onPrimary.copy(alpha = 0.7f),
                            focusedPlaceholderColor = colorScheme.onPrimary.copy(alpha = 0.7f),
                            unfocusedPlaceholderColor = colorScheme.onPrimary.copy(alpha = 0.7f)
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Icona Cerca")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (searchQuery.isNotBlank()) {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                // TODO: Implementa la logica di ricerca sulla mappa (geocoding + fetch properties)
                                println("Avvio ricerca mappa per: $searchQuery")
                                // Esempio: muovi la camera (dovrai implementare geocoding)
                                // coroutineScope.launch {
                                // cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(NEW_LAT, NEW_LON), 15f))
                                // }
                            }
                        })
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filtri",
                                tint = colorScheme.onPrimary
                            )
                        }
                        if (appliedFilters != null) {
                            Badge(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = dimensions.spacingExtraSmall, y = -(dimensions.spacingExtraSmall)),
                                containerColor = colorScheme.error
                            ) {}
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: Implementa la logica per ottenere la posizione corrente e centrare la mappa
                    println("FAB Posizione Corrente cliccato")
                },
                containerColor = colorScheme.secondaryContainer,
                contentColor = colorScheme.onSecondaryContainer,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(Icons.Filled.MyLocation, "La Mia Posizione")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = false, // TODO: Gestisci permesso e abilita se concesso
                mapType = MapType.NORMAL,
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = true,
                mapToolbarEnabled = false,
                compassEnabled = true
            ),
            onMapClick = { latLng ->
                println("Mappa cliccata: $latLng")
                // TODO: Potresti voler fare reverse geocoding o mostrare info
            },
            onPOIClick = { poi ->
                println("POI Cliccato: ${poi.name} at ${poi.latLng}")
            }
        ) {
            sampleProperties.forEach { property ->
                Marker(
                    state = MarkerState(position = property.position),
                    title = property.title,
                    snippet = "${property.price} - ${property.type}",
                    onClick = { marker ->
                        println("Marker cliccato: ${marker.title}")
                        coroutineScope.launch { // <<-- CORREZIONE QUI
                            cameraPositionState.animate(CameraUpdateFactory.newLatLng(marker.position), 500)
                        }
                        true // Consuma l'evento di click
                    }
                )
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = colorScheme.background,
            shape = RoundedCornerShape(topStart = dimensions.cornerRadiusLarge, topEnd = dimensions.cornerRadiusLarge),
            scrimColor = Color.Black.copy(alpha = 0.32f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = dimensions.buttonHeight * 10)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                SearchFilterScreen(
                    navController = navController,
                    idUtente = idUtente,
                    comune = "",
                    ricercaQueryText = searchQuery,
                    onNavigateBack = { showFilterSheet = false },
                    onApplyFilters = { filterData ->
                        appliedFilters = filterData
                        showFilterSheet = false
                        println("Filtri da applicare dalla mappa: $filterData")
                    },
                    isFullScreenContext = false
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun MapSearchScreenPreview() {
    DietiEstatesTheme {
        MapSearchScreen(
            navController = rememberNavController(),
            idUtente = "previewUser"
        )
    }
}
