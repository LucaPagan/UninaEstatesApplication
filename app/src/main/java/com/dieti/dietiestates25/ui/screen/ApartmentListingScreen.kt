package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
        val initialFiltersFromNav = remember(currentBackStackEntry) {
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
        var appliedFilters by remember { mutableStateOf(initialFiltersFromNav) }

        val gradientColors = arrayOf(
            0.0f to colorScheme.primary, 0.20f to colorScheme.background,
            0.60f to colorScheme.background, 1.0f to colorScheme.primary
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors))
                .systemBarsPadding()
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            ) {
                HeaderBar(
                    navController = navController, colorScheme = colorScheme, typography = typography,
                    dimensions = dimensions, onFilterClick = { showFilterSheet = true },
                    filtersApplied = appliedFilters != null, comune = comune
                )

                val filteredProperties = remember(appliedFilters, sampleListingProperties) {
                    val currentFilters = appliedFilters
                    if (currentFilters == null) {
                        sampleListingProperties
                    } else {
                        sampleListingProperties.filter { property ->
                            var match = true
                            currentFilters.purchaseType?.let { _ ->
                            }
                            val propertyPriceFloat = property.price.parsePriceToFloat()

                            currentFilters.minPrice?.let { minP ->
                                if (propertyPriceFloat == null || propertyPriceFloat < minP) match = false
                            }
                            currentFilters.maxPrice?.let { maxP ->
                                if (propertyPriceFloat == null || propertyPriceFloat > maxP) match = false
                            }
                            // TODO: Implementa la logica di filtro per:
                            // currentFilters.minSurface, currentFilters.maxSurface (richiede campo area in PropertyModel)
                            // currentFilters.minRooms, currentFilters.maxRooms (richiede campo stanze in PropertyModel)
                            // currentFilters.bathrooms (richiede campo bagni in PropertyModel)
                            // currentFilters.condition (richiede campo condizione in PropertyModel)
                            match
                        }
                    }
                }

                if (filteredProperties.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(dimensions.paddingLarge), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (appliedFilters != null) "Nessun immobile trovato con i filtri selezionati per \"$comune\"."
                            else "Nessun immobile disponibile per \"$comune\".",
                            style = typography.bodyLarge,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            start = dimensions.paddingMedium, end = dimensions.paddingMedium,
                            top = dimensions.paddingSmall, bottom = dimensions.paddingLarge + dimensions.buttonHeight
                        ),
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                    ) {
                        item {
                            Text(
                                text = "Annunci immobiliari a $comune", // Titolo più contestualizzato
                                style = typography.titleLarge,
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = dimensions.paddingSmall)
                            )

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
                                    // property.numeroLocali?.let { if (it == 1) "$it locale" else "$it locali" }
                                ).filter { it.isNotBlank() },
                                onClick = { },
                                actionButton = {
                                    AppPropertyViewButton(
                                        onClick = {
                                            navController.navigate(Screen.PropertyScreen.route)
                                        }
                                    )
                                },
                                horizontalMode = false, // Layout verticale
                                imageHeightVerticalRatio = 0.50f, // Immagine 50%, dettagli + bottone 50%
                                // Questo dà più spazio ai dettagli
                                elevationDp = Dimensions.elevationSmall
                            )
                        }
                    }
                }
            }

            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showFilterSheet = false },
                    sheetState = sheetState,
                    containerColor = colorScheme.background,
                    shape = RoundedCornerShape(topStart = dimensions.cornerRadiusLarge, topEnd = dimensions.cornerRadiusLarge),
                    scrimColor = Color.Black.copy(alpha = 0.32f),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = dimensions.buttonHeight * 10)
                            .fillMaxHeight(0.87f)
                    ) {
                        SearchFilterScreen(
                            navController = navController,
                            idUtente = idUtente,
                            comune = comune,
                            ricercaQueryText = ricerca,
                            onNavigateBack = { showFilterSheet = false },
                            onApplyFilters = { filterData ->
                                appliedFilters = filterData
                                showFilterSheet = false
                            },
                            isFullScreenContext = false,
                            originScreen = originScreen
                        )
                    }
                }
            }
        }
}

@Composable
fun HeaderBar(
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    onFilterClick: () -> Unit,
    filtersApplied: Boolean,
    comune: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.buttonHeight + dimensions.spacingSmall),
        color = colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = dimensions.cornerRadiusLarge, bottomEnd = dimensions.cornerRadiusLarge),
        shadowElevation = dimensions.elevationMedium
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensions.spacingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(dimensions.iconSizeLarge + dimensions.spacingSmall)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(dimensions.iconSizeMedium)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensions.spacingSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = comune.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    color = colorScheme.onPrimary,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box {
                IconButton(
                    onClick = onFilterClick,
                    modifier = Modifier.size(dimensions.iconSizeLarge + dimensions.spacingSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtra",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(dimensions.iconSizeMedium)
                    )
                }
                if (filtersApplied) {
                    Badge(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = -(dimensions.spacingSmall), y = dimensions.spacingExtraSmall),
                        containerColor = colorScheme.error
                    ) {}
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