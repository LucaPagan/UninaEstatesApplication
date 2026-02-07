package com.dieti.dietiestates25.ui.features.property

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppPropertyCard
import com.dieti.dietiestates25.ui.components.AppPropertyViewButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun YourPropertyScreen(
    navController: NavController,
    idUtente: String,
    viewModel: YourPropertyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val gradientColors = arrayOf(
        0.0f to colorScheme.primary, 0.20f to colorScheme.background,
        0.60f to colorScheme.background, 1.0f to colorScheme.primary
    )

    // Carica i dati all'ingresso
    LaunchedEffect(Unit) {
        viewModel.loadMyProperties()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GeneralHeaderBar(
                title = "Le tue proprietà",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors))
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is YourPropertyState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                }
                is YourPropertyState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = colorScheme.error,
                            style = typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(dimensions.paddingMedium)
                        )
                    }
                }
                is YourPropertyState.Success -> {
                    if (state.immobili.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Non hai ancora inserito nessun immobile.",
                                style = typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = dimensions.paddingMedium,
                                end = dimensions.paddingMedium,
                                top = dimensions.paddingMedium,
                                bottom = dimensions.paddingLarge
                            ),
                            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                        ) {
                            items(items = state.immobili, key = { it.id }) { immobile ->
                                // Gestione URL Immagine
                                val imageUrl = immobile.urlImmagine.toString()

                                AppPropertyCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dimensions.propertyCardHeight),
                                    price = "€ ${immobile.prezzo ?: "N/D"}",
                                    imageUrl = imageUrl,
                                    address = immobile.indirizzo ?: "Indirizzo non disponibile",
                                    details = listOf(immobile.titolo ?: "Senza titolo"),
                                    onClick = {
                                        navController.navigate(Screen.PropertyScreen.withId(immobile.id))
                                    },
                                    actionButton = {
                                        AppPropertyViewButton(
                                            text = "Modifica",
                                            onClick = {
                                                navController.navigate(Screen.EditPropertyScreen.route)
                                            }
                                        )
                                    },
                                    horizontalMode = false,
                                    imageHeightVerticalRatio = 0.50f,
                                    elevationDp = dimensions.elevationSmall
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun YourPropertyScreenPreview() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        YourPropertyScreen(
            navController = navController,
            idUtente = "previewUser"
        )
    }
}