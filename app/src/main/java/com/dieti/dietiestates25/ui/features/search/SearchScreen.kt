package com.dieti.dietiestates25.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.CustomSearchAppBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun SearchScreen(
    navController: NavController,
    idUtente: String,
    viewModel: SearchViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    var searchQuery by remember { mutableStateOf("") }
    var searchBarHasFocus by remember { mutableStateOf(false) }

    // Osserviamo i dati reali dal ViewModel
    val suggestedCities by viewModel.citySuggestions.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    // Chiamata al backend quando il testo cambia
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.fetchCitySuggestions(searchQuery)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .clickable(interactionSource = interactionSource, indication = null) {
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
                    placeholderText = "Cerca comune...",
                    focusRequester = focusRequester,
                    onFocusChanged = { hasFocus -> searchBarHasFocus = hasFocus },
                    imeAction = ImeAction.Search,
                    onSearchKeyboardAction = { query ->
                        if (query.isNotBlank()) {
                            // Se preme invio, usiamo il testo libero
                            navController.navigate(
                                Screen.SearchTypeSelectionScreen.buildRoute(
                                    idUtente, query, ""
                                )
                            )
                        }
                    }
                )

                val showResults = searchQuery.isNotBlank() && suggestedCities.isNotEmpty()
                val showPrompt = searchBarHasFocus && searchQuery.isBlank()
                val showRecentSearches = searchQuery.isBlank() && recentSearches.isNotEmpty()

                when {
                    showResults -> {
                        SearchResultsList(
                            navController = navController,
                            comuni = suggestedCities,
                            searchQuery = searchQuery,
                            colorScheme = colorScheme,
                            typography = typography,
                            dimensions = dimensions,
                            idUtente = idUtente,
                            onItemClick = { selectedComune ->
                                // Quando clicco un comune suggerito
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                searchQuery = ""
                                // Navigo alla selezione tipo visualizzazione
                                navController.navigate(
                                    Screen.SearchTypeSelectionScreen.buildRoute(
                                        idUtente, selectedComune, ""
                                    )
                                )
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
                                text = "Scrivi il nome di un comune.",
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
                                searchQuery = recentQuery
                                focusManager.clearFocus()
                            },
                            onClearRecentSearch = { queryToClear ->
                                viewModel.clearHistoryItem(queryToClear)
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

// ... Le altre funzioni (RecentSearchesView, RecentSearchItem) restano uguali ...

@Composable
fun SearchResultsList(
    navController: NavController,
    comuni: List<String>,
    searchQuery: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    idUtente: String,
    onItemClick: (String) -> Unit
) {
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
                comune = comune,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions,
                onItemClick = { onItemClick(comune) }
            )
        }
    }
}

@Composable
fun SearchResultItem(
    comune: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensions.elevationSmall
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensions.paddingMedium)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = colorScheme.primary
            )
            Spacer(modifier = Modifier.width(dimensions.spacingMedium))
            Text(
                text = comune,
                color = colorScheme.onSurfaceVariant,
                style = typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
    }
}