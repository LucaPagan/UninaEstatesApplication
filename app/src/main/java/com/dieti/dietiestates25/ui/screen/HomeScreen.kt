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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
fun HomeScreen(navController: NavController, idUtente: String) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background),
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Header(idUtente)

                Spacer(modifier = Modifier.height(80.dp))

                // Search Bar Button
                SearchBar(navController, idUtente)

                Spacer(modifier = Modifier.height(80.dp))

                // Recent Searches Section
                RecentSearchesSection(navController, idUtente)

                Spacer(modifier = Modifier.height(48.dp))

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
            .height(120.dp),
        color = colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "UNINAESTATES25",
                color = colorScheme.onPrimary,
                style = typography.titleLarge
            )

            if (idUtente.isNotEmpty()) {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Benvenuto $idUtente",
                        color = colorScheme.onPrimary,
                        style = typography.bodyMedium
                    )
                }
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
                .padding(horizontal = 64.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.secondary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerca casa",
                    color = colorScheme.onSecondary,
                    style = typography.bodyMedium
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
            .padding(horizontal = 16.dp)
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
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondary
                ),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 18.dp)
            ) {
                Text(
                    text = "Vai ad ultime ricerche",
                    color = colorScheme.onSecondary,
                    style = typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

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

    Box(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                // TODO: Navigate to property details screen
                // navController.navigate(Screen.PropertyDetailsScreen.withArgs(property.id.toString()))
            }
    ) {
        Image(
            painter = painterResource(id = property.imageRes),
            contentDescription = "Property Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Price and type overlay
        if (property.price.isNotEmpty() || property.type.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                if (property.price.isNotEmpty()) {
                    Text(
                        text = property.price,
                        color = MaterialTheme.colorScheme.surface,
                        style = typography.titleMedium
                    )
                }

                if (property.type.isNotEmpty()) {
                    Text(
                        text = property.type,
                        color = MaterialTheme.colorScheme.surface,
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
                // TODO: Navigate to post ad screen
                // navController.navigate(Screen.PostAdScreen.withArgs(idUtente))
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            modifier = Modifier
                .width(200.dp)
                .height(40.dp)
        ) {
            Text(
                text = "Pubblica annuncio",
                color = colorScheme.onPrimary,
                style = typography.labelMedium
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