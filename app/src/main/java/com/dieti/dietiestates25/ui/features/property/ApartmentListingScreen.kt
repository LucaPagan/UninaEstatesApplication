package com.dieti.dietiestates25.ui.features.property

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.model.FilterOriginScreen
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.components.AppPropertyCard
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.features.search.SearchFilterScreen
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentListingScreen(
    navController: NavController,
    idUtente: String,
    comune: String,
    ricerca: String,
    filters: FilterModel? = null,
    viewModel: ApartmentListingViewModel = viewModel()
) {
    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme

    val immobili by viewModel.immobili.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var activeFilters by remember { mutableStateOf(filters) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(comune, ricerca, activeFilters) {
        viewModel.loadImmobili(comune, ricerca, activeFilters)
    }

    val gradientColors = arrayOf(
        0.0f to colorScheme.primary,
        0.20f to colorScheme.background,
        0.60f to colorScheme.background,
        1.0f to colorScheme.primary
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colorStops = gradientColors))
    ) {
        GeneralHeaderBar(
            title = if (ricerca.isNotBlank()) "$comune - $ricerca" else comune,
            onBackClick = { navController.popBackStack() },
            actions = {
                Box {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filtra",
                            tint = colorScheme.onPrimary
                        )
                    }
                    if (activeFilters != null) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp, end = 4.dp),
                            containerColor = colorScheme.error
                        )
                    }
                }
            }
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorScheme.onPrimary)
            }
        } else if (error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error ?: "Errore sconosciuto", color = colorScheme.error)
            }
        } else if (immobili.isEmpty()) {
            EmptyStateView(comune, activeFilters) { activeFilters = null }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = dimensions.paddingMedium,
                    vertical = dimensions.paddingLarge
                ),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                item {
                    ResultsHeader(count = immobili.size, activeFilters = activeFilters != null, comune = comune)
                }

                items(items = immobili, key = { it.id }) { property ->
                    // FIX: Visualizzazione uniforme con la Home
                    // Usiamo AppPropertyCard invece di RealPropertyCard per consistenza
                    val imageUrl = property.immagini.firstOrNull()?.url?.let { RetrofitClient.getFullUrl(it) }

                    // Calcolo bagni per dettagli
                    val bagni = property.ambienti
                        .filter { it.tipologia.contains("bagno", ignoreCase = true) }
                        .sumOf { it.numero }

                    val details = mutableListOf<String>().apply {
                        property.categoria?.let { add(it) }
                        property.mq?.let { add("$it mq") }
                        if (bagni > 0) add("$bagni bagni")
                    }

                    AppPropertyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp), // Altezza adatta per listing verticale
                        price = if (property.tipoVendita) "€ ${property.prezzo?.let { String.format("%,d", it) } ?: "-"}" else "€ ${property.prezzo}/mese",
                        imageUrl = imageUrl,
                        address = property.indirizzo ?: property.localita ?: "Zona non specificata",
                        details = details,
                        onClick = { navController.navigate(Screen.PropertyScreen.withId(property.id)) },
                        horizontalMode = false
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(topStart = dimensions.cornerRadiusLarge, topEnd = dimensions.cornerRadiusLarge)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 400.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                SearchFilterScreen(
                    navController = navController,
                    idUtente = idUtente,
                    comune = comune,
                    ricercaQueryText = ricerca,
                    initialFilters = activeFilters,
                    onNavigateBack = { showFilterSheet = false },
                    onApplyFilters = { newFilters ->
                        activeFilters = newFilters
                        showFilterSheet = false
                    },
                    isFullScreenContext = false,
                    originScreen = FilterOriginScreen.APARTMENT_LISTING
                )
            }
        }
    }
}

@Composable
fun ResultsHeader(count: Int, activeFilters: Boolean, comune: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Risultati",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (activeFilters) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(
                    text = "$count trovati",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(comune: String, activeFilters: FilterModel?, onReset: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = if (activeFilters != null) "Nessun immobile trovato con questi filtri." else "Nessun immobile disponibile a \"$comune\".",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (activeFilters != null) {
                Button(onClick = onReset) { Text("Resetta Filtri") }
            }
        }
    }
}