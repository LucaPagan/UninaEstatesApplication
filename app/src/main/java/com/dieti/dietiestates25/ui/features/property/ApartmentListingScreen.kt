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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.ui.components.AppPropertyViewButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import kotlinx.coroutines.delay

// --- DATI MOCK LOCALI (Sostituiscono DTO e Modelli Esterni) ---
data class FilterModelMock(
    val priceMin: Float? = null,
    val priceMax: Float? = null,
    val rooms: Int? = null
)

data class ImmobileMock(
    val id: String,
    val titolo: String,
    val prezzo: String,
    val localita: String,
    val superficie: Int,
    val stanze: Int,
    val bagni: Int,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentListingScreen(
    navController: NavController,
    idUtente: String,
    comune: String,
    ricerca: String
) {
    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme

    // --- GESTIONE STATO LOCALE (Sostituisce ViewModel) ---
    var isLoading by remember { mutableStateOf(true) }
    var immobili by remember { mutableStateOf<List<ImmobileMock>>(emptyList()) }
    var activeFilters by remember { mutableStateOf<FilterModelMock?>(null) }

    // Bottom Sheet State
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showFilterSheet by remember { mutableStateOf(false) }

    // --- CARICAMENTO DATI SIMULATO ---
    LaunchedEffect(comune, ricerca, activeFilters) {
        isLoading = true
        delay(1500) // Simula chiamata API

        // Generazione dati mock
        val baseList = listOf(
            ImmobileMock("1", "Trilocale Vista Mare", "350.000", comune, 110, 3, 2, "https://picsum.photos/400/300"),
            ImmobileMock("2", "Villa Indipendente", "850.000", comune, 250, 6, 3, "https://picsum.photos/400/301"),
            ImmobileMock("3", "Monolocale Centro", "120.000", comune, 45, 1, 1, "https://picsum.photos/400/302"),
            ImmobileMock("4", "Attico Panoramico", "550.000", comune, 160, 4, 2, "https://picsum.photos/400/303"),
            ImmobileMock("5", "Bilocale Ristrutturato", "210.000", comune, 70, 2, 1, "https://picsum.photos/400/304")
        )

        // Simulazione filtraggio
        immobili = if (activeFilters != null) {
            // Se ci sono filtri, mostriamo un sottoinsieme casuale per simulare il risultato
            baseList.shuffled().take(3)
        } else {
            baseList
        }

        isLoading = false
    }

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
                CircularProgressIndicator(color = colorScheme.onPrimary)
            }
        } else if (immobili.isEmpty()) {
            EmptyStateView(comune, activeFilters) {
                activeFilters = null // Reset filtri
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
                            navController.navigate(Screen.PropertyScreen.withId(property.id))
                        }
                    )
                }
            }
        }
    }

    // --- BOTTOM SHEET FILTRI (MOCK CONTENT) ---
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
                    .padding(16.dp)
            ) {
                // Placeholder per i filtri (dato che SearchFilterScreen era una dipendenza esterna)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Filtri di Ricerca", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Text("Simulazione interfaccia filtri...")
                    Spacer(Modifier.height(32.dp))

                    // Bottone Applica Mock
                    Button(
                        onClick = {
                            // Attiva un filtro fittizio
                            activeFilters = FilterModelMock(priceMax = 500000f)
                            showFilterSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Applica Filtri (Mock)")
                    }

                    Spacer(Modifier.height(8.dp))

                    // Bottone Reset Mock
                    TextButton(
                        onClick = {
                            activeFilters = null
                            showFilterSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Resetta")
                    }
                }
            }
        }
    }
}

// --- COMPONENTI UI DI SUPPORTO ---

@Composable
fun ResultsHeader(count: Int, activeFilters: FilterModelMock?, comune: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
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
fun EmptyStateView(comune: String, activeFilters: FilterModelMock?, onReset: () -> Unit) {
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
fun RealPropertyCard(immobile: ImmobileMock, onClick: () -> Unit) {
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
            // Immagine
            Box(modifier = Modifier.weight(0.6f)) {
                AsyncImage(
                    model = immobile.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray), // Colore di fallback
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
                        text = immobile.localita,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val detailsList = mutableListOf<String>()
                    detailsList.add("${immobile.superficie}mq")
                    detailsList.add("${immobile.stanze} stanze")
                    detailsList.add("${immobile.bagni} bagni")

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