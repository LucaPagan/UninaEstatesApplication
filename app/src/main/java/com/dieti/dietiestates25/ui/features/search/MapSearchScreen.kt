package com.dieti.dietiestates25.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.PropertyPreviewInfoWindow
import com.dieti.dietiestates25.ui.components.CustomPriceMarker
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.model.FilterOriginScreen
import com.dieti.dietiestates25.data.model.PropertyMarker
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
    viewModel: MapSearchViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val originScreen = FilterOriginScreen.MAP_SEARCH

    // Osserva i dati dal ViewModel
    val properties by viewModel.properties.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Stato Mappa
    val initialMapCenter = remember(comune) {
        // Default provvisorio su Napoli, idealmente geocodifichi il comune all'avvio o usi il primo risultato
        LatLng(40.8518, 14.2681)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialMapCenter, 12f)
    }

    // Filtri
    var showFilterSheet by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var appliedFilters by remember { mutableStateOf<FilterModel?>(null) }

    // Init Filtri
    LaunchedEffect(currentBackStackEntry) {
        if (appliedFilters == null) {
            currentBackStackEntry?.arguments?.let { args ->
                val pType = args.getString("purchaseType")
                val mPrice = args.getFloat("minPrice").takeIf { it != -1f && it != 0f }
                val mxPrice = args.getFloat("maxPrice").takeIf { it != -1f }
                // ... altri mapping uguali ...

                if (pType != null || mPrice != null || mxPrice != null) {
                    appliedFilters = FilterModel(
                        purchaseType = pType, minPrice = mPrice, maxPrice = mxPrice
                        // ... assegna altri ...
                    )
                }
            }
        }
    }

    // Caricamento iniziale
    LaunchedEffect(comune, ricerca, appliedFilters) {
        viewModel.loadProperties(query = "$comune $ricerca", filters = appliedFilters)
    }

    // Se abbiamo risultati e la mappa è appena caricata, centra sul primo risultato
    LaunchedEffect(properties) {
        if (properties.isNotEmpty() && !cameraPositionState.isMoving) {
            // Opzionale: muovi camera solo se è la prima ricerca
            // cameraPositionState.move(CameraUpdateFactory.newLatLng(properties[0].position))
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var selectedProperty by remember { mutableStateOf<PropertyMarker?>(null) }
    val currentZoom by remember { derivedStateOf { cameraPositionState.position.zoom } }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surfaceDim)
    ) {
        Scaffold(
            topBar = {
                GeneralHeaderBar(
                    title = if (comune.isNotBlank()) comune else "Mappa",
                    onBackClick = { navController.popBackStack() },
                    actions = {
                        Box {
                            IconButton(onClick = { showFilterSheet = true }) {
                                Icon(Icons.Default.Tune, "Filtra", tint = colorScheme.onPrimary)
                            }
                            if (appliedFilters != null) {
                                Badge(
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
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
                    horizontalAlignment = Alignment.End
                ) {
                    // Tasto "Cerca in questa zona"
                    ExtendedFloatingActionButton(
                        onClick = {
                            val center = cameraPositionState.position.target
                            // Calcola raggio approssimativo in base allo zoom (semplificato)
                            val radiusKm = 150000.0 / Math.pow(2.0, cameraPositionState.position.zoom.toDouble()) / 1000.0

                            viewModel.loadProperties(
                                query = "",
                                filters = appliedFilters,
                                searchArea = center,
                                radiusKm = radiusKm.coerceAtLeast(1.0) // Minimo 1km
                            )
                        },
                        containerColor = colorScheme.primaryContainer,
                        contentColor = colorScheme.onPrimaryContainer,
                        icon = { Icon(Icons.Default.Search, null) },
                        text = { Text("Cerca qui") }
                    )

                    FloatingActionButton(
                        onClick = {
                            // Reset filtri
                            appliedFilters = null
                            selectedProperty = null
                            viewModel.loadProperties("$comune $ricerca")
                        },
                        containerColor = colorScheme.secondaryContainer
                    ) {
                        Icon(Icons.Filled.FilterList, "Reset")
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false),
                    onMapClick = { selectedProperty = null }
                ) {
                    properties.forEach { property ->
                        MarkerComposable(
                            state = MarkerState(position = property.position),
                            onClick = {
                                selectedProperty = property
                                coroutineScope.launch {
                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(property.position, 15f))
                                }
                                true
                            }
                        ) {
                            CustomPriceMarker(
                                price = property.price,
                                isSelected = selectedProperty?.id == property.id,
                                colorScheme = colorScheme,
                                typography = typography,
                                scale = if (currentZoom > 12f) 1f else 0.8f
                            )
                        }
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colorScheme.primary
                    )
                }

                // Preview Immobile
                selectedProperty?.let { property ->
                    PropertyPreviewInfoWindow(
                        property = property,
                        onClick = { navController.navigate(Screen.PropertyScreen.route) }, // Passa ID immobile reale qui
                        onClose = { selectedProperty = null },
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
        }

        // Sheet Filtri
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 400.dp)) {
                    SearchFilterScreen(
                        navController = navController,
                        idUtente = idUtente,
                        comune = comune,
                        ricercaQueryText = ricerca,
                        initialFilters = appliedFilters,
                        onNavigateBack = { showFilterSheet = false },
                        onApplyFilters = { filterData ->
                            appliedFilters = filterData
                            showFilterSheet = false
                            // Ricarica con nuovi filtri
                            viewModel.loadProperties(
                                query = "$comune $ricerca",
                                filters = filterData,
                                searchArea = if (ricerca.isEmpty()) cameraPositionState.position.target else null
                            )
                        },
                        isFullScreenContext = false,
                        originScreen = originScreen
                    )
                }
            }
        }
    }
}