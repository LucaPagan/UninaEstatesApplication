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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

// Dati di esempio per la propertyList (mantenuti)
data class Property(
    val id: Int,
    val price: String,
    val type: String,
    val imageRes: Int,
    val location: String // Added location for better card info
)

val sampleProperties = listOf(
    Property(1, "400.000 €", "Appartamento", R.drawable.property1, "Napoli"),
    Property(2, "320.000 €", "Villa", R.drawable.property2, "Roma"),
    Property(3, "250.000 €", "Attico", R.drawable.property1, "Milano"),
    Property(4, "180.000 €", "Bilocale", R.drawable.property2, "Torino")
)

@Composable
fun HomeScreen(navController: NavController, idUtente: String = "sconosciuto") {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        // Refined gradient colors for a softer look
        val gradientColors = listOf(
            colorScheme.primary.copy(alpha = 0.7f), // Primary with more transparency at top
            colorScheme.background,              // Background in the middle
            colorScheme.background,              // Stay background
            colorScheme.primary.copy(alpha = 0.6f) // Primary with more transparency at bottom
        )


        Scaffold( // Use Scaffold for standard structure
            bottomBar = {
                AppBottomNavigation(navController = navController, idUtente = idUtente)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = gradientColors))
                    .padding(paddingValues) // Apply padding from Scaffold
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // Make content scrollable
                ) {
                    Header(idUtente)

                    Spacer(modifier = Modifier.height(32.dp)) // Adjusted spacing

                    // Search Bar Button (Clickable Box)
                    SearchBar(navController, idUtente)

                    Spacer(modifier = Modifier.height(32.dp)) // Adjusted spacing

                    // Recent Searches Section
                    RecentSearchesSection(navController, idUtente, sampleProperties)

                    // Removed the heavy Divider with shadow, rely on spacing instead

                    Spacer(modifier = Modifier.height(32.dp)) // Spacing before next section

                    // Post Ad Section
                    PostAdSection(navController, idUtente)

                    // Removed the weight(1f) Spacer at the end, let content define height with scroll
                }
            }
        }
    }
}

@Composable
fun Header(idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Header with softer bottom shape and better aligned content
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.primary) // Solid primary background for simplicity
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)) // Softer rounding
            .padding(horizontal = 24.dp) // Horizontal padding for content
            .padding(top = 40.dp, bottom = 24.dp), // Vertical padding
        contentAlignment = Alignment.BottomStart // Align content to bottom start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Space out icon and text
        ) {
            // App Icon within a clean, smaller container
            Surface(
                modifier = Modifier
                    .size(60.dp), // Smaller size for header icon
                shape = RoundedCornerShape(16.dp), // Slightly rounded corners
                color = colorScheme.surface, // Surface color as background
                shadowElevation = 4.dp // Subtle shadow
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp) // Padding inside the surface
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.appicon1), // Using appicon1
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)), // Clip image with slight rounding
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) // Spacing between icon and text

            Column(
                modifier = Modifier.weight(1f) // Allow text column to take available space
            ) {
                Text(
                    text = "Bentornato",
                    color = colorScheme.onPrimary.copy(alpha = 0.8f), // Slightly less opaque
                    style = typography.titleSmall // Adjusted typography
                )
                Text(
                    text = idUtente.ifEmpty { "Utente" }, // Display "Utente" if idUtente is empty
                    color = colorScheme.onPrimary,
                    style = typography.titleLarge, // More prominent user name
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun SearchBar(navController: NavController, idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Visually styled search bar (clickable Box)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(28.dp)) // Highly rounded corners
            .background(colorScheme.surface) // Use surface color for the bar
            .clickable {
                // Navigate to SearchScreen on click
                navController.navigate(Screen.SearchScreen.withArgs(idUtente))
            }
            .padding(horizontal = 16.dp, vertical = 12.dp) // Padding inside the bar
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between icon and text
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cerca",
                tint = colorScheme.onSurface.copy(alpha = 0.7f) // Softer icon tint
            )
            Text(
                text = "Cerca comune, zona...", // Placeholder text
                color = colorScheme.onSurface.copy(alpha = 0.7f), // Softer text color
                style = typography.bodyLarge // Adjusted typography
            )
        }
    }
}


@Composable
fun RecentSearchesSection(navController: NavController, idUtente: String, properties: List<Property>) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp) // Consistent horizontal padding
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ultime ricerche",
                color = colorScheme.onBackground,
                style = typography.titleMedium, // Use titleMedium for section title
                fontWeight = FontWeight.SemiBold
            )

            TextButton(
                onClick = {
                    // TODO: Navigate to recent searches screen
                    // navController.navigate(Screen.RecentSearchesScreen.route)
                },
                // Removed explicit height and shape, using TextButton defaults
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colorScheme.primary // Primary color for text button
                )
            ) {
                Text(
                    text = "Vedi tutte", // More concise text
                    style = typography.labelLarge // Using labelLarge
                )
            }
        }

        // Removed the heavy Divider here

        Spacer(modifier = Modifier.height(12.dp)) // Spacing between title/button and list

        // ScrollView orizzontale delle proprietà
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp), // Horizontal padding for the LazyRow content
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Spacing between cards
        ) {
            items(properties) { property ->
                PropertyCard(
                    property = property,
                    navController = navController,
                    modifier = Modifier.width(260.dp) // Slightly wider cards
                )
            }
        }
    }
}

@Composable
fun PropertyCard(
    property: Property,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    Card( // Use Material 3 Card
        modifier = modifier
            .height(200.dp) // Increased card height
            .clickable {
                navController.navigate(Screen.PropertyScreen.route)
            },
        shape = RoundedCornerShape(12.dp), // Rounded corners for the card
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface // Use surface color for card background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp // Subtle elevation
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = property.imageRes),
                contentDescription = "Property Image",
                contentScale = ContentScale.Crop, // Crop to fill the card
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp) // Fixed height for the image
            )

            // Overlay sfumato nella parte inferiore dell'immagine
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(60.dp) // Height of the gradient overlay
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f) // Gradient to a darker, more opaque color for better text contrast
                            )
                        )
                    )
            )

            // Prezzo, tipo e location posizionati sopra l'overlay scuro
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = property.price,
                    color = Color.White, // White text for better contrast on dark overlay
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = property.type,
                    color = Color.White.copy(alpha = 0.9f), // Slightly less opaque white
                    style = typography.bodyMedium, // Adjusted typography
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (property.location.isNotEmpty()) {
                    Text(
                        text = property.location,
                        color = Color.White.copy(alpha = 0.7f), // Even less opaque white for location
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
fun PostAdSection(navController: NavController, idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp) // Consistent horizontal padding
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vuoi vendere o affittare il tuo immobile?",
            color = colorScheme.onBackground,
            style = typography.titleMedium, // Use titleMedium for section title
            fontWeight = FontWeight.SemiBold,
            textAlign =  TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp)) // Adjusted spacing

        Text(
            text = "Inserisci il tuo annuncio in pochi semplici passaggi.",
            color = colorScheme.onBackground.copy(alpha = 0.8f),
            style = typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp)) // Adjusted spacing

        Button(
            onClick = {
                navController.navigate(Screen.PropertySellScreen.withArgs(idUtente))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp), // More standard rounded corners
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.secondary, // Use secondary color for a different call to action
                contentColor = colorScheme.onSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp, // Subtle elevation
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "Pubblica annuncio",
                color = colorScheme.onSecondary,
                style = typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    // Per la preview, creiamo un NavController fittizio
    val navController = rememberNavController()
    HomeScreen(navController = navController, idUtente = "Danilo") // Example user name
}