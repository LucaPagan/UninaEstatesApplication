package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

@Composable
fun ApartmentListingScreen(navController: NavController, idUtente: String, comune: String) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        val scrollState = rememberScrollState()
        var searchQuery by remember { mutableStateOf("") }

        // Gradient background like WelcomeScreen
        val gradientColors = arrayOf(
            0.0f to colorScheme.primary,
            0.20f to colorScheme.background,
            0.70f to colorScheme.background,
            1.0f to colorScheme.primary
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Header
                HeaderBar(colorScheme = colorScheme, typography = typography)

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Cerca appartamenti", style = typography.bodyMedium) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Cerca",
                            tint = colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                )

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(top = 8.dp)
                        .padding(horizontal = 8.dp),
                ) {
                    // Apartment listings
                    ApartmentCard(
                        price = "€180.000",
                        address = "Appartamento Napoli, Via Gennaro 49",
                        rooms = "2 Locali",
                        area = "62 mq",
                        floor = "2 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di ascensore",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        price = "€195.000",
                        address = "Appartamento Napoli, Soccavo, Via Montevergine 20",
                        rooms = "3 Locali",
                        area = "93 mq",
                        floor = "3 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di ascensore",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        price = "€240.000",
                        address = "Appartamento Napoli, Vomero, Via Torsanlo Tasso 185",
                        rooms = "3 Locali",
                        area = "86 mq",
                        floor = "1 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di posto auto",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        price = "€210.000",
                        address = "Appartamento Napoli, Posillipo, Via Petrarca 25",
                        rooms = "4 Locali",
                        area = "105 mq",
                        floor = "4 piano",
                        bathrooms = "2 bagni",
                        features = "Dotato di ascensore e terrazzo",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ApartmentCard(
                        price = "€175.000",
                        address = "Appartamento Napoli, Fuorigrotta, Via Leopardi 12",
                        rooms = "2 Locali",
                        area = "75 mq",
                        floor = "2 piano",
                        bathrooms = "1 bagno",
                        features = "Dotato di balcone",
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    // Bottom padding to ensure last item is visible above bottom bar
                    Spacer(modifier = Modifier.height(80.dp))
                }

                // Fixed Bottom Bar
                BottomBar(colorScheme = colorScheme, typography = typography)
            }
        }
    }
}

@Composable
fun HeaderBar(colorScheme: ColorScheme, typography: Typography) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Handle back navigation */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onPrimary
                )
            }

            Text(
                text = "Napoli - Comune",
                color = colorScheme.onPrimary,
                style = typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            IconButton(
                onClick = { /* Handle edit/filter */ },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Modifica",
                    tint = colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ApartmentCard(
    price: String,
    address: String,
    rooms: String,
    area: String,
    floor: String,
    bathrooms: String,
    features: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Apartment image (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                // This would be an Image in a real app
            }

            // Apartment details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = price,
                        style = typography.titleMedium
                    )

                    // Visualizza button
                    Button(
                        onClick = { /* View details */ },
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.secondary,
                            contentColor = colorScheme.onSecondary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Visualizza",
                            style = typography.labelSmall
                        )
                    }
                }

                Text(
                    text = address,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Property details
                Text(
                    text = "$rooms, $area, $floor, $bathrooms, $features",
                    style = typography.bodySmall,
                    color = colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BottomBar(colorScheme: ColorScheme, typography: Typography) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = colorScheme.primary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppBottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                selected = true,
                colorScheme = colorScheme,
                typography = typography
            )

            AppBottomNavItem(
                icon = Icons.Default.FavoriteBorder,
                label = "Preferiti",
                selected = false,
                colorScheme = colorScheme,
                typography = typography
            )

            AppBottomNavItem(
                icon = Icons.Default.Person,
                label = "Profilo",
                selected = false,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
fun AppBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = if (selected) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f),
            style = typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApartmentListingScreen() {
    val navController = rememberNavController()
    ApartmentListingScreen(navController = navController, idUtente = "Danilo", comune = "Napoli")
}