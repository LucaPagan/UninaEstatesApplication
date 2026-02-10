package com.dieti.dietiestates25.ui.features.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.*
import com.dieti.dietiestates25.ui.features.auth.AuthViewModel
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions


@Composable
fun ManagerScreen(
    navController: NavController,
    idUtente: String,
    viewModel: ManagerViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val uiState by viewModel.uiState.collectAsState()

    // Carichiamo i dati all'avvio
    LaunchedEffect(idUtente) {
        viewModel.loadDashboardData(idUtente)
    }

    val onLogout = {
        authViewModel.logout() // Usiamo la logica robusta di AuthViewModel
        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard Manager",
                showAppIcon = true,
                // Aggiungiamo il tasto logout anche nella TopBar per comodità (opzionale)
                actionIcon = Icons.Default.Logout,
                onActionClick = onLogout,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
        // Nessuna BottomBar come richiesto
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensions.paddingLarge)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(dimensions.spacingLarge))

                // --- SEZIONE CONTATORI INTERATTIVI ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                ) {
                    StatCard(
                        title = "Notifiche",
                        count = uiState.notificationCount.toString(),
                        icon = Icons.Default.NotificationsActive,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.NotificationScreen.route) }, // Azione integrata
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    )
                    StatCard(
                        title = "Proposte",
                        count = uiState.proposalCount.toString(),
                        icon = Icons.Default.Description, // O icona Proposte
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.RequestsScreen.withIdUtente(idUtente)) }, // Azione integrata
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                Text(
                    text = "Gestione Rapida",
                    style = typography.titleMedium,
                    color = colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                // --- PULSANTI DI NAVIGAZIONE RIMASTI ---

                // Pulsante per vedere gli immobili personali
                ManagerMenuButton(
                    text = "Visualizza i tuoi immobili",
                    onClick = { navController.navigate(Screen.YourPropertyScreen.route) },
                    icon = Icons.Default.House,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                // --- AZIONI OPERATIVE ---

                AppPrimaryButton(
                    text = "Pubblica Annuncio",
                    onClick = { navController.navigate(Screen.PropertySellScreen.withIdUtente(idUtente)) },
                    icon = Icons.Default.AddHome,
                    modifier = Modifier.fillMaxWidth()
                )

                // Pulsante SOLO SE CAPO
                if (uiState.isCapo) {
                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                    AppSecondaryButton(
                        text = "Crea un Agente",
                        onClick = {
                            // Navigazione verso la registrazione (o schermata dedicata creazione agente)
                            navController.navigate(Screen.RegisterScreen.route)
                        },
                        icon = Icons.Default.PersonAdd,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
            }
        }
    }
}

// Componente Helper per le Card dei Contatori con pulsante circolare integrato
@Composable
fun StatCard(
    title: String,
    count: String,
    icon: ImageVector,
    onClick: () -> Unit, // Callback per il click
    modifier: Modifier,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme,
) {
    Card(
        modifier = modifier.height(dimensions.infoCardHeight), // Altezza leggermente aumentata per ospitare il bottone
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
    ) {
        Box( // Usiamo Box per sovrapporre il pulsante nell'angolo
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.paddingMedium)
        ) {
            // Contenuto principale (Icona e Testi)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icona colorata
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(dimensions.iconSizeExtraLarge)
                )

                // Numeri e Titolo
                Column {
                    Text(
                        text = count,
                        style = typography.displayLarge,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = title,
                        style = typography.labelLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // Pulsante Circolare nell'angolo in basso a destra
            CircularIconActionButton(
                onClick = onClick,
                iconVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, // Freccia per indicare navigazione
                contentDescription = "Vai a $title",
                modifier = Modifier.align(Alignment.BottomEnd), // Posizionato in basso a destra
                backgroundColor = colorScheme.surfaceDim,
                iconTint = colorScheme.primary,
                buttonSize = dimensions.iconSizeLarge, // Leggermente più piccolo del standard se serve
                iconSize = dimensions.iconSizeMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManagerScreenPreview() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        ManagerScreen(navController = navController, idUtente = "admin_test")
    }
}