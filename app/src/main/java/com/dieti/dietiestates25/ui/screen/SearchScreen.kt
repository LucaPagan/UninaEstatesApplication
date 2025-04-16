package com.dieti.dietiestates25.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.ui.theme.TealPrimary
import com.dieti.dietiestates25.ui.theme.TealLightest
import com.dieti.dietiestates25.ui.theme.TextGray
import com.dieti.dietiestates25.ui.theme.White

@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Sample search results (you can replace with real data)
    val searchResults = listOf("Napoli - Comune", "Roma - Comune")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        if (isSearchActive) {
            // Search results view
            SearchResultsView(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onBackPressed = { isSearchActive = false },
                onClearSearch = { searchQuery = "" },
                results = searchResults
            )
        } else {
            // Normal search screen
            // Header con "RICERCA"
            Header()

            Spacer(modifier = Modifier.height(48.dp))

            // Campo di ricerca comune
            SearchField(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchClick = { isSearchActive = true }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Bottone "Cerca su mappa"
            SearchMapButton()

            Spacer(modifier = Modifier.height(24.dp))

            // Bottone "Cerca per metro"
            SearchMetroButton()

            // Spazio aggiuntivo per eventuali altri elementi
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun Header() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = TealPrimary,
        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RICERCA",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Cerca comune",
                    color = TextGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(TealLightest.copy(alpha = 0.5f))
                .clickable { onSearchClick() },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TealLightest.copy(alpha = 0.5f),
                unfocusedContainerColor = TealLightest.copy(alpha = 0.5f),
                disabledContainerColor = TealLightest.copy(alpha = 0.5f),
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                cursorColor = TealPrimary
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cerca",
                        tint = TextGray
                    )
                }
            }
        )
    }
}

@Composable
fun SearchResultsView(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackPressed: () -> Unit,
    onClearSearch: () -> Unit,
    results: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Search bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Indietro"
                )
            }

            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Cerca comune, zona...",
                        color = TextGray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(TealLightest.copy(alpha = 0.5f)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TealLightest.copy(alpha = 0.5f),
                    unfocusedContainerColor = TealLightest.copy(alpha = 0.5f),
                    disabledContainerColor = TealLightest.copy(alpha = 0.5f),
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    cursorColor = TealPrimary
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancella",
                            tint = TextGray
                        )
                    }
                }
            )
        }

        // Search results
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(results) { result ->
                SearchResultItem(result)
            }
        }
    }
}

@Composable
fun SearchResultItem(result: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle result selection */ },
        color = TealPrimary
    ) {
        Text(
            text = result,
            color = White,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SearchMapButton() {
    Button(
        onClick = { /* TODO: Implementa ricerca su mappa */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TealPrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Mappa",
                tint = White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Cerca su mappa",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SearchMetroButton() {
    Button(
        onClick = { /* TODO: Implementa ricerca per metro */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TealPrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Train,
                contentDescription = "Metro",
                tint = White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Cerca per metro",
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    SearchScreen()
}