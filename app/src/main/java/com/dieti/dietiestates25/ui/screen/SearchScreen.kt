package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.typography

@Composable
fun SearchScreen(navController: NavController, idUtente: String) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        var searchQuery by remember { mutableStateOf("") }
        var isSearchActive by remember { mutableStateOf(false) }

        // Sample search results (you can replace with real data)
        val searchResults = listOf("Napoli - Comune", "Roma - Comune")

        // Gradient background like WelcomeScreen
        val gradientColors = arrayOf(
            0.0f to colorScheme.primary,
            0.20f to colorScheme.background,
            0.60f to colorScheme.background,
            1.0f to colorScheme.primary
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (isSearchActive) {
                    // Search results view
                    SearchResultsView(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onBackPressed = { isSearchActive = false },
                        onClearSearch = { searchQuery = "" },
                        results = searchResults,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                } else {
                    // Normal search screen
                    // Header con "RICERCA"
                    Header(
                        colorScheme = colorScheme,
                        typography = typography,
                        onBackToHomeClick = {
                            // Naviga alla home passando l'idUtente come parametro
                            navController.popBackStack()
                        }
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Campo di ricerca comune
                    SearchField(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onSearchClick = { isSearchActive = true },
                        colorScheme = colorScheme
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Bottone "Cerca su mappa"
                    SearchMapButton(colorScheme = colorScheme, typography = typography)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Bottone "Cerca per metro"
                    SearchMetroButton(colorScheme = colorScheme, typography = typography)

                    // Spazio aggiuntivo per eventuali altri elementi
                    Spacer(modifier = Modifier.weight(1f))

                    // Bottone per tornare alla homepage
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun Header(
    colorScheme: ColorScheme,
    typography: Typography,
    onBackToHomeClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        color = colorScheme.primary,
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
                color = colorScheme.onPrimary,
                style = typography.titleLarge,
                letterSpacing = 1.sp
            )

            // Aggiungiamo il bottone per tornare alla homepage nell'header
            IconButton(
                onClick = onBackToHomeClick,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Torna alla home",
                    tint = colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    colorScheme: ColorScheme
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
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(colorScheme.secondary.copy(alpha = 0.3f))
                .clickable { onSearchClick(
                    )
                           },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.secondary.copy(alpha = 0.3f),
                unfocusedContainerColor = colorScheme.secondary.copy(alpha = 0.3f),
                disabledContainerColor = colorScheme.secondary.copy(alpha = 0.3f),
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                cursorColor = colorScheme.primary
            ),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cerca",
                        tint = colorScheme.onSurface.copy(alpha = 0.6f)
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
    results: List<String>,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onBackground
                )
            }

            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Cerca comune, zona...",
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(32.dp))
                    .background(colorScheme.secondary.copy(alpha = 0.3f)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.secondary.copy(alpha = 0.3f),
                    unfocusedContainerColor = colorScheme.secondary.copy(alpha = 0.3f),
                    disabledContainerColor = colorScheme.secondary.copy(alpha = 0.3f),
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    cursorColor = colorScheme.primary
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancella",
                            tint = colorScheme.onSurface.copy(alpha = 0.6f)
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
                SearchResultItem(result, colorScheme, typography)
            }
        }
    }
}

@Composable
fun SearchResultItem(result: String, colorScheme: ColorScheme, typography: Typography) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* TODO: Handle result selection */ },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Text(
            text = result,
            color = colorScheme.onPrimary,
            style = typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SearchMapButton(colorScheme: ColorScheme, typography: Typography) {
    Button(
        onClick = { /* TODO: Implementa ricerca su mappa */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondary,
            contentColor = colorScheme.onSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Mappa",
                tint = colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Cerca su mappa",
                style = typography.labelLarge
            )
        }
    }
}

@Composable
fun SearchMetroButton(colorScheme: ColorScheme, typography: Typography) {
    Button(
        onClick = { /* TODO: Implementa ricerca per metro */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondary,
            contentColor = colorScheme.onSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Train,
                contentDescription = "Metro",
                tint = colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Cerca per metro",
                style = typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    val navController = rememberNavController()
    SearchScreen(navController = navController, idUtente = "user123")
}