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

    LaunchedEffect(idUtente) {
        viewModel.loadDashboardData(idUtente)
    }

    val onLogout = {
        authViewModel.logout()
        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard Manager",
                showAppIcon = true,
                actionIcon = Icons.Default.Logout,
                onActionClick = onLogout,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
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
                        // FIX: Navigazione aggiornata alla schermata notifiche manager
                        onClick = { navController.navigate(Screen.ManagerNotificationScreen.route) },
                        dimensions = dimensions,
                        typography = typography,
                        colorScheme = colorScheme
                    )
                    StatCard(
                        title = "Proposte",
                        count = uiState.proposalCount.toString(),
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.RequestsScreen.withIdUtente(idUtente)) },
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

                ManagerMenuButton(
                    text = "Visualizza i tuoi immobili",
                    onClick = { navController.navigate(Screen.YourPropertyScreen.route) },
                    icon = Icons.Default.House,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                AppPrimaryButton(
                    text = "Pubblica Annuncio",
                    onClick = { navController.navigate(Screen.PropertySellScreen.withIdUtente(idUtente)) },
                    icon = Icons.Default.AddHome,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.isCapo) {
                    Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                    AppSecondaryButton(
                        text = "Crea un Agente",
                        onClick = { navController.navigate(Screen.RegisterScreen.route) },
                        icon = Icons.Default.PersonAdd,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
            }
        }
    }
}

// ... (StatCard resta uguale) ...
@Composable
fun StatCard(
    title: String,
    count: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme,
) {
    Card(
        modifier = modifier.height(dimensions.infoCardHeight),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(dimensions.paddingMedium)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(dimensions.iconSizeExtraLarge)
                )
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
            CircularIconActionButton(
                onClick = onClick,
                iconVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Vai a $title",
                modifier = Modifier.align(Alignment.BottomEnd),
                backgroundColor = colorScheme.surfaceDim,
                iconTint = colorScheme.primary,
                buttonSize = dimensions.iconSizeLarge,
                iconSize = dimensions.iconSizeMedium
            )
        }
    }
}