package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.AppIconDisplay
import com.dieti.dietiestates25.ui.AppSecondaryButton
import com.dieti.dietiestates25.ui.ClickableSearchBar
import com.dieti.dietiestates25.ui.TitledSection
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

// Sample Data for HomeScreen (remains the same)
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
                    .navigationBarsPadding()
                    .statusBarsPadding()
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

                    HomeScreenRecentSearchesSection(
                        navController = navController,
                        idUtente = idUtente,
                        properties = sampleProperties_Home
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
                size = 60.dp, // Mantenuto valore specifico perché è una dimensione dell'icona particolare
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
fun HomeScreenRecentSearchesSection(
    navController: NavController,
    idUtente: String,
    properties: List<Property>
) {
    val dimensions = Dimensions

    TitledSection(
        title = "Ultime ricerche",
        modifier = Modifier.padding(vertical = dimensions.paddingMedium),
        onSeeAllClick = {
            navController.navigate(Screen.ApartmentListingScreen.withArgs(idUtente))
        }
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            items(properties) { property ->
                PropertyCard_Home(
                    property = property,
                    navController = navController,
                    modifier = Modifier.width(260.dp) // Mantenuto per preservare l'aspetto desiderato
                )
            }
        }
    }
}

@Composable
fun PropertyCard_Home(
    property: Property,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions

    Card(
        modifier = modifier
            .height(200.dp) // Mantenuto per preservare l'aspetto desiderato
            .clickable {
                navController.navigate(Screen.PropertyScreen.route)
            },
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationMedium)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = property.imageRes),
                contentDescription = "Property Image: ${property.type}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp) // Mantenuto per preservare l'aspetto desiderato
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp) // Mantenuto per preservare l'aspetto desiderato
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(dimensions.paddingSmall)
            ) {
                Text(
                    text = property.price,
                    color = Color.White,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = property.type,
                    color = Color.White.copy(alpha = 0.9f),
                    style = typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (property.location.isNotEmpty()) {
                    Text(
                        text = property.location,
                        color = Color.White.copy(alpha = 0.7f),
                        style = typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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