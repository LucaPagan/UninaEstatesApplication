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

        val searchResults = listOf("Napoli - Comune", "Roma - Comune", "Milano - Comune", "Torino - Comune", "Firenze - Comune", "Bologna - Comune", "Genova - Comune")
        val filteredComuni = remember(searchQuery, searchResults) {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                searchResults.filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }

        val gradientColors = listOf(
            colorScheme.primary.copy(alpha = 0.7f),
            colorScheme.background,
            colorScheme.primary.copy(alpha = 0.7f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
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
                SearchAppBar(
                    navController = navController,
                    searchQuery = searchQuery,
                    onSearchQueryChange = {
                        searchQuery = it
                        isSearchActive = it.isNotBlank()
                    },
                    onBackPressed = {
                        if (isSearchActive) {
                            searchQuery = ""
                            isSearchActive = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onClearSearch = {
                        searchQuery = ""
                        isSearchActive = false
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
                    SearchResultsList(
                        navController = navController,
                        comuni = filteredComuni,
                        searchQuery = searchQuery, // Passa la searchQuery corrente
                        colorScheme = colorScheme,
                        typography = typography,
                        idUtente = idUtente,
                        onItemClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SearchOptionButton(
                            icon = Icons.Outlined.LocationOn,
                            text = "Cerca su mappa",
                            onClick = { navController.navigate("mapSearch/$idUtente") },
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        SearchOptionButton(
                            icon = Icons.Outlined.Train,
                            text = "Cerca per metro",
                            onClick = { navController.navigate("metroSearch/$idUtente") },
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAppBar(
    navController: NavController, // Non più usata direttamente qui per popBackStack se onBackPressed la gestisce
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
        color = colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // IconButton corretto: usa onBackPressed e ha lo sfondo/dimensione corretti
            IconButton(
                onClick = onBackPressed,
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

            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Cerca comune, zona...",
                        style = typography.bodyLarge,
                        color = colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(colorScheme.surface.copy(alpha = 0.2f))
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && !isSearchActive) {
                            keyboardController?.show()
                        }
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface.copy(alpha = 0.3f),
                    unfocusedContainerColor = colorScheme.surface.copy(alpha = 0.2f),
                    disabledContainerColor = colorScheme.surface.copy(alpha = 0.2f),
                    cursorColor = colorScheme.onPrimary, // Modificato per coerenza
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    // Assicurati che il colore del testo sia corretto anche qui
                    focusedTextColor = colorScheme.onPrimary,
                    unfocusedTextColor = colorScheme.onPrimary,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            keyboardController?.hide()
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
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca",
                            tint = colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                textStyle = typography.bodyLarge.copy(color = colorScheme.onPrimary)
            )
        }
    }
}

@Composable
fun SearchResultsList(
    navController: NavController,
    comuni: List<String>,
    searchQuery: String, // Aggiunto parametro searchQuery
    colorScheme: ColorScheme,
    typography: Typography,
    idUtente: String,
    onItemClick: () -> Unit
) {
    if (comuni.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nessun risultato trovato per \"$searchQuery\"", // Utilizza searchQuery
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
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(comuni) { comune ->
                SearchResultItem(
                    navController = navController,
                    comune = comune,
                    colorScheme = colorScheme,
                    typography = typography,
                    idUtente = idUtente,
                    onItemClick = onItemClick
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
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                onItemClick()
                navController.navigate(Screen.ApartmentListingScreen.withArgs(idUtente, comune))
            },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Text(
            text = comune,
            color = colorScheme.onSurface,
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
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
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
    // Per la preview, la modifica della status bar non avrà effetto visibile diretto nell'IDE
    // ma il layout con statusBarsPadding() può essere osservato.
    DietiEstatesTheme { // Assicurati che la Preview usi lo stesso tema per coerenza
        SearchScreen(navController = navController, idUtente = "user123")
    }
}