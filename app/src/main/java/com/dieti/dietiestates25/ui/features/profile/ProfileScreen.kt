package com.dieti.dietiestates25.ui.features.profile

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.DeleteConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.LogoutConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.data.model.ProfileData
import com.dieti.dietiestates25.ui.theme.Dimensions
import kotlinx.coroutines.launch

// --- ENTRY POINT (STATEFUL) ---
// Questo composable viene usato dall'App reale (con ViewModel)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Osserviamo lo stato dal ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Caricamento Iniziale
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile(context)
    }

    // Callbacks per le azioni (gestiscono logica e navigazione)
    val onLogout = {
        scope.launch {
            viewModel.logout(context)
            Toast.makeText(context, "Logout effettuato", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
        Unit
    }

    val onDelete = {
        scope.launch {
            Toast.makeText(context, "Profilo eliminato (Simulato)", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
        Unit
    }

    val onRetry = { viewModel.loadUserProfile(context) }

    // Delega alla UI Stateless
    ProfileScreenContent(
        navController = navController,
        uiState = uiState,
        onLogout = onLogout,
        onDelete = onDelete,
        onRetry = onRetry
    )
}

// --- UI IMPLEMENTATION (STATELESS) ---
// Questo composable contiene solo la UI, niente ViewModel. Perfetto per la Preview.
@Composable
fun ProfileScreenContent(
    navController: NavController,
    uiState: ProfileUiState,
    onLogout: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val focusManager = LocalFocusManager.current
    val dimensions = Dimensions

    // Stato locale per i dialoghi (UI state)
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    // Recuperiamo l'ID utente (email) dai dati caricati per la BottomBar
    val currentUserEmail = if (uiState is ProfileUiState.Success) {
        (uiState as ProfileUiState.Success).data.email
    } else ""

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Profilo Utente",
                showAppIcon = true,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            AppBottomNavigation(
                navController = navController,
                idUtente = currentUserEmail,
                onNavigateAttempt = { true }
            )
        }
    ) { scaffoldPaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(scaffoldPaddingValues)
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                }
                is ProfileUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Errore: ${uiState.message}",
                                color = colorScheme.error,
                                style = typography.bodyLarge,
                                modifier = Modifier.padding(dimensions.paddingMedium)
                            )
                            AppPrimaryButton(
                                onClick = onRetry,
                                text = "Riprova"
                            )
                        }
                    }
                }
                is ProfileUiState.Success -> {
                    val data = (uiState as ProfileUiState.Success).data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { focusManager.clearFocus() }
                            )
                            .verticalScroll(rememberScrollState())
                    ) {
                        ProfileInnerContent(
                            profileData = data,
                            onLogoutClick = { showLogoutDialog = true },
                            onDeleteClick = { showDeleteDialog = true },
                            typography = typography,
                            colorScheme = colorScheme,
                            navController = navController,
                            dimensions = dimensions
                        )
                    }
                }
            }
        }

        if (showLogoutDialog) {
            LogoutConfirmAlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                onLogoutConfirm = { _ ->
                    showLogoutDialog = false
                    onLogout()
                },
                isEditMode = false,
                hasUnsavedChanges = false,
                canSaveChanges = false,
                colorScheme = colorScheme,
                dimensions = dimensions,
                typography = typography
            )
        }

        if (showDeleteDialog) {
            DeleteConfirmAlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                onConfirmDelete = {
                    showDeleteDialog = false
                    onDelete()
                },
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun ProfileInnerContent(
    profileData: ProfileData,
    onLogoutClick: () -> Unit,
    onDeleteClick: () -> Unit,
    typography: Typography,
    colorScheme: ColorScheme,
    navController: NavController,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium)
            .padding(top = dimensions.paddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dati personali",
            style = typography.titleMedium,
            modifier = Modifier
                .padding(top = dimensions.paddingMedium, bottom = dimensions.paddingMedium)
                .align(Alignment.Start)
        )

        ProfileReadOnlyFields(
            profileData = profileData,
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )

        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

        // --- Opzioni Navigazione Commentate ---
        /*
        ProfileOtherOptions(
            profileData = profileData,
            typography = typography,
            colorScheme = colorScheme,
            navController = navController,
            dimensions = dimensions
        )
        Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
        */

        ProfileActionButtons(
            onLogoutClick = onLogoutClick,
            onDeleteProfileClick = onDeleteClick,
            dimensions = dimensions
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
    }
}

@Composable
private fun ProfileReadOnlyFields(
    profileData: ProfileData,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.primary.copy(alpha = 0.6f), shape = MaterialTheme.shapes.medium)
            .padding(dimensions.paddingMedium)
    ) {
        Text(
            text = "Nome e Cognome",
            style = typography.labelMedium,
            color = colorScheme.onPrimary.copy(alpha = 0.6f)
        )
        Text(
            text = profileData.name, // Assicurati che 'name' esista nel tuo ProfileData
            style = typography.bodyLarge,
            color = colorScheme.onPrimary,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall, bottom = dimensions.paddingMedium)
        )

        HorizontalDivider(color = colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Label Email
        Text(
            text = "Email",
            style = typography.labelMedium,
            color = colorScheme.onPrimary.copy(alpha = 0.6f)
        )
        Text(
            text = profileData.email,
            style = typography.bodyLarge,
            color = colorScheme.onPrimary,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall, bottom = dimensions.paddingMedium)
        )

        HorizontalDivider(color = colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Label Telefono
        Text(
            text = "Telefono",
            style = typography.labelMedium,
            color = colorScheme.onPrimary.copy(alpha = 0.6f)
        )
        val phoneDisplay = if (profileData.phoneNumberWithoutPrefix.isNotBlank()) {
            "${profileData.selectedPrefix.prefix} ${profileData.phoneNumberWithoutPrefix}"
        } else {
            "Non specificato"
        }

        Text(
            text = phoneDisplay,
            style = typography.bodyLarge,
            color = colorScheme.onPrimary,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall)
        )
    }
}

@Composable
private fun ProfileOtherOptions(
    profileData: ProfileData,
    typography: Typography,
    colorScheme: ColorScheme,
    navController: NavController,
    dimensions: Dimensions
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Altro",
            style = typography.titleMedium,
            modifier = Modifier
                .padding(bottom = dimensions.paddingSmall)
                .align(Alignment.Start)
        )
        ProfileOptionRow(
            text = "I tuoi immobili",
            icon = Icons.Default.NightsStay,
            onClick = { navController.navigate(Screen.YourPropertyScreen.route) },
            dimensions = dimensions,
            colorScheme = colorScheme
        )
        ProfileOptionRow(
            text = "Immobili salvati",
            icon = Icons.Default.NightsStay,
            onClick = { navController.navigate(
                Screen.ApartmentListingScreen.buildRoute(
                    idUtentePath = profileData.email,
                    comunePath = "",
                    ricercaPath = ""
                )
            ) },
            dimensions = dimensions,
            colorScheme = colorScheme
        )
        ProfileOptionRow(
            text = "Richieste appuntamenti",
            icon = Icons.Default.NightsStay,
            onClick = { navController.navigate(
                Screen.RequestsScreen.withIdUtente(profileData.email)
            ) },
            dimensions = dimensions,
            colorScheme = colorScheme
        )
    }
}

@Composable
fun ProfileOptionRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    dimensions: Dimensions,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = colorScheme.primary)
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Text(text, style = typography.bodyLarge, modifier = Modifier.weight(1f), color = colorScheme.onSurface)
        Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Vai a $text", tint = colorScheme.onSurface)
    }
}

@Composable
private fun ProfileActionButtons(
    onLogoutClick: () -> Unit,
    onDeleteProfileClick: () -> Unit,
    dimensions: Dimensions
) {
    AppPrimaryButton(
        onClick = onLogoutClick,
        text = "Esci Dal Profilo",
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
    AppRedButton(
        onClick = onDeleteProfileClick,
        text = "Elimina Profilo",
        modifier = Modifier.fillMaxWidth()
    )
}

// --- PREVIEW AGGIORNATA ---
@Preview(showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()

    // Creiamo dei dati fittizi per la preview
    // Nota: Uso il costruttore predefinito di ProfileData che hai definito tu con i valori di default
    // (Lorenzo, ecc.) oppure ne instanzio uno nuovo.
    val mockData = ProfileData(
        name = "Mario Rossi",
        email = "mario.rossi@unina.it",
        // selectedPrefix userà il default se non specificato
        phoneNumberWithoutPrefix = "3339998877"
    )

    DietiEstatesTheme {
        // Chiamiamo la versione STATELESS passandogli i dati mockati
        ProfileScreenContent(
            navController = navController,
            uiState = ProfileUiState.Success(mockData),
            onLogout = {},
            onDelete = {},
            onRetry = {}
        )
    }
}

// --- PREVIEW AGGIORNATA ---
@Preview(showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkProfileScreenPreview() {
    val navController = rememberNavController()

    // Creiamo dei dati fittizi per la preview
    // Nota: Uso il costruttore predefinito di ProfileData che hai definito tu con i valori di default
    // (Lorenzo, ecc.) oppure ne instanzio uno nuovo.
    val mockData = ProfileData(
        name = "Mario Rossi",
        email = "mario.rossi@unina.it",
        // selectedPrefix userà il default se non specificato
        phoneNumberWithoutPrefix = "3339998877"
    )

    DietiEstatesTheme {
        // Chiamiamo la versione STATELESS passandogli i dati mockati
        ProfileScreenContent(
            navController = navController,
            uiState = ProfileUiState.Success(mockData),
            onLogout = {},
            onDelete = {},
            onRetry = {}
        )
    }
}