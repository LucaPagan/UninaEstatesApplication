package com.dieti.dietiestates25.ui.features.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.*
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun ManagerScreen(
    navController: NavController,
    idUtente: String,
    viewModel: ManagerViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val uiState by viewModel.uiState.collectAsState()

    // Carichiamo i dati all'avvio
    LaunchedEffect(idUtente) {
        viewModel.loadDashboardData(idUtente)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Area Manager",
                showAppIcon = true,
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

                // --- SEZIONE CONTATORI ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                ) {
                    StatCard(
                        title = "Notifiche",
                        count = uiState.notificationCount.toString(),
                        icon = Icons.Default.Notifications,
                        color = colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    )
                    StatCard(
                        title = "Proposte",
                        count = uiState.proposalCount.toString(),
                        icon = Icons.Default.Description, // O icona Proposte
                        color = colorScheme.tertiary, // Colore diverso per distinguere
                        modifier = Modifier.weight(1f),
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

                // --- PULSANTI DI NAVIGAZIONE ---
                // Usiamo ManagerMenuButton per le liste come da richiesta
                ManagerMenuButton(
                    text = "Vai alle Notifiche",
                    onClick = { navController.navigate(Screen.NotificationScreen.route) },
                    icon = Icons.Default.NotificationsActive,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )

                Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                ManagerMenuButton(
                    text = "Vai alle Proposte",
                    // Qui navighiamo alla schermata delle richieste/proposte
                    // Assumo Screen.RequestsScreen per ora
                    onClick = { navController.navigate(Screen.RequestsScreen.withIdUtente(idUtente)) },
                    icon = Icons.Default.Assignment,
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

// Componente Helper per le Card dei Contatori
@Composable
fun StatCard(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensions.paddingMedium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(dimensions.iconSizeLarge)
                )
            }

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