package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.PrimaryColor
import com.dieti.dietiestates25.ui.theme.SecondaryColor

@Composable
fun HomeScreen(idUtente: String = "") {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Header()

        // Search bar
        SearchBar()

        // Recent searches section
        RecentSearches()

        // Post Ad section
        PostAdSection()

        // Spacer to push navigation to bottom
        Spacer(modifier = Modifier.weight(1f))

        // Bottom navigation
        BottomNavigation()
    }
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryColor)
            .padding(16.dp)
    ) {
        Text(
            text = "UNINAESTATES25",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SearchBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 64.dp, vertical = 16.dp)
    ) {
        // The most modern and simple approach with TextField
        androidx.compose.material3.TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Cerca casa") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = PrimaryColor,
                unfocusedIndicatorColor = PrimaryColor
            )
        )
    }
}

@Composable
fun RecentSearches() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ultime ricerche",
                fontWeight = FontWeight.Medium
            )
            TextButton(onClick = { }) {
                Text(
                    text = "Vai ad ultime ricerche",
                    color = PrimaryColor,
                    fontSize = 14.sp
                )
            }
        }

        // Property cards row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First property card (larger)
            PropertyCard(
                price = "400.000",
                type = "Appartamento",
                modifier = Modifier.weight(2f),
                imageRes = R.drawable.property1
            )

            // Second property card (smaller)
            PropertyCard(
                price = "",
                type = "",
                modifier = Modifier.weight(1f),
                imageRes = R.drawable.property2
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
    }
}

@Composable
fun PropertyCard(
    price: String,
    type: String,
    modifier: Modifier = Modifier,
    imageRes: Int
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            // Property image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Property Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Price overlay (only if price is not empty)
            if (price.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color(0x88000000), RoundedCornerShape(4.dp))
                        .padding(4.dp)
                ) {
                    Text(
                        text = price,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = type,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PostAdSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inserisci il tuo annuncio nell'app",
            modifier = Modifier.padding(bottom = 16.dp),
            fontWeight = FontWeight.Medium
        )

        Button(
            onClick = { },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(48.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = PrimaryColor
            )
        ) {
            Text("Pubblica annuncio")
        }
    }
}

@Composable
fun BottomNavigation() {
    NavigationBar(
        containerColor = SecondaryColor,
        contentColor = PrimaryColor
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Esplora") },
            label = { Text("Esplora") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifiche") },
            label = { Text("Notifiche") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profilo") },
            label = { Text("Profilo") },
            selected = false,
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DietiEstatesTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen()
        }
    }
}