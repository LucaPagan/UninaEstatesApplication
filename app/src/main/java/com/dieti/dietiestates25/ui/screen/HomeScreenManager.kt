package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.components.ClickableSearchBar
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppPropertyCard
import com.dieti.dietiestates25.ui.components.PropertyShowcaseSection
import com.dieti.dietiestates25.ui.model.modelsource.sampleListingProperties
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.theme.TealDeep

@Composable
fun HomeScreenManager(navController: NavController, idUtente: String = "manager") {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val comune = "Napoli"

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    Scaffold(
        topBar = {
            HomeScreenManagerHeader(
                idUtente = idUtente,
                dimensions = dimensions,
                typography = typography,
                colorScheme = colorScheme
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController, idUtente = idUtente)
        }
    ) { paddingValuesScaffold ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
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

                PropertyShowcaseSection(
                    title = "Ultime ricerche",
                    items = sampleListingProperties,
                    itemContent = { property ->
                        AppPropertyCard(
                            modifier = Modifier
                                .width(240.dp)
                                .height(210.dp),
                            price = property.price,
                            imageResId = property.imageRes,
                            address = property.location,
                            details = listOf(property.type),
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

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                // Sezione per la pubblicazione dell'annuncio E i pulsanti per il manager
                ManagerActionsSection(
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
fun HomeScreenManagerHeader(
    idUtente: String,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Status Bar con colore TealDeep fisso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(TealDeep)
        )

        // Header content con primary color e angoli arrotondati
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primary)
                .clip(
                    RoundedCornerShape(
                        bottomStart = dimensions.cornerRadiusLarge,
                        bottomEnd = dimensions.cornerRadiusLarge
                    )
                )
                .padding(horizontal = dimensions.paddingLarge)
                .padding(top = dimensions.paddingMedium, bottom = dimensions.paddingLarge),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconDisplay(
                    size = 60.dp,
                    shapeRadius = dimensions.cornerRadiusMedium
                )
                Spacer(modifier = Modifier.width(dimensions.spacingMedium))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bentornato",
                        color = colorScheme.onPrimary.copy(alpha = 0.8f),
                        style = typography.titleSmall
                    )
                    Text(
                        text = idUtente.ifEmpty { "Utente" },
                        color = colorScheme.onPrimary,
                        style = typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ManagerActionsSection(
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
        // Sezione per la pubblicazione dell'annuncio
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

        // --- Sezioni specifiche per il Manager ---
        Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

        Text(
            text = "Funzionalità Manager",
            color = colorScheme.onBackground,
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = dimensions.spacingMedium)
        )

        // Pulsante per monitorare le offerte
        AppSecondaryButton(
            text = "Monitora Offerte",
            onClick = {
                // Naviga alla schermata di monitoraggio delle offerte
                navController.navigate(Screen.OfferManagerScreen.withIdUtente(idUtente))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

        // Pulsante per gestire le segnalazioni
        AppSecondaryButton(
            text = "Gestisci Segnalazioni",
            onClick = {
                // Naviga alla schermata di gestione delle segnalazioni
                navController.navigate(Screen.ReportManagerScreen.withIdUtente(idUtente))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun PreviewHomeScreenManager() {
    DietiEstatesTheme {
        val navController = rememberNavController()
        HomeScreenManager(navController = navController, idUtente = "Boss")
    }
}
