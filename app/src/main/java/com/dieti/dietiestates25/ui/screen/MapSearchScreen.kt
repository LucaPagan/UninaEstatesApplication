package com.dieti.dietiestates25.ui.screen

// Assicurati che findActivity sia accessibile, ad esempio da ui.util
import com.dieti.dietiestates25.ui.utils.findActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map // Icona placeholder per la mappa
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.CustomSearchAppBar // Il tuo componente
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchScreen(
    navController: NavController,
    idUtente: String // Potrebbe servire per ricerche personalizzate o salvataggi
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    var searchQuery by remember { mutableStateOf("") }
    var searchBarHasFocus by remember { mutableStateOf(false) } // Per gestire UI in base al focus

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            CustomSearchAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onBackPressed = {
                    if (searchQuery.isNotBlank()) {
                        searchQuery = "" // Prima cancella la query
                    } else if (searchBarHasFocus) {
                        focusManager.clearFocus() // Poi togli il focus
                    } else {
                        navController.popBackStack() // Infine, torna indietro
                    }
                },
                onClearSearch = {
                    searchQuery = ""
                },
                placeholderText = "Inserisci indirizzo, città o zona...",
                onFocusChanged = { hasFocus ->
                    searchBarHasFocus = hasFocus
                },
                imeAction = ImeAction.Search,
                onSearchKeyboardAction = { query ->
                    if (query.isNotBlank()) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        // TODO: Qui avvieresti la ricerca sulla mappa con la 'query'
                        println("Avvio ricerca mappa per: $query")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: Implementa la logica per centrare sulla posizione corrente dell'utente
                    println("FAB Posizione Corrente cliccato")
                },
                containerColor = colorScheme.secondaryContainer,
                contentColor = colorScheme.onSecondaryContainer
            ) {
                Icon(Icons.Filled.MyLocation, "La Mia Posizione")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.surfaceVariant.copy(alpha = 0.3f)), // Sfondo leggero per il placeholder
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = "Area Mappa",
                        modifier = Modifier.size(120.dp),
                        tint = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                    Text(
                        "La mappa interattiva apparirà qui",
                        style = typography.titleMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Cerca un indirizzo per visualizzare la mappa.",
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = dimensions.paddingLarge)
                    )
                }
            }

            // TODO: Potresti voler aggiungere qui overlay sopra la mappa,
            // come una lista di risultati di ricerca testuali se la barra ha il focus
            // e c'è una query, o un pannello dettagli quando un marker è selezionato.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapSearchScreenPreview() {
    DietiEstatesTheme {
        MapSearchScreen(navController = rememberNavController(), idUtente = "previewUser")
    }
}
