package com.dieti.dietiestates25.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

// --- COMPOSABLE STATEFUL (Logica + Navigazione) ---
@Composable
fun SearchScreen(
    navController: NavController,
    idUtente: String,
    viewModel: SearchViewModel = viewModel()
) {
    // Stati raccolti dal ViewModel
    val citySuggestions by viewModel.citySuggestions.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    var queryText by remember { mutableStateOf("") }

    // Effetti collaterali per caricare i dati
    LaunchedEffect(queryText) {
        viewModel.fetchCitySuggestions(queryText)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchRecentSearches()
    }

    // Passiamo stato e callback alla UI "stupida" (Stateless)
    SearchScreenContent(
        queryText = queryText,
        onQueryChange = { queryText = it },
        citySuggestions = citySuggestions,
        recentSearches = recentSearches,
        onBackClick = { navController.popBackStack() },
        onSearch = {
            if (it.isNotBlank()) {
                navController.navigate(
                    Screen.SearchTypeSelectionScreen.buildRoute(
                        idUtentePath = idUtente,
                        comunePath = it,
                        ricercaPath = ""
                    )
                )
            }
        },
        onRecentSearchClicked = { selectedQuery ->
            navController.navigate(
                Screen.SearchTypeSelectionScreen.buildRoute(
                    idUtentePath = idUtente,
                    comunePath = selectedQuery,
                    ricercaPath = ""
                )
            )
        },
        onClearRecentSearch = { queryToDelete ->
            viewModel.clearRecentSearch(queryToDelete)
        },
        onCitySuggestionClicked = { city ->
            navController.navigate(
                Screen.SearchTypeSelectionScreen.buildRoute(
                    idUtentePath = idUtente,
                    comunePath = city,
                    ricercaPath = ""
                )
            )
        }
    )
}

// --- COMPOSABLE STATELESS (Solo UI per Preview) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    queryText: String,
    onQueryChange: (String) -> Unit,
    citySuggestions: List<String>,
    recentSearches: List<String>,
    onBackClick: () -> Unit,
    onSearch: (String) -> Unit,
    onRecentSearchClicked: (String) -> Unit,
    onClearRecentSearch: (String) -> Unit,
    onCitySuggestionClicked: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(colorScheme.primary) // Sfondo "verdino" (Primary)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensions.paddingSmall, vertical = dimensions.paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = colorScheme.onPrimary // Tinta icona per contrasto su sfondo Primary
                        )
                    }

                    SearchBar(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = dimensions.paddingSmall)
                            .height(56.dp),
                        query = queryText,
                        onQueryChange = onQueryChange,
                        onSearch = onSearch,
                        active = false,
                        onActiveChange = {},
                        placeholder = { Text("Cerca comune o zona...", style = typography.bodyLarge) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = colorScheme.onSurfaceVariant) },
                        colors = SearchBarDefaults.colors(
                            // Sfondo SearchBar bianco/surface per risaltare sul verde
                            containerColor = colorScheme.surface,
                            dividerColor = Color.Transparent,
                            inputFieldColors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        ),
                        content = {}
                    )
                }
                // Colore divisore più chiaro per stare su sfondo scuro
                HorizontalDivider(color = colorScheme.onPrimary.copy(alpha = 0.2f))
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            if (queryText.isEmpty()) {
                // Vista Cronologia
                RecentSearchesView(
                    recentSearches = recentSearches,
                    onRecentSearchClicked = onRecentSearchClicked,
                    onClearRecentSearch = onClearRecentSearch,
                    typography = typography,
                    colorScheme = colorScheme,
                    dimensions = dimensions
                )
            } else {
                // Vista Suggerimenti
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = dimensions.paddingMedium, bottom = dimensions.paddingMedium)
                ) {
                    if (citySuggestions.isEmpty() && queryText.length > 2) {
                        item {
                            Text(
                                text = "Nessun risultato trovato.",
                                modifier = Modifier
                                    .padding(dimensions.paddingMedium)
                                    .fillMaxWidth(),
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(citySuggestions) { city ->
                            // MODIFICATO: Card per i comuni suggeriti (Nuove Ricerche)
                            Card(
                                onClick = { onCitySuggestionClicked(city) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = dimensions.paddingMedium, vertical = 4.dp),
                                shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
                                colors = CardDefaults.cardColors(
                                    containerColor = colorScheme.primary, // Verde Primario come richiesto
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensions.paddingMedium),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(dimensions.paddingMedium))
                                    Text(
                                        text = city,
                                        style = typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "1. Cronologia Vuota")
@Composable
fun PreviewSearchScreenEmpty() {
    DietiEstatesTheme {
        SearchScreenContent(
            queryText = "",
            onQueryChange = {},
            citySuggestions = emptyList(),
            recentSearches = emptyList(),
            onBackClick = {},
            onSearch = {},
            onRecentSearchClicked = {},
            onClearRecentSearch = {},
            onCitySuggestionClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Con Ricerche Recenti")
@Composable
fun PreviewSearchScreenHistory() {
    DietiEstatesTheme {
        SearchScreenContent(
            queryText = "",
            onQueryChange = {},
            citySuggestions = emptyList(),
            recentSearches = listOf("Napoli", "Roma Centro", "Milano Bilocale", "Giugliano in Campania"),
            onBackClick = {},
            onSearch = {},
            onRecentSearchClicked = {},
            onClearRecentSearch = {},
            onCitySuggestionClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Digitazione e Suggerimenti")
@Composable
fun PreviewSearchScreenTyping() {
    DietiEstatesTheme {
        SearchScreenContent(
            queryText = "Nap",
            onQueryChange = {},
            citySuggestions = listOf("Napoli", "Napoli Centro", "Nappo", "Napolis"),
            recentSearches = emptyList(),
            onBackClick = {},
            onSearch = {},
            onRecentSearchClicked = {},
            onClearRecentSearch = {},
            onCitySuggestionClicked = {}
        )
    }
}