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
import com.dieti.dietiestates25.ui.components.AppTopBarProfileNotification // Import aggiunto
import androidx.compose.material.icons.Icons // Import aggiunto
import androidx.compose.material.icons.filled.Shield // Import aggiunto

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
            // Sostituzione di HomeScreenManagerHeader con AppTopBarProfileNotification
            AppTopBarProfileNotification(
                title = "Bentornato $idUtente",
                actionIcon = Icons.Filled.Shield,
                actionContentDescription = "Mostra funzionalità manager",
                onActionClick = {
                    navController.navigate(Screen.ManagerScreen.withIdUtente(idUtente))
                },
                actionBackgroundColor = colorScheme.primaryContainer,
                actionIconTint = colorScheme.onPrimaryContainer,
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

                // La sezione per la pubblicazione dell'annuncio viene mantenuta,
                // ma le funzionalità manager specifiche vengono rimosse da qui
                // per essere spostate nella schermata dedicata.
                PublishAdSection(
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
fun PublishAdSection(
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
fun PreviewHomeScreenManager() {
    DietiEstatesTheme {
        val navController = rememberNavController()
        HomeScreenManager(navController = navController, idUtente = "Boss")
    }
}