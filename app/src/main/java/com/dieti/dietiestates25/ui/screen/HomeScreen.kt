package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

// Dati di esempio per la propertyList
data class Property(
    val id: Int,
    val price: String,
    val type: String,
    val imageRes: Int
)

val sampleProperties = listOf(
    Property(1, "400.000", "Appartamento...", R.drawable.property1),
    Property(2, "320.000", "Villa...", R.drawable.property2),
    Property(3, "250.000", "Attico...", R.drawable.property1),
    Property(4, "180.000", "Bilocale...", R.drawable.property2)
)

@Composable
fun HomeScreen(navController: NavController, idUtente: String = "sconosciuto") {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        val gradientColors = listOf(
            colorScheme.primary.copy(alpha = 0.85f),
            colorScheme.background,
            colorScheme.background,
            colorScheme.primary.copy(alpha = 0.75f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Header(idUtente)

                Spacer(modifier = Modifier.height(40.dp))

                // Search Bar Button
                SearchBar(navController, idUtente)

                Spacer(modifier = Modifier.height(30.dp))

                // Recent Searches Section
                RecentSearchesSection(navController, idUtente)

                Divider(
                    modifier = Modifier
                        .shadow(
                            elevation = 2.dp,
                            shape = RectangleShape,
                            spotColor = Color.Black.copy(alpha = 0.2f)
                        )
                        .height(4.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Post Ad Section
                PostAdSection(navController, idUtente)

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Navigation
                BottomNavigation(navController, idUtente)



            }
        }
    }
}

@Composable
fun Header(idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        color = colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier
                    .size(70.dp), // Container size remains the same
                shape = RoundedCornerShape(24.dp), // Rounded rectangular shape
                color = colorScheme.surface, // Surface color as background
                shadowElevation = 8.dp // Subtle shadow
            ) {
                Box(
                    modifier = Modifier
                        .padding(4.dp) // Padding minimo per icona grande
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.appicon1),
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Fit // Adatta l'icona mantenendo l'aspect ratio
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Bentornato $idUtente",
                color = colorScheme.onPrimary,
                style = typography.bodyMedium
            )

            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            if (idUtente.isNotEmpty()) {
                Text(
                    text = "Benvenuto $idUtente",
                    color = colorScheme.onPrimary,
                    style = typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SearchBar(navController: NavController, idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Button(
            onClick = {
                navController.navigate(Screen.SearchScreen.withArgs(idUtente))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerca casa",
                    color = colorScheme.onPrimary,
                    style = typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun RecentSearchesSection(navController: NavController, idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ultime ricerche",
                color = colorScheme.onBackground,
                style = typography.bodyLarge
            )

            Button(
                onClick = {
                    // TODO: Navigate to recent searches screen
                    // navController.navigate(Screen.RecentSearchesScreen.route)
                },
                modifier = Modifier
                    .height(40.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = "Vai ad ultime ricerche",
                    color = colorScheme.onPrimary,
                    style = typography.labelMedium
                )
            }
        }

        Divider(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = RectangleShape,
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .height(4.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // ScrollView orizzontale delle proprietà
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sampleProperties) { property ->
                PropertyCard(
                    property = property,
                    navController = navController,
                    modifier = Modifier.width(240.dp)
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

    Box(
        modifier = modifier
            .height(160.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                navController.navigate(Screen.PropertyScreen.route)
            }
    ) {
        Image(
            painter = painterResource(id = property.imageRes),
            contentDescription = "Property Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        // Overlay sfumato bianco nella parte inferiore
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorScheme.secondary.copy(alpha = 0.6f)
                        ),
                        startY = 0f,
                        endY = 80f
                    )
                )
                .height(60.dp)
        )

        // Prezzo e tipo posizionati sopra l'overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            if (property.price.isNotEmpty()) {
                Text(
                    text = property.price,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (property.type.isNotEmpty()) {
                Text(
                    text = property.type,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    style = typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inserisci il tuo annuncio nell'app",
            color = colorScheme.onBackground,
            style = typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(Screen.PropertySellScreen.withArgs(idUtente))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Text(
                text = "Pubblica annuncio",
                color = colorScheme.onPrimary,
                style = typography.labelLarge
            )
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController, idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        color = colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Esplora",
                selected = true,
                onClick = {
                    // Già nella schermata home
                }
            )

            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = "Notifiche",
                selected = false,
                onClick = {
                    // TODO: Navigate to notifications screen
                    // navController.navigate(Screen.NotificationsScreen.route)
                }
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profilo",
                selected = false,
                onClick = {
                    // TODO: Navigate to profile screen
                    // navController.navigate(Screen.ProfileScreen.route)
                }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) colorScheme.onPrimaryContainer else colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = if (selected) colorScheme.onPrimaryContainer else colorScheme.onPrimary,
            style = typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    // Per la preview, creiamo un NavController fittizio
    val navController = rememberNavController()
    HomeScreen(navController = navController, idUtente = "")
}