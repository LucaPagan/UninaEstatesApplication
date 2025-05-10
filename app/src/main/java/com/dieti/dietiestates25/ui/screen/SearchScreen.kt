package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

@Composable
fun SearchScreen(navController: NavController, idUtente: String) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val interactionSource = remember { MutableInteractionSource() }


        var searchQuery by remember { mutableStateOf("") }
        var isSearchActive by remember { mutableStateOf(false) }

        // Sample search results (you can replace with real data)
        val searchResults = listOf("Napoli - Comune", "Roma - Comune", "Milano - Comune", "Torino - Comune", "Firenze - Comune", "Bologna - Comune", "Genova - Comune")

        // Filter results based on search query
        val filteredComuni = remember(searchQuery, searchResults) {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                searchResults.filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }

        // Smoother gradient background
        val gradientColors = listOf(
            colorScheme.primary.copy(alpha = 0.7f), // Subtle primary at top
            colorScheme.background,              // Background in the middle
            colorScheme.primary.copy(alpha = 0.7f) // Subtle primary at bottom
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                // Add clickable to dismiss keyboard when clicking outside search area
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Unified Search Bar (always visible, changes appearance)
                SearchAppBar(
                    navController = navController,
                    searchQuery = searchQuery,
                    onSearchQueryChange = {
                        searchQuery = it
                        isSearchActive = it.isNotBlank() // Activate search when query is not blank
                    },
                    onBackPressed = {
                        if (isSearchActive) {
                            searchQuery = "" // Clear query
                            isSearchActive = false // Deactivate search
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        } else {
                            navController.popBackStack() // Go back to home
                        }
                    },
                    onClearSearch = {
                        searchQuery = ""
                        isSearchActive = false // Deactivate search
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                    isSearchActive = isSearchActive,
                    colorScheme = colorScheme,
                    typography = typography,
                    focusRequester = focusRequester,
                    keyboardController = keyboardController
                )

                if (isSearchActive) {
                    // Search results view
                    SearchResultsList(
                        navController = navController,
                        comuni = filteredComuni,
                        colorScheme = colorScheme,
                        typography = typography,
                        idUtente = idUtente,
                        onItemClick = {
                            // When an item is clicked, hide keyboard and clear focus
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            // Navigation happens inside SearchResultItem
                        }
                    )
                } else {
                    // Content when search is not active
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp), // Add some space below the search bar
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp)) // Spacing below the search bar

                        // Bottone "Cerca su mappa"
                        SearchOptionButton(
                            icon = Icons.Outlined.LocationOn,
                            text = "Cerca su mappa",
                            onClick = { navController.navigate("mapSearch/$idUtente") },
                            colorScheme = colorScheme,
                            typography = typography
                        )

                        Spacer(modifier = Modifier.height(16.dp)) // Spacing between buttons

                        // Bottone "Cerca per metro"
                        SearchOptionButton(
                            icon = Icons.Outlined.Train,
                            text = "Cerca per metro",
                            onClick = { navController.navigate("metroSearch/$idUtente") },
                            colorScheme = colorScheme,
                            typography = typography
                        )

                        // Spazio aggiuntivo
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAppBar(
    navController: NavController,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackPressed: () -> Unit,
    onClearSearch: () -> Unit,
    isSearchActive: Boolean,
    colorScheme: ColorScheme,
    typography: Typography,
    focusRequester: FocusRequester,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = colorScheme.primary, // Use primary color for the app bar background
        // No rounded corners for a standard app bar look
        shadowElevation = 4.dp // Add a subtle shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Add vertical padding
                .padding(start = 8.dp, end = 16.dp), // Adjust horizontal padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .background(colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Cerca comune, zona...",
                        style = typography.bodyLarge, // Use bodyLarge for placeholder
                        color = colorScheme.onPrimary.copy(alpha = 0.7f) // Softer placeholder color
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp) // Add horizontal padding to TextField
                    .clip(RoundedCornerShape(28.dp)) // Rounded corners for the input field
                    .background(colorScheme.surface.copy(alpha = 0.2f)) // Semi-transparent surface background
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && !isSearchActive) {
                            // Automatically show keyboard when TextField gains focus and search is not active yet
                            keyboardController?.show()
                        }
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface.copy(alpha = 0.3f), // Slightly more opaque when focused
                    unfocusedContainerColor = colorScheme.surface.copy(alpha = 0.2f),
                    disabledContainerColor = colorScheme.surface.copy(alpha = 0.2f),
                    cursorColor = colorScheme.onSurface, // Use onSurface for cursor
                    focusedIndicatorColor = Color.Transparent, // Remove indicators
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            // Perform search action here if needed, or rely on onSearchQueryChange
                            keyboardController?.hide()
                            // Example: navController.navigate(...) based on query
                        }
                    }
                ),
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancella",
                                tint = colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        // Optional: Show a search icon when query is empty
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca",
                            tint = colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                textStyle = typography.bodyLarge.copy(color = colorScheme.onPrimary) // Text color
            )
        }
    }
}

@Composable
fun SearchResultsList(
    navController: NavController,
    comuni: List<String>,
    colorScheme: ColorScheme,
    typography: Typography,
    idUtente: String,
    onItemClick: () -> Unit // Callback for item click
) {
    if (comuni.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nessun risultato trovato per \"${remember { idUtente }}\"", // Usa searchQuery qui invece di idUtente
                color = colorScheme.onBackground.copy(alpha = 0.7f),
                style = typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp) // Add vertical padding to the list
        ) {
            items(comuni) { comune ->
                SearchResultItem(
                    navController = navController,
                    comune = comune,
                    colorScheme = colorScheme,
                    typography = typography,
                    idUtente = idUtente,
                    onItemClick = onItemClick // Pass the callback
                )
            }
        }
    }
}


@Composable
fun SearchResultItem(
    navController: NavController,
    comune: String,
    colorScheme: ColorScheme,
    typography: Typography,
    idUtente: String,
    onItemClick: () -> Unit // Receive the callback
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // Reduced vertical padding between items
            .clickable {
                onItemClick() // Call the callback when clicked
                // Naviga alla schermata dell'elenco degli appartamenti
                navController.navigate(Screen.ApartmentListingScreen.withArgs(idUtente, comune))
            },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface // Use surface color for card background
        ),
        shape = RoundedCornerShape(8.dp), // Slightly less rounded corners for items
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp // Reduced elevation for a flatter look
        )
    ) {
        Text(
            text = comune,
            color = colorScheme.onSurface, // Use onSurface for text on surface
            style = typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SearchOptionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp), // Slightly reduced height for consistency
        shape = RoundedCornerShape(8.dp), // More standard rounded corners
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
                imageVector = icon,
                contentDescription = text,
                tint = colorScheme.onSecondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
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