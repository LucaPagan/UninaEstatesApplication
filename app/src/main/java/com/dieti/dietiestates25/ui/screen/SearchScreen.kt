package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect // Importato per il focus iniziale
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
// Rimosso import dp se non usato direttamente (Dimensions lo usa)
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
        val dimensions = Dimensions // Usato Dimensions invece di dp singoli

        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val interactionSource = remember { MutableInteractionSource() }

        var searchQuery by remember { mutableStateOf("") }
        var searchBarHasFocus by remember { mutableStateOf(false) }

        // Dati di esempio per la ricerca (sostituire con dati reali o da ViewModel)
        val searchResults = remember {
            listOf("Napoli - Comune", "Roma - Comune", "Milano - Comune", "Torino - Comune", "Firenze - Comune", "Bologna - Comune", "Genova - Comune")
        }
        val filteredComuni = remember(searchQuery, searchResults) {
            if (searchQuery.isBlank()) {
                emptyList()
            } else {
                searchResults.filter { it.contains(searchQuery, ignoreCase = true) }
            }
        }

        val gradientColors = remember(colorScheme) { // Ricorda i colori se colorScheme può cambiare
            listOf(
                colorScheme.primary.copy(alpha = 0.7f),
                colorScheme.background,
                colorScheme.primary.copy(alpha = 0.7f)
            )
        }

        // Richiede il focus sulla barra di ricerca all'avvio della schermata
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            keyboardController?.show() // Opzionale: forza la visualizzazione della tastiera
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .clickable( // Per chiudere la tastiera cliccando fuori
                    interactionSource = interactionSource,
                    indication = null // Nessun effetto ripple
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CustomSearchAppBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { query -> searchQuery = query },
                    onBackPressed = {
                        if (searchQuery.isNotBlank()) {
                            searchQuery = ""
                        } else if (searchBarHasFocus) {
                            focusManager.clearFocus()
                            // keyboardController?.hide() // Nascondere la tastiera è gestito da clearFocus indirettamente
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onClearSearch = {
                        searchQuery = ""
                        focusRequester.requestFocus() // Mantiene il focus
                        keyboardController?.show()
                    },
                    placeholderText = "Cerca comune, zona...",
                    focusRequester = focusRequester,
                    onFocusChanged = { hasFocus -> searchBarHasFocus = hasFocus },
                    imeAction = ImeAction.Search,
                    onSearchKeyboardAction = {
                        if (searchQuery.isNotBlank()) {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }
                )

                // Contenuto dinamico basato sullo stato della ricerca
                val showResults = searchQuery.isNotBlank()
                val showPrompt = searchBarHasFocus && !showResults
                val showOptions = !showResults && !showPrompt

                when {
                    showResults -> {
                        SearchResultsList(
                            navController = navController,
                            comuni = filteredComuni,
                            searchQuery = searchQuery,
                            colorScheme = colorScheme,
                            typography = typography,
                            dimensions = dimensions, // Passa dimensions se SearchResultsList lo usa
                            idUtente = idUtente,
                            onItemClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                searchQuery = "" // Resetta per tornare allo stato iniziale dopo la navigazione
                            }
                        )
                    }
                    showPrompt -> {
                        Box(
                            modifier = Modifier
                                .weight(1f) // Occupa lo spazio rimanente
                                .fillMaxWidth()
                                .padding(dimensions.paddingLarge),
                            contentAlignment = Alignment.TopCenter // Allineato in alto per più visibilità
                        ) {
                            Text(
                                text = "Inizia a digitare per cercare un comune o una zona.",
                                color = colorScheme.onBackground.copy(alpha = 0.7f),
                                style = typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = dimensions.paddingLarge)
                            )
                        }
                    }
                    showOptions -> {
                        Column(
                            modifier = Modifier
                                .weight(1f) // Occupa lo spazio rimanente
                                .fillMaxWidth()
                                .padding(horizontal = dimensions.paddingLarge)
                                .padding(top = dimensions.paddingLarge), // Spazio sotto la search bar
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(dimensions.spacingLarge))
                            AppSecondaryButton(
                                text = "Cerca su mappa",
                                onClick = {
                                    // TODO: Implementa navigazione a MapScreen
                                    // navController.navigate(Screen.MapScreen.route) // Esempio
                                },
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Default.Map,
                                iconContentDescription = "Cerca su mappa"
                            )
                            Spacer(modifier = Modifier.height(dimensions.spacingMedium)) // Spazio ridotto tra i due bottoni
                            AppSecondaryButton(
                                text = "Cerca per comune/zona", // Testo più esplicito
                                onClick = {
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Default.Train, // Icona esempio, potresti usare Icons.Default.LocationCity
                                iconContentDescription = "Cerca per comune o zona"
                            )
                            Spacer(modifier = Modifier.weight(1f)) // Spinge i bottoni in alto se c'è più spazio
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
    dimensions: Dimensions, // Aggiunto per coerenza se necessario
    idUtente: String,
    onItemClick: () -> Unit
) {
    if (comuni.isEmpty() && searchQuery.isNotBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize() // Occupa tutto lo spazio disponibile per centrare il messaggio
                .padding(dimensions.paddingLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nessun risultato trovato per \"$searchQuery\"",
                color = colorScheme.onBackground.copy(alpha = 0.7f),
                style = typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    } else if (comuni.isNotEmpty()) { // Mostra la lista solo se ci sono comuni da visualizzare
        LazyColumn(
            modifier = Modifier.fillMaxWidth(), // Non .fillMaxSize() per non coprire altri elementi se la lista è corta
            contentPadding = PaddingValues(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.spacingSmall
            ),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall) // Spazio tra gli item
        ) {
            items(items = comuni, key = { it }) { comune -> // Usa il comune stesso come chiave se univoco
                SearchResultItem(
                    navController = navController,
                    comune = comune,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions, // Passa dimensions
                    idUtente = idUtente,
                    onItemClick = onItemClick
                )
            }
        }
    }
    // Se comuni è vuoto e searchQuery è vuota, non mostra nulla (corretto, lo stato è gestito sopra)
}


@Composable
fun SearchResultItem(
    navController: NavController,
    comune: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions, // Aggiunto
    idUtente: String,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Rimosso padding(vertical) qui, gestito da Arrangement.spacedBy in LazyColumn
            .clickable {
                onItemClick()
                // Passa il comune selezionato e una stringa vuota per la ricerca testuale specifica
                // dentro SearchFilterScreen, che si concentrerà sui filtri per quel comune.
                navController.navigate(Screen.SearchFilterScreen.withArgs(idUtente, comune, ""))
            },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant // Leggermente diverso da surface per distinzione
        ),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium), // Più arrotondato
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensions.elevationSmall
        )
    ) {
        Text(
            text = comune,
            color = colorScheme.onSurfaceVariant,
            style = typography.bodyLarge,
            modifier = Modifier
                .padding(dimensions.paddingMedium) // Padding interno alla Card
                .fillMaxWidth(), // Assicura che il testo usi tutta la larghezza per l'allineamento
            textAlign = TextAlign.Start // Allineamento standard a sinistra
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