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
import kotlinx.coroutines.delay

// --- CLASSE DATI MOCK (Sostituisce il DTO del Backend) ---
data class ImmobileMock(
    val id: String,
    val prezzo: Int,
    val localita: String?,
    val tipologia: String,
    val mq: Int
)

@Composable
fun HomeScreen(
    navController: NavController,
    idUtente: String = "sconosciuto"
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val comune = "Napoli"

    // --- GESTIONE STATO LOCALE (Sostituisce il ViewModel) ---
    var immobili by remember { mutableStateOf<List<ImmobileMock>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) } // Parte true per simulare il caricamento iniziale

    // --- CARICAMENTO DATI SIMULATO ---
    LaunchedEffect(Unit) {
        // Simula ritardo di rete
        delay(2000)

        // Dati finti
        immobili = listOf(
            ImmobileMock("1", 350000, "Napoli, Vomero", "Appartamento", 110),
            ImmobileMock("2", 1200000, "Napoli, Posillipo", "Villa", 250),
            ImmobileMock("3", 180000, "Napoli, Centro Storico", "Monolocale", 50),
            ImmobileMock("4", 450000, "Napoli, Chiaia", "Attico", 130)
        )

        isLoading = false
    }
    // -------------------------------

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
                        items = immobili,
                        itemContent = { property ->
                            // Mappatura oggetto locale -> UI Component
                            AppPropertyCard(
                                modifier = Modifier
                                    .width(dimensions.propertyCardHeight)
                                    .height(dimensions.circularIconSize), // Verifica layout originale
                                price = "€ ${property.prezzo}",
                                // Placeholder statico
                                imageResId = R.drawable.property1,
                                address = property.localita ?: "N/A",
                                details = listOfNotNull(property.tipologia, "${property.mq} mq"),
                                onClick = {
                                    // Navigazione finta verso dettaglio
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

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                // Pulsante Manager
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