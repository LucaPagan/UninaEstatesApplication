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
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.components.ClickableSearchBar
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppPropertyCard
import com.dieti.dietiestates25.ui.components.PropertyShowcaseSection
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

data class Property(
    val id: Int,
    val price: String,
    val type: String,
    val imageRes: Int,
    val location: String
)

val sampleProperties_Home = listOf(
    Property(1, "400.000 €", "Appartamento", R.drawable.property1, "Napoli"),
    Property(2, "320.000 €", "Villa", R.drawable.property2, "Roma"),
    Property(3, "250.000 €", "Attico", R.drawable.property1, "Milano"),
    Property(4, "180.000 €", "Bilocale", R.drawable.property2, "Torino")
)

@Composable
fun HomeScreen(navController: NavController, idUtente: String = "sconosciuto") {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val dimensions = Dimensions
        val comune = "Napoli"

        val gradientColors = listOf(
            colorScheme.primary.copy(alpha = 0.7f),
            colorScheme.background,
            colorScheme.background,
            colorScheme.primary.copy(alpha = 0.6f)
        )

        Scaffold(
            bottomBar = {
                AppBottomNavigation(navController = navController, idUtente = idUtente)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = gradientColors))
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    HomeScreenHeader(idUtente = idUtente)
                    Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                    ClickableSearchBar(
                        placeholderText = "Cerca comune, zona...",
                        onClick = { navController.navigate(Screen.SearchScreen.withArgs(idUtente)) }
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                    PropertyShowcaseSection(
                        title = "Ultime ricerche",
                        items = sampleProperties_Home, // Passa la lista dei dati Property
                        itemContent = { property -> // Per ogni 'property' nella lista...
                            AppPropertyCard(
                                // Mappa i dati della Property ai parametri della AppPropertyCard
                                price = property.price,
                                address = property.location,
                                details = listOf(property.type),
                                imageResId = property.imageRes,

                                // Configura AppPropertyCard per il layout verticale con dimensione fissa
                                horizontalMode = false, // Layout verticale
                                modifier = Modifier.size(width = 260.dp, height = 200.dp), // Dimensione fissa

                                // actionButton NON fornito, quindi l'onClick della card sarà attivo
                                actionButton = null,

                                onClick = {
                                    // QUESTA AZIONE viene eseguita quando si clicca sulla card
                                    navController.navigate("propertyDetail/${property.id}")
                                }
                            )
                        },
                        onSeeAllClick = {
                            navController.navigate(Screen.ApartmentListingScreen.withArgs(idUtente, comune))
                        },
                        modifier = Modifier.padding(vertical = Dimensions.paddingMedium)
                    )

                    Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                    HomeScreenPostAdSection(navController = navController, idUtente = idUtente)
                    Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
                }
            }
        }
    }
}

@Composable
fun HomeScreenHeader(idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.primary)
            .clip(RoundedCornerShape(bottomStart = dimensions.cornerRadiusLarge, bottomEnd = dimensions.cornerRadiusLarge))
            .padding(horizontal = dimensions.paddingLarge)
            .padding(top = 40.dp, bottom = dimensions.paddingLarge), // Mantenuto il paddingTop specifico per allinearsi col design
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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

@Composable
fun HomeScreenPostAdSection(navController: NavController, idUtente: String) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions

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
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        AppSecondaryButton(
            text = "Pubblica annuncio",
            onClick = {
                navController.navigate(Screen.PropertySellScreen.withArgs(idUtente))
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