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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.theme.DietiEstates25Theme
import com.dieti.dietiestates25.ui.theme.TealLighter
import com.dieti.dietiestates25.ui.theme.TealPrimary

// Definizione dei colori
private val TealSelected = Color(0xFFB2DFDB)
private val SurfaceGray = Color(0xFFF5F5F5)
private val TextGray = Color(0xFF424242)
private val IconColor = Color(0xFF37474F)

// Dati di esempio per le proprietà immobiliari
data class Property(
    val id: Int,
    val price: String,
    val type: String,
    val address: String,
    val imageRes: Int
)

@Composable
fun HomeScreen(
    idUtente: String,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToPropertyDetails: (Int) -> Unit = {},
    onNavigateToSearch: (String) -> Unit = {},
    onNavigateToRecentSearches: () -> Unit = {},
    onNavigateToPostAd: () -> Unit = {}
) {
    // Stato per il campo di ricerca
    var searchQuery by remember { mutableStateOf("") }

    // Dati di esempio direttamente definiti qui
    val sampleProperties = remember {
        listOf(
            Property(
                id = 1,
                price = "500",
                type = "Monolocale",
                address = "Via Roma 123, Napoli",
                imageRes = R.drawable.property2 // Assicurati di avere queste risorse
            ),
            Property(
                id = 2,
                price = "700",
                type = "Bilocale",
                address = "Via Napoli 45, Napoli",
                imageRes = R.drawable.property1
            ),
            Property(
                id = 3,
                price = "900",
                type = "Trilocale",
                address = "Corso Umberto 78, Napoli",
                imageRes = R.drawable.property2
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header()

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        onNavigateToSearch(searchQuery)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Searches Section
            RecentSearchesSection(
                properties = sampleProperties,
                onPropertyClick = onNavigateToPropertyDetails,
                onSeeAllClick = onNavigateToRecentSearches
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Post Ad Section
            PostAdSection(onPostAdClick = onNavigateToPostAd)

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Navigation
            BottomNavigation(
                onHomeClick = { /* Già nella home */ },
                onNotificationsClick = onNavigateToNotifications,
                onProfileClick = onNavigateToProfile
            )
        }
    }
}

@Composable
private fun Header() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = TealPrimary,
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
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Campo di testo per la ricerca
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cerca casa...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = TealLighter
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bottone di ricerca
        Button(
            onClick = onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealLighter
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerca casa",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun RecentSearchesSection(
    properties: List<Property>,
    onPropertyClick: (Int) -> Unit,
    onSeeAllClick: () -> Unit
) {
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
                color = TextGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Button(
                onClick = onSeeAllClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealLighter
                ),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 18.dp)
            ) {
                Text(
                    text = "Vai ad ultime ricerche",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ScrollView orizzontale delle proprietà
        if (properties.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(properties) { property ->
                    PropertyCard(
                        property = property,
                        onPropertyClick = { onPropertyClick(property.id) },
                        modifier = Modifier.width(240.dp)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nessuna ricerca recente",
                    color = TextGray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun PropertyCard(
    property: Property,
    onPropertyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onPropertyClick)
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = property.imageRes),
            contentDescription = "Property Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Price and type overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color(0x80000000))
                .padding(8.dp)
        ) {
            Text(
                text = "€ ${property.price}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = property.type,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = property.address,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PostAdSection(onPostAdClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inserisci il tuo annuncio nell'app",
            color = TextGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onPostAdClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TealPrimary
            ),
            modifier = Modifier
                .width(200.dp)
                .height(40.dp)
        ) {
            Text(
                text = "Pubblica annuncio",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    onHomeClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = TealPrimary,
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
                onClick = onHomeClick
            )

            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = "Notifiche",
                selected = false,
                onClick = onNotificationsClick
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profilo",
                selected = false,
                onClick = onProfileClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
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
            tint = if (selected) TealSelected else Color.White,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = if (selected) TealSelected else Color.White,
            fontSize = 12.sp
        )
    }
}

// Funzione di Preview che rispetta il NavController
@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun HomeScreenPreview() {
    // Creazione di stub di navigazione per la preview
    val navigateToNotifications = {}
    val navigateToProfile = {}
    val navigateToPropertyDetails = { _: Int -> }
    val navigateToSearch = { _: String -> }
    val navigateToRecentSearches = {}
    val navigateToPostAd = {}

    // ID utente di esempio per la preview
    val testUserId = "user123"

    DietiEstates25Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(
                idUtente = testUserId,
                onNavigateToNotifications = navigateToNotifications,
                onNavigateToProfile = navigateToProfile,
                onNavigateToPropertyDetails = navigateToPropertyDetails,
                onNavigateToSearch = navigateToSearch,
                onNavigateToRecentSearches = navigateToRecentSearches,
                onNavigateToPostAd = navigateToPostAd
            )
        }
    }
}

// Preview aggiuntiva per il tema scuro
@Preview(showBackground = true, widthDp = 360, heightDp = 740, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenDarkPreview() {
    // Creazione di stub di navigazione per la preview
    val navigateToNotifications = {}
    val navigateToProfile = {}
    val navigateToPropertyDetails = { _: Int -> }
    val navigateToSearch = { _: String -> }
    val navigateToRecentSearches = {}
    val navigateToPostAd = {}

    // ID utente di esempio per la preview
    val testUserId = "user123"

    DietiEstates25Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(
                idUtente = testUserId,
                onNavigateToNotifications = navigateToNotifications,
                onNavigateToProfile = navigateToProfile,
                onNavigateToPropertyDetails = navigateToPropertyDetails,
                onNavigateToSearch = navigateToSearch,
                onNavigateToRecentSearches = navigateToRecentSearches,
                onNavigateToPostAd = navigateToPostAd
            )
        }
    }
}