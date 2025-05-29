package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.PropertyPreviewInfoWindow
import com.dieti.dietiestates25.ui.components.CustomPriceMarker
import com.dieti.dietiestates25.ui.model.FilterModel
import com.dieti.dietiestates25.ui.model.PropertyMarker
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.capitalizeFirstLetter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    navController: NavController,
    idUtente: String,
    comune: String,
    ricerca: String,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val context = LocalContext.current

    // Stato per la mappa
    val initialMapCenter = remember(comune) { LatLng(40.8518, 14.2681) } // Default: Napoli
    val initialMapZoom = 12f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialMapCenter, initialMapZoom)
    }

    // Stati per i filtri
    var showFilterSheet by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var initialFiltersFromNav = remember(currentBackStackEntry) {
        currentBackStackEntry?.arguments?.let { args ->
            val pType = args.getString("purchaseType")
            val mPrice = args.getFloat("minPrice").takeIf { it != -1f && it != 0f }
            val mxPrice = args.getFloat("maxPrice").takeIf { it != -1f }
            val mSurf = args.getFloat("minSurface").takeIf { it != -1f && it != 0f }
            val mxSurf = args.getFloat("maxSurface").takeIf { it != -1f }
            val mRooms = args.getInt("minRooms").takeIf { it != -1 }
            val mxRooms = args.getInt("maxRooms").takeIf { it != -1 }
            val baths = args.getInt("bathrooms").takeIf { it != -1 }
            val cond = args.getString("condition")

            if (pType != null || mPrice != null || mxPrice != null || mSurf != null || mxSurf != null || mRooms != null || mxRooms != null || baths != null || cond != null) {
                FilterModel(
                    purchaseType = pType, minPrice = mPrice, maxPrice = mxPrice,
                    minSurface = mSurf, maxSurface = mxSurf, minRooms = mRooms,
                    maxRooms = mxRooms, bathrooms = baths, condition = cond
                )
            } else { null }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // Stato per gestire il marker selezionato e la sua preview
    var selectedProperty by remember { mutableStateOf<PropertyMarker?>(null) }

    // Stato per il livello di zoom corrente
    val currentZoom by remember {
        derivedStateOf { cameraPositionState.position.zoom }
    }

    // Lista delle proprietà da mostrare con dati più completi
    val propertiesToDisplay = remember(comune, ricerca, initialFiltersFromNav) {
        listOf(
            PropertyMarker(
                id = "1",
                position = LatLng(40.8518, 14.2681),
                title = "Appartamento Centro Storico",
                price = "€850/mese",
                type = "2 locali",
                imageRes = PropertyMarker.getPropertyImage("1"), // property1
                description = "Splendido appartamento nel cuore del centro storico di Napoli",
                surface = "85 m²",
                bathrooms = 1,
                bedrooms = 2
            ),
            PropertyMarker(
                id = "2",
                position = LatLng(40.8400, 14.2500),
                title = "Monolocale Vomero",
                price = "€650/mese",
                type = "1 locale",
                imageRes = PropertyMarker.getPropertyImage("2"), // property2
                description = "Accogliente monolocale nella zona residenziale del Vomero",
                surface = "45 m²",
                bathrooms = 1,
                bedrooms = 1
            ),
            PropertyMarker(
                id = "3",
                position = LatLng(40.8600, 14.2800),
                title = "Trilocale Chiaia",
                price = "€1200/mese",
                type = "3 locali",
                imageRes = PropertyMarker.getPropertyImage("3"), // property1 come fallback
                description = "Elegante trilocale nella prestigiosa zona di Chiaia",
                surface = "120 m²",
                bathrooms = 2,
                bedrooms = 3
            )
        ).filter { true }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = comune.capitalizeFirstLetter() +
                                if (ricerca.isNotBlank() && ricerca.lowercase() != comune.lowercase()) {
                                    " - ${ricerca.capitalizeFirstLetter()}"
                                } else { "" },
                        style = typography.titleMedium,
                        color = colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                        if (initialFiltersFromNav != null) {
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
                    containerColor = colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
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
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = true,
                    mapToolbarEnabled = false,
                    compassEnabled = true
                ),
                onMapClick = { latLng ->
                    // Nasconde la preview quando si clicca sulla mappa
                    selectedProperty = null
                    println("Mappa cliccata in posizione: $latLng")
                },
                onPOIClick = { poi ->
                    println("POI Cliccato: ${poi.name} @ ${poi.latLng}")
                }
            ) {
                // Mostra i marker solo se il livello di zoom è appropriato
                if (currentZoom >= 10f) {
                    propertiesToDisplay.forEach { property ->
                        MarkerComposable(
                            state = MarkerState(position = property.position),
                            onClick = { marker ->
                                println("Marker personalizzato '${property.title}' cliccato")
                                selectedProperty = property
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(marker.position, 15f),
                                        700
                                    )
                                }
                                true // Consume il click per evitare comportamenti di default
                            }
                        ) {
                            CustomPriceMarker(
                                price = property.price,
                                isSelected = selectedProperty?.id == property.id,
                                colorScheme = colorScheme,
                                typography = typography,
                                // Scala il marker in base al livello di zoom
                                scale = when {
                                    currentZoom >= 15f -> 1f
                                    currentZoom >= 13f -> 0.9f
                                    currentZoom >= 11f -> 0.8f
                                    else -> 0.7f
                                }
                            )
                        }
                    }
                }
            }

            // Preview della proprietà selezionata
            selectedProperty?.let { property ->
                PropertyPreviewInfoWindow(
                    property = property,
                    onClick = {
                        navController.navigate(Screen.PropertyScreen.route)
                    },
                    onClose = {
                        selectedProperty = null
                    },
                    dimensions = dimensions,
                    typography = typography,
                    colorScheme = colorScheme,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(dimensions.spacingMedium)
                        .padding(bottom = dimensions.spacingLarge * 2) // Spazio per il FAB
                )
            }
        }
    }

    // ModalBottomSheet per i Filtri
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = colorScheme.background,
            shape = RoundedCornerShape(
                topStart = dimensions.cornerRadiusLarge,
                topEnd = dimensions.cornerRadiusLarge
            ),
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
                    comune = comune,
                    ricercaQueryText = ricerca,
                    onNavigateBack = { showFilterSheet = false },
                    onApplyFilters = { filterData ->
                        initialFiltersFromNav = filterData
                        showFilterSheet = false
                        println("MapSearchScreen - Filtri da applicare: $filterData")
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
            idUtente = "previewUser",
            comune = "Napoli",
            ricerca = "Centro", // Nome parametro corretto
        )
    }
}