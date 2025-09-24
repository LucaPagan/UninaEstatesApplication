package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.PropertyPreviewInfoWindow
import com.dieti.dietiestates25.ui.components.CustomPriceMarker
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.model.FilterModel
import com.dieti.dietiestates25.ui.model.FilterOriginScreen
import com.dieti.dietiestates25.ui.model.PropertyMarker
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
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
    val originScreen = FilterOriginScreen.MAP_SEARCH

    // Stato per la mappa
    val initialMapCenter = remember(comune) { LatLng(40.8518, 14.2681) } // Default: Napoli
    val initialMapZoom = 12f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialMapCenter, initialMapZoom)
    }

    // Stati per i filtri
    var showFilterSheet by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // Gestione dei filtri con stato reattivo
    var appliedFilters by remember { mutableStateOf<FilterModel?>(null) }

    // Inizializza i filtri dai parametri di navigazione una sola volta
    LaunchedEffect(currentBackStackEntry) {
        if (appliedFilters == null) {
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

                if (pType != null || mPrice != null || mxPrice != null || mSurf != null ||
                    mxSurf != null || mRooms != null || mxRooms != null || baths != null || cond != null) {
                    appliedFilters = FilterModel(
                        purchaseType = pType, minPrice = mPrice, maxPrice = mxPrice,
                        minSurface = mSurf, maxSurface = mxSurf, minRooms = mRooms,
                        maxRooms = mxRooms, bathrooms = baths, condition = cond
                    )
                }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // Stato per gestire il marker selezionato e la sua preview
    var selectedProperty by remember { mutableStateOf<PropertyMarker?>(null) }

    // Stato per il livello di zoom corrente
    val currentZoom by remember {
        derivedStateOf { cameraPositionState.position.zoom }
    }

    // Lista completa delle proprietà (simulata)
    val allProperties = remember {
        listOf(
            PropertyMarker(
                id = "1",
                position = LatLng(40.8518, 14.2681),
                title = "Appartamento Centro Storico",
                price = "€850/mese",
                type = "2 locali",
                imageRes = PropertyMarker.getPropertyImage("1"),
                description = "Splendido appartamento nel cuore del centro storico di Napoli",
                surface = "85 m²",
                bathrooms = 1,
                bedrooms = 2,
                purchaseType = "affitto",
                condition = "ottimo",
                priceValue = 850,
                surfaceValue = 85
            ),
            PropertyMarker(
                id = "2",
                position = LatLng(40.8400, 14.2500),
                title = "Monolocale Vomero",
                price = "€650/mese",
                type = "1 locale",
                imageRes = PropertyMarker.getPropertyImage("2"),
                description = "Accogliente monolocale nella zona residenziale del Vomero",
                surface = "45 m²",
                bathrooms = 1,
                bedrooms = 1,
                purchaseType = "affitto",
                condition = "buono",
                priceValue = 650,
                surfaceValue = 45
            ),
            PropertyMarker(
                id = "3",
                position = LatLng(40.8600, 14.2800),
                title = "Trilocale Chiaia",
                price = "€1200/mese",
                type = "3 locali",
                imageRes = PropertyMarker.getPropertyImage("3"),
                description = "Elegante trilocale nella prestigiosa zona di Chiaia",
                surface = "120 m²",
                bathrooms = 2,
                bedrooms = 3,
                purchaseType = "affitto",
                condition = "eccellente",
                priceValue = 1200,
                surfaceValue = 120
            ),
            PropertyMarker(
                id = "4",
                position = LatLng(40.8350, 14.2450),
                title = "Villa Posillipo",
                price = "€450.000",
                type = "Villa",
                imageRes = PropertyMarker.getPropertyImage("1"),
                description = "Splendida villa con vista mare a Posillipo",
                surface = "200 m²",
                bathrooms = 3,
                bedrooms = 4,
                purchaseType = "vendita",
                condition = "eccellente",
                priceValue = 450000,
                surfaceValue = 200
            ),
            PropertyMarker(
                id = "5",
                position = LatLng(40.8550, 14.2750),
                title = "Loft Moderno",
                price = "€900/mese",
                type = "Loft",
                imageRes = PropertyMarker.getPropertyImage("2"),
                description = "Loft moderno con design contemporaneo",
                surface = "95 m²",
                bathrooms = 2,
                bedrooms = 1,
                purchaseType = "affitto",
                condition = "nuovo",
                priceValue = 900,
                surfaceValue = 95
            )
        )
    }

    // Filtra le proprietà in base ai filtri applicati
    val propertiesToDisplay = remember(appliedFilters, comune, ricerca) {
        allProperties.filter { property ->
            appliedFilters?.let { filters ->
                // Filtra per tipo di acquisto
                if (filters.purchaseType != null && property.purchaseType != filters.purchaseType) {
                    return@filter false
                }

                // Filtra per prezzo
                if (filters.minPrice != null && property.priceValue < filters.minPrice) {
                    return@filter false
                }
                if (filters.maxPrice != null && property.priceValue > filters.maxPrice) {
                    return@filter false
                }

                // Filtra per superficie
                if (filters.minSurface != null && property.surfaceValue < filters.minSurface) {
                    return@filter false
                }
                if (filters.maxSurface != null && property.surfaceValue > filters.maxSurface) {
                    return@filter false
                }

                // Filtra per stanze
                if (filters.minRooms != null && property.bedrooms < filters.minRooms) {
                    return@filter false
                }
                if (filters.maxRooms != null && property.bedrooms > filters.maxRooms) {
                    return@filter false
                }

                // Filtra per bagni
                if (filters.bathrooms != null && property.bathrooms < filters.bathrooms) {
                    return@filter false
                }

                // Filtra per condizione
                if (filters.condition != null && property.condition != filters.condition) {
                    return@filter false
                }

                true
            } ?: true // Se non ci sono filtri, mostra tutto
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surfaceDim)
    ) {
        Scaffold(
            topBar = {
                GeneralHeaderBar(
                    title = comune,
                    onBackClick = { navController.popBackStack() },
                    actions = {
                        //Il pulsante per i filtri e il badge sono ora inseriti nello slot "actions".
                        Box {
                            IconButton(onClick = { showFilterSheet = true }) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filtra",
                                    tint = colorScheme.onPrimary
                                )
                            }
                            if (appliedFilters != null) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = dimensions.paddingExtraSmall, end = dimensions.paddingExtraSmall),
                                    containerColor = colorScheme.error
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall),
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    // Pulsante per resettare i filtri (se applicati)
                    if (appliedFilters != null) {
                        FloatingActionButton(
                            onClick = {
                                appliedFilters = null
                                selectedProperty = null
                                println("Filtri resettati")
                            },
                            containerColor = colorScheme.errorContainer,
                            contentColor = colorScheme.onErrorContainer,
                            modifier = Modifier.size(dimensions.buttonHeight)
                        ) {
                            Icon(Icons.Filled.FilterList, "Reset Filtri")
                        }
                    }

                    // Pulsante posizione corrente
                    FloatingActionButton(
                        onClick = {
                            println("FAB Posizione Corrente cliccato")
                            // Qui puoi implementare la logica per centrare sulla posizione corrente
                        },
                        containerColor = colorScheme.secondaryContainer,
                        contentColor = colorScheme.onSecondaryContainer
                    ) {
                        Icon(Icons.Filled.MyLocation, "La Mia Posizione")
                    }
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
                            .padding(bottom = dimensions.spacingLarge * 4) // Spazio per i FAB
                    )
                }

                // Indicatore del numero di risultati filtrati
                if (appliedFilters != null) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = dimensions.spacingMedium),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = "${propertiesToDisplay.size} proprietà trovate",
                            style = typography.bodyMedium,
                            color = colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(
                                horizontal = dimensions.spacingMedium,
                                vertical = dimensions.spacingSmall
                            )
                        )
                    }
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
                        initialFilters = appliedFilters, // Passa i filtri correnti
                        onNavigateBack = { showFilterSheet = false },
                        onApplyFilters = { filterData ->
                            appliedFilters = filterData
                            selectedProperty = null // Reset selezione
                            showFilterSheet = false
                            println("MapSearchScreen - Filtri applicati: $filterData")
                        },
                        isFullScreenContext = false,
                        originScreen = originScreen
                    )
                }
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
            ricerca = "Centro",
        )
    }
}