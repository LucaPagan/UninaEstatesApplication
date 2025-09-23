package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History // Icona per ricerche recenti
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf // Per lista osservabile di ricerche recenti
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.CustomSearchAppBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.theme.TealDeep

const val MAX_RECENT_SEARCHES = 5 // Limita il numero di ricerche recenti

@Composable
fun SearchScreen(navController: NavController, idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    var searchQuery by remember { mutableStateOf("") }
    var searchBarHasFocus by remember { mutableStateOf(false) }

    // Stato per le ricerche recenti (in memoria per questo esempio)
    val recentSearches = remember { mutableStateListOf<String>() }

    // Funzione per aggiungere una ricerca alle recenti
    fun addSearchToRecents(query: String) {
        if (query.isBlank()) return
        recentSearches.remove(query) // Rimuove se già presente per metterla in cima
        recentSearches.add(0, query) // Aggiunge in cima
        if (recentSearches.size > MAX_RECENT_SEARCHES) {
            recentSearches.removeAt(recentSearches.lastIndex) // Mantiene la lista alla dimensione massima
        }
    }

    // Funzione per rimuovere una ricerca dalle recenti
    fun removeSearchFromRecents(query: String) {
        recentSearches.remove(query)
    }

    val searchResults = remember {
        listOf(
            "Napoli - Comune",
            "Roma - Comune",
            "Milano - Comune",
            "Torino - Comune",
            "Firenze - Comune",
            "Bologna - Comune",
            "Genova - Comune"
        )
    }
    val filteredComuni = remember(searchQuery, searchResults) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            searchResults.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Status Bar con colore TealDeep fisso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(TealDeep)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    focusManager.clearFocus()
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
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onClearSearch = {
                        searchQuery = ""
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                    placeholderText = "Cerca comune, zona...",
                    focusRequester = focusRequester,
                    onFocusChanged = { hasFocus -> searchBarHasFocus = hasFocus },
                    imeAction = ImeAction.Search,
                    onSearchKeyboardAction = { query ->
                        if (query.isNotBlank()) {
                            addSearchToRecents(query) // Aggiunge alla cronologia
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            // Qui potresti voler navigare direttamente ai risultati o aggiornare la UI
                            // Per ora, l'aggiornamento di searchQuery e la perdita di focus mostreranno i risultati
                        }
                    }
                )

                val showResults = searchQuery.isNotBlank()
                val showPrompt = searchBarHasFocus && !showResults
                // Mostra le ricerche recenti se la query è vuota e la barra non ha il focus
                val showRecentSearches = !showResults && !showPrompt

                when {
                    showResults -> {
                        SearchResultsList(
                            navController = navController,
                            comuni = filteredComuni,
                            searchQuery = searchQuery,
                            colorScheme = colorScheme,
                            typography = typography,
                            dimensions = dimensions,
                            idUtente = idUtente,
                            onItemClick = { selectedComune -> // Modificato per ricevere il comune
                                addSearchToRecents(selectedComune) // Aggiunge alla cronologia prima di navigare
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                searchQuery = "" // Resetta la barra di ricerca
                                // Navigazione avviene in SearchResultItem
                            }
                        )
                    }

                    showPrompt -> {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(dimensions.paddingLarge),
                            contentAlignment = Alignment.TopCenter
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

                    showRecentSearches -> {
                        RecentSearchesView(
                            recentSearches = recentSearches,
                            onRecentSearchClicked = { recentQuery ->
                                searchQuery = recentQuery // Popola la barra e mostra i risultati
                                addSearchToRecents(recentQuery) // La sposta in cima
                                focusManager.clearFocus() // Opzionale: togli focus per mostrare subito risultati
                            },
                            onClearRecentSearch = { queryToClear ->
                                removeSearchFromRecents(queryToClear)
                            },
                            typography = typography,
                            colorScheme = colorScheme,
                            dimensions = dimensions
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSearchesView(
    recentSearches: List<String>,
    onRecentSearchClicked: (String) -> Unit,
    onClearRecentSearch: (String) -> Unit,
    typography: Typography,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensions.paddingLarge)
    ) {
        if (recentSearches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensions.paddingLarge),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Nessuna ricerca recente.",
                    color = colorScheme.onBackground.copy(alpha = 0.7f),
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimensions.paddingLarge)
                )
            }
        } else {
            Text(
                text = "Ricerche Recenti",
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = dimensions.paddingLarge, vertical = dimensions.spacingMedium)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = dimensions.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
            ) {
                items(items = recentSearches, key = { it }) { query ->
                    RecentSearchItem(
                        query = query,
                        onClick = { onRecentSearchClicked(query) },
                        onClearClick = { onClearRecentSearch(query) },
                        typography = typography,
                        colorScheme = colorScheme,
                        dimensions = dimensions
                    )
                }
            }
        }
    }
}

@Composable
fun RecentSearchItem(
    query: String,
    onClick: () -> Unit,
    onClearClick: () -> Unit,
    typography: Typography,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Ricerca Recente",
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
            Spacer(modifier = Modifier.width(dimensions.spacingMedium))
            Text(
                text = query,
                style = typography.bodyLarge,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onClearClick, modifier = Modifier.size(dimensions.iconSizeMedium + dimensions.spacingSmall)) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Cancella ricerca recente",
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(dimensions.iconSizeSmall)
            )
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
    dimensions: Dimensions,
    idUtente: String,
    onItemClick: (String) -> Unit // Modificato per passare il comune selezionato
) {
    if (comuni.isEmpty() && searchQuery.isNotBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
    } else if (comuni.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.spacingSmall
            ),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
        ) {
            items(items = comuni, key = { it }) { comune ->
                SearchResultItem(
                    navController = navController,
                    comune = comune,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions,
                    idUtente = idUtente,
                    onItemClick = { onItemClick(comune) } // Passa il comune al callback
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
    dimensions: Dimensions,
    idUtente: String,
    onItemClick: () -> Unit // Questo onClick generale è per l'azione pre-navigazione
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick() // Esegue azioni pre-navigazione (es. aggiunta a recenti, pulizia UI)
                // La navigazione vera e propria
                navController.navigate(
                    Screen.SearchFilterScreen.withInitialArgs(
                        idUtente,
                        comune,
                        ""
                    )
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensions.elevationSmall
        )
    ) {
        Text(
            text = comune,
            color = colorScheme.onSurfaceVariant,
            style = typography.bodyLarge,
            modifier = Modifier
                .padding(dimensions.paddingMedium)
                .fillMaxWidth(),
            textAlign = TextAlign.Start
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
