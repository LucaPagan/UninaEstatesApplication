package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.ui.theme.TealPrimary
import com.dieti.dietiestates25.ui.theme.TealLightest
import com.dieti.dietiestates25.ui.theme.White


@Composable
fun ApartmentListingScreen() {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Header
            HeaderBar()

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cerca appartamenti") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cerca") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = Color.LightGray
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
                    features = "Dotato di ascensore"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ApartmentCard(
                    price = "€195.000",
                    address = "Appartamento Napoli, Soccavo, Via Montevergine 20",
                    rooms = "3 Locali",
                    area = "93 mq",
                    floor = "3 piano",
                    bathrooms = "1 bagno",
                    features = "Dotato di ascensore"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ApartmentCard(
                    price = "€240.000",
                    address = "Appartamento Napoli, Vomero, Via Torsanlo Tasso 185",
                    rooms = "3 Locali",
                    area = "86 mq",
                    floor = "1 piano",
                    bathrooms = "1 bagno",
                    features = "Dotato di posto auto"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ApartmentCard(
                    price = "€210.000",
                    address = "Appartamento Napoli, Posillipo, Via Petrarca 25",
                    rooms = "4 Locali",
                    area = "105 mq",
                    floor = "4 piano",
                    bathrooms = "2 bagni",
                    features = "Dotato di ascensore e terrazzo"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ApartmentCard(
                    price = "€175.000",
                    address = "Appartamento Napoli, Fuorigrotta, Via Leopardi 12",
                    rooms = "2 Locali",
                    area = "75 mq",
                    floor = "2 piano",
                    bathrooms = "1 bagno",
                    features = "Dotato di balcone"
                )

                // Bottom padding to ensure last item is visible above bottom bar
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Fixed Bottom Bar
            BottomBar()
        }
    }
}

@Composable
fun HeaderBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = Color(0xFF00897B),
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
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Indietro",
                    tint = White
                )
            }

            Text(
                text = "Napoli - Comune",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
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
                    tint = White
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
    features: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Apartment image (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(TealLightest)
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    // Visualizza button
                    Button(
                        onClick = { /* View details */ },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Visualizza",
                            color = White,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = address,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Property details
                Text(
                    text = "$rooms, $area, $floor, $bathrooms, $features",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BottomBar() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color(0xFF00897B)
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
                selected = true
            )

            AppBottomNavItem(
                icon = Icons.Default.FavoriteBorder,
                label = "Preferiti",
                selected = false
            )

            AppBottomNavItem(
                icon = Icons.Default.Person,
                label = "Profilo",
                selected = false
            )
        }
    }
}

@Composable
fun AppBottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) White else Color.LightGray,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = if (selected) White else Color.LightGray,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApartmentListingScreen() {
    ApartmentListingScreen()
}