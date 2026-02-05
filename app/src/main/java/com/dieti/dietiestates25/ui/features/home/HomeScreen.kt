package com.dieti.dietiestates25.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.components.*
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun HomeScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: HomeViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val comune = "Napoli"

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Bentornato",
                showAppIcon = true,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController, idUtente = idUtente)
        }
    ) { paddingValuesScaffold ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .padding(paddingValuesScaffold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                ClickableSearchBar(
                    placeholderText = "Cerca comune, zona...",
                    onClick = { navController.navigate(Screen.SearchScreen.withIdUtente(idUtente)) },
                    modifier = Modifier.padding(horizontal = dimensions.paddingMedium)
                )

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = colorScheme.primary)
                        }
                    }

                    is HomeUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensions.paddingLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Qualcosa è andato storto.",
                                    color = colorScheme.error,
                                    style = typography.titleSmall
                                )
                                Text(
                                    text = state.message,
                                    color = colorScheme.onSurfaceVariant,
                                    style = typography.bodySmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.fetchImmobili() }) {
                                    Text("Riprova")
                                }
                            }
                        }
                    }

                    is HomeUiState.Success -> {
                        PropertyShowcaseSection(
                            title = "Immobili in evidenza",
                            items = state.immobili,
                            itemContent = { property ->

                                // Construct Image URL using the helper
                                // Requires property.immagini to be defined in FrontendModels.kt
                                val imageUrl = if (property.immagini.isNotEmpty()) {
                                    RetrofitClient.getImageUrl(property.immagini[0].id) + "/raw"
                                } else null

                                AppPropertyCard(
                                    modifier = Modifier
                                        .width(dimensions.propertyCardHeight)
                                        .height(dimensions.circularIconSize),
                                    price = "€ ${property.prezzo ?: "Tratt."}",
                                    imageUrl = imageUrl,
                                    // Use 'indirizzo' and 'categoria' as defined in FrontendModels.kt
                                    address = property.indirizzo ?: "Zona non specificata",
                                    details = listOfNotNull(
                                        property.categoria,
                                        property.mq?.let { "$it mq" }
                                    ),
                                    onClick = {
                                        navController.navigate(Screen.PropertyScreen.route)
                                    },
                                    actionButton = null,
                                    horizontalMode = false,
                                    imageHeightVerticalRatio = 0.55f,
                                    elevationDp = Dimensions.elevationSmall
                                )
                            },
                            onSeeAllClick = {
                                navController.navigate(
                                    Screen.ApartmentListingScreen.buildRoute(
                                        idUtentePath = idUtente,
                                        comunePath = comune,
                                        ricercaPath = ""
                                    )
                                )
                            },
                            listContentPadding = PaddingValues(horizontal = dimensions.paddingLarge)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                HomeScreenPostAdSection(
                    navController = navController,
                    idUtente = idUtente,
                    dimensions = dimensions,
                    typography = typography,
                    colorScheme = colorScheme
                )

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
            }
        }
    }
}

@Composable
fun HomeScreenPostAdSection(
    navController: NavController,
    idUtente: String,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingLarge)
            .padding(vertical = dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vuoi vendere o affittare il tuo immobile?",
            color = colorScheme.onBackground,
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        Text(
            text = "Inserisci il tuo annuncio in pochi semplici passaggi.",
            color = colorScheme.onBackground.copy(alpha = 0.8f),
            style = typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensions.paddingSmall)
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        AppSecondaryButton(
            text = "Pubblica annuncio",
            onClick = {
                navController.navigate(Screen.PropertySellScreen.withIdUtente(idUtente))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}