package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.components.CustomSearchAppBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

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
        var searchBarHasFocus by remember { mutableStateOf(false) }

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

        /*LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }*/

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
                modifier = Modifier.fillMaxSize()
            ) {
                CustomSearchAppBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { query ->
                        searchQuery = query
                    },
                    onBackPressed = {
                        if (searchQuery.isNotBlank()) {
                            searchQuery = ""
                        } else if (searchBarHasFocus) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                        else {
                            navController.popBackStack() //
                        }
                    },
                    onClearSearch = {
                        searchQuery = ""
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                    placeholderText = "Cerca comune, zona...",
                    focusRequester = focusRequester,
                    onFocusChanged = { hasFocus ->
                        searchBarHasFocus = hasFocus
                    },
                    imeAction = ImeAction.Search,
                    onSearchKeyboardAction = {
                        if (searchQuery.isNotBlank()) {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }
                )
                when {
                    // 1. Se c'è testo nella query, mostra i risultati o "nessun risultato"
                    searchQuery.isNotBlank() -> {
                        SearchResultsList(
                            navController = navController,
                            comuni = filteredComuni,
                            searchQuery = searchQuery,
                            colorScheme = colorScheme,
                            typography = typography,
                            idUtente = idUtente,
                            onItemClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                searchQuery = ""
                            }
                        )
                    }
                    // 2. Se non c'è testo, ma la barra di ricerca ha il focus, mostra "Inizia a digitare"
                    searchBarHasFocus -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Dimensions.paddingLarge),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = "Inizia a digitare per cercare un comune o una zona.",
                                color = colorScheme.onBackground.copy(alpha = 0.7f),
                                style = typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = Dimensions.paddingLarge)
                            )
                        }
                    }
                    // 3. Altrimenti (no testo, no focus), mostra i bottoni di scelta
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimensions.paddingLarge)
                                .padding(top = Dimensions.paddingLarge),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))
                            AppSecondaryButton(
                                text = "Cerca su mappa",
                                onClick = {
                                    // TODO: implementa navigazione
                                    // navController.navigate(Screen.MapScreen.route)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Default.Map,
                                iconContentDescription = "Cerca su mappa"
                            )
                            Spacer(modifier = Modifier.height(Dimensions.spacingLarge))
                            AppSecondaryButton(
                                text = "Cerca per metro",
                                onClick = {
                                    focusRequester.requestFocus() // Richiede il focus sulla barra
                                    keyboardController?.show()    // Mostra la tastiera
                                },
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Default.Train,
                                iconContentDescription = "Cerca per metro"
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultsList(
    navController: NavController,
    comuni: List<String>,
    searchQuery: String,
    colorScheme: ColorScheme,
    typography: Typography,
    idUtente: String,
    onItemClick: () -> Unit
) {
    // Mostra "nessun risultato" solo se si è cercato qualcosa attivamente
    if (comuni.isEmpty() && searchQuery.isNotBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.paddingLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nessun risultato trovato per \"$searchQuery\"",
                color = colorScheme.onBackground.copy(alpha = 0.7f),
                style = typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    } else if (comuni.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingMedium),
            contentPadding = PaddingValues(vertical = Dimensions.spacingSmall)
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
    // Altrimenti non mostra nulla (es. query vuota ma barra non focalizzata)
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
            .padding(vertical = Dimensions.spacingSmall)
            .clickable {
                onItemClick()
                navController.navigate(Screen.ApartmentListingScreen.withArgs(idUtente, comune))
            },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        shape = RoundedCornerShape(Dimensions.cornerRadiusSmall),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.elevationSmall
        )
    ) {
        Text(
            text = comune,
            color = colorScheme.onSurface,
            style = typography.bodyLarge,
            modifier = Modifier.padding(Dimensions.paddingMedium)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        SearchScreen(navController = navController, idUtente = "user123")
    }
}