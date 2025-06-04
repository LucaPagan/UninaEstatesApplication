package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.ApartmentListingScreen_MapSearchScreen_HeaderBar
import com.dieti.dietiestates25.ui.components.AppPropertyCard
import com.dieti.dietiestates25.ui.components.AppPropertyViewButton
import com.dieti.dietiestates25.ui.model.FilterModel
import com.dieti.dietiestates25.ui.model.FilterOriginScreen
import com.dieti.dietiestates25.ui.model.modelsource.sampleListingProperties
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.parsePriceToFloat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApartmentListingScreen(
    navController: NavController,
    idUtente: String,
    comune: String,
    ricerca: String,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val originScreen = FilterOriginScreen.APARTMENT_LISTING

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showFilterSheet by remember { mutableStateOf(false) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    // Gestione dei filtri con stato reattivo (come in MapSearchScreen)
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

    val gradientColors = arrayOf(
        0.0f to colorScheme.primary, 0.20f to colorScheme.background,
        0.60f to colorScheme.background, 1.0f to colorScheme.primary
    )

    // Filtra le proprietà in base ai filtri applicati (logica completa come in MapSearchScreen)
    val filteredProperties = remember(appliedFilters, sampleListingProperties, comune, ricerca) {
        sampleListingProperties.filter { property ->
            appliedFilters?.let { filters ->
                // Filtra per tipo di acquisto
                if(filters.purchaseType != null) {
                    property.purchaseType?.let { if (it != filters.purchaseType) return@filter false }
                }

                // Filtra per prezzo
                val propertyPriceFloat = property.price.parsePriceToFloat()
                if (filters.minPrice != null && (propertyPriceFloat == null || propertyPriceFloat < filters.minPrice)) {
                    return@filter false
                }
                if (filters.maxPrice != null && (propertyPriceFloat == null || propertyPriceFloat > filters.maxPrice)) {
                    return@filter false
                }

                // Filtra per superficie
                if (filters.minSurface != null) {
                    val propertySurface = property.areaMq?.toFloat()
                    if (propertySurface == null || propertySurface < filters.minSurface) {
                        return@filter false
                    }
                }
                if (filters.maxSurface != null) {
                    val propertySurface = property.areaMq?.toFloat()
                    if (propertySurface == null || propertySurface > filters.maxSurface) {
                        return@filter false
                    }
                }

                // Filtra per stanze (assumendo che PropertyModel abbia un campo rooms o bedrooms)
                if (filters.minRooms != null) {
                    property.rooms?.let { if (it < filters.minRooms) return@filter false }
                }
                if (filters.maxRooms != null) {
                    property.rooms?.let { if (it > filters.maxRooms) return@filter false }
                }

                // Filtra per bagni
                if (filters.bathrooms != null) {
                    property.bathrooms?.let {
                        if (it < filters.bathrooms) return@filter false
                    }
                }

                // Filtra per condizione (assumendo che PropertyModel abbia un campo condition)
                if (filters.condition != null) {
                    // property.condition?.let { if (it != filters.condition) return@filter false }
                    // Commentato perché potrebbe non essere disponibile nel modello corrente
                }

                true
            } ?: true // Se non ci sono filtri, mostra tutto
        }
    }

    // Layout principale senza systemBarsPadding() in quanto gestito dalla CustomHeaderBar
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colorStops = gradientColors))
    ) {
        // Header personalizzato
        ApartmentListingScreen_MapSearchScreen_HeaderBar(
            navController = navController,
            comune = comune,
            onFilterClick = { showFilterSheet = true },
            filtersApplied = appliedFilters != null
        )

        if (filteredProperties.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(dimensions.paddingLarge),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                ) {
                    Text(
                        text = if (appliedFilters != null)
                            "Nessun immobile trovato con i filtri selezionati per \"$comune\"."
                        else
                            "Nessun immobile disponibile per \"$comune\".",
                        style = typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Pulsante per resettare i filtri (se applicati)
                    if (appliedFilters != null) {
                        OutlinedButton(
                            onClick = {
                                appliedFilters = null
                                println("Filtri resettati")
                            }
                        ) {
                            Text("Reset Filtri")
                        }
                    }

                    // Indicatore del numero di risultati trovati
                    if (appliedFilters != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "${filteredProperties.size} proprietà trovate",
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
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    start = dimensions.paddingMedium,
                    end = dimensions.paddingMedium,
                    top = dimensions.paddingSmall,
                    bottom = dimensions.paddingLarge + dimensions.buttonHeight
                ),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Annunci immobiliari a $comune",
                            style = typography.titleLarge,
                            color = colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        // Indicatore del numero di risultati filtrati (come in MapSearchScreen)
                        if (appliedFilters != null) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.primaryContainer
                                )
                            ) {
                                Text(
                                    text = "${filteredProperties.size} trovate",
                                    style = typography.bodySmall,
                                    color = colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(
                                        horizontal = dimensions.spacingSmall,
                                        vertical = dimensions.spacingExtraSmall
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                }

                items(items = filteredProperties, key = { it.id }) { property ->
                    AppPropertyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        price = property.price,
                        imageResId = property.imageRes,
                        address = property.location,
                        details = listOfNotNull(
                            property.type,
                            property.areaMq?.let { "$it mq" },
                            property.bathrooms?.let { numBagni ->
                                if (numBagni == 1) "$numBagni bagno" else "$numBagni bagni"
                            }
                        ).filter { it.isNotBlank() },
                        onClick = { },
                        actionButton = {
                            AppPropertyViewButton(
                                onClick = {
                                    navController.navigate(Screen.PropertyScreen.route)
                                }
                            )
                        },
                        horizontalMode = false,
                        imageHeightVerticalRatio = 0.50f,
                        elevationDp = Dimensions.elevationSmall
                    )
                }
            }
        }
    }

    // ModalBottomSheet per i Filtri (identica a MapSearchScreen)
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
                        showFilterSheet = false
                        println("ApartmentListingScreen - Filtri applicati: $filterData")
                    },
                    isFullScreenContext = false,
                    originScreen = originScreen
                )
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