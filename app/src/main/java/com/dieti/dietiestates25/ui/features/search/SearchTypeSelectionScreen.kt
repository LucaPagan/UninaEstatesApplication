package com.dieti.dietiestates25.ui.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.capitalizeFirstLetter

@Composable
fun SearchTypeSelectionScreen(
    navController: NavController,
    idUtente: String,
    comune: String,
    ricerca: String,
    filters: FilterModel? = null // Parametro aggiunto per risolvere l'errore di compilazione
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    // Debugging della navigazione (opzionale)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        currentBackStackEntry?.let { entry ->
            println("Current destination: ${entry.destination.route}")
        }
    }

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    // Layout principale senza Scaffold
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // TopBar personalizzata
        SearchTypeSelectionTopBar(
            title = "Scegli Visualizzazione",
            onNavigationClick = {
                navController.popBackStack()
            }
        )

        // Contenuto principale
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Risultati per: ${comune.capitalizeFirstLetter()}" +
                        if (ricerca.isNotBlank() && ricerca.lowercase() != comune.lowercase()) {
                            " - ${ricerca.capitalizeFirstLetter()}"
                        } else {
                            ""
                        },
                style = typography.headlineSmall,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = dimensions.spacingExtraLarge)
            )

            AppPrimaryButton(
                text = "Visualizza come Lista",
                onClick = {
                    navController.navigate(
                        Screen.ApartmentListingScreen.buildRoute(
                            idUtentePath = idUtente,
                            comunePath = comune,
                            ricercaPath = ricerca,
                            filters = filters // Passiamo i filtri ricevuti
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Filled.ListAlt
            )

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            AppPrimaryButton(
                text = "Visualizza su Mappa",
                onClick = {
                    navController.navigate(
                        Screen.MapSearchScreen.buildRoute(
                            idUtentePath = idUtente,
                            comunePath = comune,
                            ricercaPath = ricerca,
                            filters = filters // Passiamo i filtri ricevuti
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Map
            )
        }
    }
}

@Composable
fun SearchTypeSelectionTopBar(
    title: String,
    onNavigationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    // Container che include la status bar
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(colorScheme.primaryContainer)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.headerBarHeight) // Altezza standard della TopAppBar
                .padding(horizontal = dimensions.paddingExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onPrimary
                )
            }

            Text(
                text = title,
                style = typography.titleLarge,
                color = colorScheme.onPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun PreviewSearchTypeSelectionScreen() {
    DietiEstatesTheme {
        SearchTypeSelectionScreen(
            navController = rememberNavController(),
            idUtente = "previewUser",
            comune = "Napoli",
            ricerca = "Centro",
            filters = null
        )
    }
}