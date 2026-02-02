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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.data.model.FilterOriginScreen
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.ui.components.AppPropertyViewButton
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
    filters: FilterModel? = null, // Filtri passati dalla navigazione
    viewModel: ApartmentListingViewModel = viewModel()
) {
    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme

    // Stato dal ViewModel
    val immobili by viewModel.immobili.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Stato locale per i filtri (inizialmente quelli passati, poi modificabili)
    var activeFilters by remember { mutableStateOf(filters) }

    // Bottom Sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showFilterSheet by remember { mutableStateOf(false) }

    // Caricamento Iniziale
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
        // --- HEADER ---
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

        // --- CONTENT ---
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorScheme.onPrimary)
            }
        } else if (error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error ?: "Errore sconosciuto", color = colorScheme.error)
            }
        } else if (immobili.isEmpty()) {
            EmptyStateView(comune, activeFilters) {
                activeFilters = null // Reset filtri
            }
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
                    RealPropertyCard(
                        immobile = property,
                        onClick = {
                            navController.navigate(Screen.PropertyScreen.withId(property.id))
                        }
                    )
                }
            }
        }
    }

    // --- BOTTOM SHEET FILTRI (REALE) ---
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
                    .defaultMinSize(minHeight = 400.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Riusiamo la schermata dei filtri esistente
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
                        // Il LaunchedEffect ricaricherà automaticamente i dati
                    },
                    isFullScreenContext = false,
                    originScreen = FilterOriginScreen.APARTMENT_LISTING
                )
            }
        }
    }
}

// --- UTILS UI ---

@Composable
fun ResultsHeader(count: Int, activeFilters: Boolean, comune: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Risultati", // O "Annunci a $comune"
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (activeFilters) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
    val dimensions = Dimensions
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Immagine (Parte Superiore)
            Box(modifier = Modifier.weight(0.6f)) {
                // Usiamo l'URL della prima immagine se presente
                val imageUrl = immobile.immagini.firstOrNull()?.url

                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                // Prezzo Overlay
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(topStart = dimensions.cornerRadiusMedium, bottomEnd = dimensions.cornerRadiusMedium),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    val prezzoFormat = if (immobile.tipoVendita)
                        "€ ${immobile.prezzo?.let { String.format("%,d", it) } ?: "-"}"
                    else
                        "€ ${immobile.prezzo}/mese"

                    Text(
                        text = prezzoFormat,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Dettagli (Parte Inferiore)
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = immobile.categoria ?: "Immobile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = immobile.indirizzo ?: immobile.descrizione ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val detailsList = mutableListOf<String>()
                    immobile.mq?.let { detailsList.add("${it}mq") }

                    // Conta stanze (Ambienti non 'BAGNO' o 'CUCINA' se vuoi essere specifico, oppure totale)
                    // Per semplicità qui mostriamo solo mq e bagni se disponibili
                    // Se vuoi contare le stanze: immobile.ambienti.sumOf { it.numero }

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
