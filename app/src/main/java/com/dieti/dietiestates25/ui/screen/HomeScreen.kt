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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.data.PreferenceManager
import com.dieti.dietiestates25.ui.theme.TealLighter
import com.dieti.dietiestates25.ui.theme.TealPrimary
import com.dieti.dietiestates25.viewmodel.SharedViewModel

val TealSelected = Color(0xFFB2DFDB)
val SurfaceGray = Color(0xFFF5F5F5)
val TextGray = Color(0xFF424242)
val IconColor = Color(0xFF37474F)

// Dati di esempio per la propertyList
data class Property(
    val id: Int,
    val price: String,
    val type: String,
    val imageRes: Int
)

@Composable
fun HomeScreen(
    userToken: String,
    viewModel: SharedViewModel,
    onSearchClick: (String) -> Unit,
    onPropertyClick: (Int) -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPostAdClick: () -> Unit,
    onRecentSearchesClick: () -> Unit
) {
    // Stato per il campo di ricerca
    var searchQuery by remember { mutableStateOf("") }

    // Osserva le proprietà dal viewModel
    val properties = viewModel.properties

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceGray),
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Header()

            Spacer(modifier = Modifier.height(80.dp))

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.addSearch(searchQuery)
                        onSearchClick(searchQuery)
                    }
                }
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Recent Searches Section
            RecentSearchesSection(
                properties = properties,
                onPropertyClick = onPropertyClick,
                onViewAllClick = onRecentSearchesClick
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Post Ad Section
            PostAdSection(onPostAdClick)

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Navigation
            BottomNavigation(
                onHomeClick = { /* Già nella home */ },
                onNotificationsClick = onNotificationsClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
fun Header() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
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
fun SearchBar(
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
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
                .padding(horizontal = 64.dp)
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
fun RecentSearchesSection(
    properties: List<Property>,
    onPropertyClick: (Int) -> Unit,
    onViewAllClick: () -> Unit
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
                onClick = onViewAllClick,
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

        Spacer(modifier = Modifier.height(32.dp))

        // ScrollView orizzontale delle proprietà
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(properties) { property ->
                PropertyCard(
                    property = property,
                    onPropertyClick = { onPropertyClick(property.id) },
                    modifier = Modifier.width(240.dp)
                )
            }
        }
    }
}

@Composable
fun PropertyCard(
    property: Property,
    onPropertyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onPropertyClick)
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
        }
    }
}

@Composable
fun PostAdSection(onPostAdClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
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
fun BottomNavigation(
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
fun BottomNavItem(
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
            tint = if (selected) TealSelected else IconColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = if (selected) TealSelected else Color.White,
            fontSize = 12.sp
        )
    }
}