package com.dieti.dietiestates25.ui.features.property

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.model.FilterOriginScreen
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.components.AppPropertyViewButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.features.search.SearchFilterScreen
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentListingScreen(
    navController: NavController,
    idUtente: String,
    comune: String,
    ricerca: String,
    viewModel: ApartmentListingViewModel = viewModel()
) {
    // Osserva lo stato dal ViewModel
    val immobili by viewModel.filteredImmobili.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activeFilters by viewModel.currentFilters.collectAsState()

    // Effettua la chiamata API all'avvio (o quando cambiano i parametri di ricerca)
    LaunchedEffect(comune, ricerca) {
        viewModel.fetchImmobili(comune, ricerca)
    }

    // UI Configuration
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions
    val originScreen = FilterOriginScreen.APARTMENT_LISTING

    // Bottom Sheet State
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showFilterSheet by remember { mutableStateOf(false) }

    // Sfondo Gradiente
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
        // --- HEADER BAR ---
        GeneralHeaderBar(
            title = comune,
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

        // --- CONTENT AREA ---
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (immobili.isEmpty()) {
            EmptyStateView(comune, activeFilters) {
                viewModel.updateFilters(null) // Reset filters logic
            }
        } else {
            // Lista dei risultati
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = dimensions.paddingMedium,
                    vertical = dimensions.paddingLarge
                ),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                item {
                    ResultsHeader(count = immobili.size, activeFilters = activeFilters, comune = comune)
                }

                items(items = immobili, key = { it.id }) { property ->
                    RealPropertyCard(
                        immobile = property,
                        onClick = {
                            // Navigazione al dettaglio gestendo l'ID come stringa
                            navController.navigate(Screen.PropertyScreen.withId(property.id))
                        }
                    )
                }
            }
        }
    }

    // --- BOTTOM SHEET FILTRI ---
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = colorScheme.background,
            shape = RoundedCornerShape(
                topStart = dimensions.cornerRadiusLarge,
                topEnd = dimensions.cornerRadiusLarge
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 400.dp) // Altezza minima ragionevole
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
                    onApplyFilters = { filterData ->
                        // Passa i dati al ViewModel
                        viewModel.updateFilters(filterData)
                        showFilterSheet = false
                    },
                    isFullScreenContext = false,
                    originScreen = originScreen
                )
            }
        }
    }
}

// --- COMPONENTI UI DI SUPPORTO ---

@Composable
fun ResultsHeader(count: Int, activeFilters: FilterModel?, comune: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Annunci a $comune",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (activeFilters != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
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
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (activeFilters != null)
                    "Nessun immobile trovato con questi filtri."
                else
                    "Nessun immobile disponibile a \"$comune\".",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (activeFilters != null) {
                Button(onClick = onReset) {
                    Text("Resetta Filtri")
                }
            }
        }
    }
}

@Composable
fun RealPropertyCard(immobile: ImmobileDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationSmall),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Immagine (usando Coil e URL remoto)
            Box(modifier = Modifier.weight(0.6f)) {
                // Gestione URL immagine con fallback vuoto se nullo
                val imgUrl = immobile.coverImageId?.let { RetrofitClient.getImageUrl(it) } ?: ""
                AsyncImage(
                    model = imgUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Prezzo overlay
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(topStart = Dimensions.cornerRadiusMedium, bottomEnd = Dimensions.cornerRadiusMedium),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "€ ${immobile.prezzo}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Dettagli
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = immobile.titolo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = immobile.localita ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Costruzione stringa dettagli sicura (gestione null)
                    val detailsList = mutableListOf<String>()
                    immobile.superficie?.let { detailsList.add("${it}mq") }
                    immobile.stanze?.let { detailsList.add("$it stanze") }
                    immobile.bagni?.let { detailsList.add("$it bagni") }

                    Text(
                        text = detailsList.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )

                    AppPropertyViewButton(onClick = onClick)
                }
            }
        }
    }
}
@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun PreviewApartmentListingScreen() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        ApartmentListingScreen(
            navController = navController,
            idUtente = "Danilo",
            comune = "Napoli",
            ricerca = "Centro"
        )
    }
}