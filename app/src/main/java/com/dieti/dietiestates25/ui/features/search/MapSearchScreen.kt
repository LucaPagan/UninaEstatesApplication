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
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.ui.components.PropertyPreviewInfoWindow
import com.dieti.dietiestates25.ui.components.CustomPriceMarker
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
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
    val initialMapCenter = remember {
        // Default provvisorio su Napoli, verrà aggiornato dai risultati
        LatLng(40.8518, 14.2681)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialMapCenter, 12f)
    }

    // Flag per gestire la centratura automatica della mappa sui risultati
    var hasCenteredOnResults by remember { mutableStateOf(false) }

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
                // ... altri mapping ...

                if (pType != null || mPrice != null || mxPrice != null) {
                    appliedFilters = FilterModel(
                        purchaseType = pType, minPrice = mPrice, maxPrice = mxPrice
                    )
                }
            }
        }
    }

    // Caricamento iniziale e reset centratura se cambiano i parametri di ricerca
    LaunchedEffect(comune, ricerca, appliedFilters) {
        hasCenteredOnResults = false // Resetta il flag per permettere la centratura sui nuovi risultati
        viewModel.loadProperties(query = "$comune $ricerca", filters = appliedFilters)
    }

    // Logica di centratura automatica sui risultati
    LaunchedEffect(properties) {
        if (properties.isNotEmpty() && !hasCenteredOnResults && !cameraPositionState.isMoving) {
            val firstProperty = properties.first()
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(firstProperty.position, 13f),
                1000
            )
            hasCenteredOnResults = true
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
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        mapToolbarEnabled = false
                    ),
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
                } else if (properties.isEmpty() && error == null) {
                    // Messaggio se non ci sono risultati
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "Nessun immobile in questa zona",
                            modifier = Modifier.padding(16.dp),
                            style = typography.bodyMedium
                        )
                    }
                }

                // Preview Immobile (Card in basso)
                selectedProperty?.let { property ->
                    PropertyPreviewInfoWindow(
                        property = property,
                        onClick = {
                            // NAVIGAZIONE IMPLEMENTATA: Passa l'ID corretto alla PropertyScreen
                            navController.navigate(Screen.PropertyScreen.withId(property.id))
                        },
                        onClose = { selectedProperty = null },
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 200.dp, start = 16.dp, end = 16.dp)
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
                            hasCenteredOnResults = false // Reset per centrare sui nuovi risultati filtrati
                            // Ricarica con nuovi filtri
                            viewModel.loadProperties(
                                query = "$comune $ricerca",
                                filters = filterData,
                                // Se la ricerca è vuota (solo mappa), usa il centro attuale, altrimenti usa la query
                                searchArea = if (ricerca.isEmpty() && comune.isEmpty()) cameraPositionState.position.target else null
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