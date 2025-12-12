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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.components.ClickableSearchBar
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppPropertyCard
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.components.PropertyShowcaseSection
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun HomeScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: HomeViewModel = viewModel()
) {
    // Stato per la lista degli immobili dal DB
    val immobili by viewModel.immobili.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val comune = "Napoli"


    // Caricamento Dati all'avvio
    LaunchedEffect(Unit) {
        if (immobili.isEmpty()) {
            viewModel.fetchImmobili()
        }
    }
    // -------------------------------

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Bentornato", // Puoi mettere il nome utente se lo passi
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

                // Se sta caricando mostriamo una rotella, altrimenti la lista
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                } else {
                    PropertyShowcaseSection(
                        title = "Immobili in evidenza",
                        items = immobili, // Passiamo la lista dal Backend
                        itemContent = { property ->
                            // Adattiamo ImmobileDTO (Backend) ai parametri di AppPropertyCard (Frontend)
                            AppPropertyCard(
                                modifier = Modifier
                                    .width(dimensions.propertyCardHeight)
                                    .height(dimensions.circularIconSize), // Verifica se questa altezza è corretta per la card
                                price = "€ ${property.prezzo}", // Conversione Int -> String
                                // TODO: Modifica AppPropertyCard per accettare imageUrl (String) e usa AsyncImage + Coil
                                // Per ora usiamo un placeholder statico per evitare errori di compilazione
                                imageResId = R.drawable.property1,
                                address = property.localita ?: "N/A",
                                details = listOfNotNull(property.tipologia, "${property.mq} mq"),
                                onClick = {
                                    // Passiamo l'ID reale dell'immobile
                                    // Assicurati che Screen.PropertyScreen accetti un argomento ID, es: "property_screen/{id}"
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

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                HomeScreenPostAdSection(
                    navController = navController,
                    idUtente = idUtente,
                    dimensions = dimensions,
                    typography = typography,
                    colorScheme = colorScheme
                )

                // Rimuovere dato che è il pulsante per vedere le funzionalità manager
                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
                TextButton(
                    onClick = { navController.navigate(Screen.ManagerScreen.withIdUtente(idUtente)) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = dimensions.paddingLarge)
                ) {
                    Text(
                        text = "Vai alla Home del Manager",
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
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

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun PreviewHomeScreen() {
    DietiEstatesTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController, idUtente = "Danilo")
    }
}