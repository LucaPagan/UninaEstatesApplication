package com.dieti.dietiestates25.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.*
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun HomeScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: HomeViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val comuneDefault = "Napoli"

    val uiState by viewModel.uiState.collectAsState()

    // Controllo se l'utente è l'Admin (basato sull'ID impostato al login)
    val isAdmin = idUtente == "ADMIN_SESSION"

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (isAdmin) "Bentornato Admin" else "Bentornato",
                showAppIcon = true,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController, idUtente = idUtente)
        }
    ) { paddingValuesScaffold ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .padding(paddingValuesScaffold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                ClickableSearchBar(
                    placeholderText = "Cerca comune, zona...",
                    onClick = { navController.navigate(Screen.SearchScreen.withIdUtente(idUtente)) },
                    modifier = Modifier.padding(horizontal = dimensions.paddingMedium)
                )

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colorScheme.primary)
                        }
                    }
                    is HomeUiState.Error -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(dimensions.paddingLarge), contentAlignment = Alignment.Center) {
                            Text(text = "Errore caricamento dati.", color = colorScheme.error)
                        }
                    }
                    is HomeUiState.Success -> {
                        // 1. RICERCHE RECENTI
                        if (state.ricercheRecenti.isNotEmpty()) {
                            RecentSearchesSection(
                                recentSearches = state.ricercheRecenti,
                                onSearchClick = { query ->
                                    navController.navigate(
                                        Screen.SearchTypeSelectionScreen.buildRoute(idUtente, query, "")
                                    )
                                },
                                dimensions = dimensions, typography = typography, colorScheme = colorScheme
                            )
                            Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
                        }

                        // 2. IMMOBILI EVIDENZA
                        PropertyShowcaseSection(
                            title = "Immobili in evidenza",
                            items = state.immobili,
                            itemContent = { property ->
                                val imageUrl = viewModel.getImmobileMainImageUrl(property)
                                AppPropertyCard(
                                    modifier = Modifier.width(dimensions.propertyCardHeight).height(dimensions.circularIconSize),
                                    price = "€ ${property.prezzo?.let { String.format("%,d", it) } ?: "Tratt."}",
                                    imageUrl = imageUrl,
                                    address = property.indirizzo ?: "Zona non specificata",
                                    details = listOfNotNull(property.categoria, property.mq?.let { "$it mq" }),
                                    onClick = { navController.navigate(Screen.PropertyScreen.withId(property.id)) },
                                    horizontalMode = false
                                )
                            },
                            onSeeAllClick = {
                                navController.navigate(Screen.ApartmentListingScreen.buildRoute(idUtente, comuneDefault, ""))
                            },
                            listContentPadding = PaddingValues(horizontal = dimensions.paddingLarge)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                HomeScreenPostAdSection(
                    navController = navController,
                    idUtente = idUtente,
                    dimensions = dimensions,
                    typography = typography,
                    colorScheme = colorScheme
                )

                // --- TASTO SPECIALE PER ADMIN ---
                if (isAdmin) {
                    Spacer(modifier = Modifier.height(dimensions.spacingLarge))
                    Button(
                        onClick = {
                            // FIX: Ora naviga alla vera Dashboard Admin, non al Manager
                            navController.navigate(Screen.AdminDashboardScreen.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensions.paddingLarge),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.tertiary)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("VAI ALLA DASHBOARD ADMIN")
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
            }
        }
    }
}

@Composable
fun RecentSearchesSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    Column {
        Text(
            text = "Ultime Ricerche",
            style = typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = dimensions.paddingLarge)
        )
        Spacer(modifier = Modifier.height(dimensions.spacingMedium))
        LazyRow(
            contentPadding = PaddingValues(horizontal = dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            items(recentSearches) { query ->
                Card(
                    onClick = { onSearchClick(query) },
                    shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.width(160.dp).height(100.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(dimensions.paddingMedium),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Default.History, null, tint = colorScheme.primary)
                        Text(text = query, style = typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenPostAdSection(
    navController: NavController,
    idUtente: String,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingLarge, vertical = dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Vuoi vendere o affittare il tuo immobile?", color = colorScheme.onBackground, style = typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        Text("Inserisci il tuo annuncio in pochi semplici passaggi.", color = colorScheme.onBackground.copy(alpha = 0.8f), style = typography.bodyMedium)
        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        AppSecondaryButton(
            text = "Pubblica annuncio",
            onClick = { navController.navigate(Screen.PropertySellScreen.withIdUtente(idUtente)) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}