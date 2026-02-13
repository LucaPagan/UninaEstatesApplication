package com.dieti.dietiestates25.ui.features.profile

import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.data.remote.ProfileData
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.DeleteConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.LogoutConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.SessionManager
import com.dieti.dietiestates25.ui.features.auth.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    idUtente: String?,
    viewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    Log.d("PROFILE_UI", "Screen started. ID: $idUtente")

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Caricamento dati
    LaunchedEffect(idUtente) {
        viewModel.loadUserProfile(context, idUtente)
    }

    // Gestione stato 'Utente Eliminato'
    LaunchedEffect(uiState) {
        if (uiState is ProfileUiState.UserDeleted) {
            Toast.makeText(context, "Profilo eliminato definitivamente.", Toast.LENGTH_LONG).show()
            authViewModel.logout()
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Funzioni di callback
    val onLogout = {
        authViewModel.logout()
        Toast.makeText(context, "Logout effettuato", Toast.LENGTH_SHORT).show()
        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    val onUpdateNotification: (Boolean?, Boolean?, Boolean?) -> Unit = { t, p, n ->
        viewModel.updateNotificationPreference(t, p, n)
    }

    ProfileScreenContent(
        navController = navController,
        idUtente = idUtente,
        uiState = uiState,
        onLogout = onLogout,
        onDelete = { viewModel.deleteProfile(context) },
        onRetry = { viewModel.loadUserProfile(context, idUtente) },
        onUpdateNotification = onUpdateNotification
    )
}

@Composable
fun ProfileScreenContent(
    navController: NavController,
    idUtente: String?,
    uiState: ProfileUiState,
    onLogout: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit,
    onUpdateNotification: (Boolean?, Boolean?, Boolean?) -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val snackbarHostState = remember { SnackbarHostState() }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // --- LOGICA VISIBILITÀ IMPOSTAZIONI ---
    val sessionUserId = remember { SessionManager.getUserId(context) }
    val userRole = remember { SessionManager.getUserRole(context) }

    // È il mio profilo se l'ID passato è nullo (default) oppure coincide con il mio ID sessione
    val isMyProfile by remember(idUtente, sessionUserId) {
        derivedStateOf { idUtente == null || idUtente == sessionUserId }
    }

    // Mostriamo i settings solo se è il mio profilo E non sono un manager
    // (I manager, da backend, hanno i settings hardcoded a true per ora)
    val showSettings = isMyProfile && userRole != "MANAGER"

    // Mostriamo i tasti azione (Logout/Delete) solo se è il mio profilo
    val showActions = isMyProfile

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    // Fallback per navigation bar: se idUtente è null, usa quello di sessione
    val navId = idUtente ?: sessionUserId ?: "utente"

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = if (userRole == "MANAGER") "Profilo Manager" else "Profilo Utente",
                showAppIcon = true,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            AppBottomNavigation(
                navController = navController,
                idUtente = navId,
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
                is ProfileUiState.Loading, is ProfileUiState.UserDeleted -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorScheme.primary)
                    }
                }
                is ProfileUiState.Error -> {
                    ErrorContent(uiState.message, onRetry, onLogout, colorScheme, typography, dimensions)
                }
                is ProfileUiState.Success -> {
                    val data = uiState.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {})
                            .verticalScroll(rememberScrollState())
                    ) {
                        ProfileInnerContent(
                            profileData = data,
                            onLogoutClick = { showLogoutDialog = true },
                            onDeleteClick = { showDeleteDialog = true },
                            onUpdateNotification = onUpdateNotification,
                            showSettings = showSettings,
                            showActions = showActions,
                            typography = typography,
                            colorScheme = colorScheme,
                            dimensions = dimensions
                        )
                    }
                }
            }
        }

        if (showLogoutDialog) {
            LogoutConfirmAlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                onLogoutConfirm = { _ -> showLogoutDialog = false; onLogout() },
                isEditMode = false, hasUnsavedChanges = false, canSaveChanges = false,
                colorScheme = colorScheme, dimensions = dimensions, typography = typography
            )
        }

        if (showDeleteDialog) {
            DeleteConfirmAlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                onConfirmDelete = { showDeleteDialog = false; onDelete() },
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onLogout: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimensions.paddingLarge)
        ) {
            Icon(Icons.Default.Warning, null, tint = colorScheme.error, modifier = Modifier.height(48.dp).width(48.dp))
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            Text("Ops! Qualcosa non va.", style = typography.titleMedium, color = colorScheme.onSurface)

            val displayError = if (message.contains("404")) "Utente non trovato.\nEffettua il logout." else message
            Text(displayError, color = colorScheme.error, style = typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = dimensions.paddingSmall))
            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

            if (!message.contains("404")) {
                AppPrimaryButton(onClick = onRetry, text = "Riprova", modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            }
            AppRedButton(onClick = onLogout, text = "Logout", modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ProfileInnerContent(
    profileData: ProfileData,
    onLogoutClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onUpdateNotification: (Boolean?, Boolean?, Boolean?) -> Unit,
    showSettings: Boolean,
    showActions: Boolean,
    typography: Typography,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium)
            .padding(top = dimensions.paddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- SEZIONE DATI PERSONALI ---
        Text(
            text = "Dati personali",
            style = typography.titleMedium,
            modifier = Modifier
                .padding(vertical = dimensions.paddingMedium)
                .align(Alignment.Start)
        )

        ProfileReadOnlyFields(profileData, colorScheme, typography, dimensions)

        // --- SEZIONE NOTIFICHE (Solo se è il mio profilo E non sono manager) ---
        if (showSettings) {
            Text(
                text = "Impostazioni Notifiche",
                style = typography.titleMedium,
                modifier = Modifier
                    .padding(top = dimensions.spacingLarge, bottom = dimensions.paddingMedium)
                    .align(Alignment.Start)
            )

            NotificationSettingsSection(
                profileData = profileData,
                onUpdate = onUpdateNotification,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spacingLarge))

        // --- PULSANTI AZIONE (Solo se è il mio profilo) ---
        if (showActions) {
            ProfileActionButtons(onLogoutClick, onDeleteClick, dimensions)
            Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        }
    }
}

@Composable
private fun NotificationSettingsSection(
    profileData: ProfileData,
    onUpdate: (Boolean?, Boolean?, Boolean?) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
            .padding(dimensions.paddingMedium)
    ) {
        NotificationSwitchItem(
            label = "Aggiornamenti Trattative",
            description = "Ricevi notifiche quando ricevi offerte o risposte.",
            checked = profileData.notifTrattative,
            onCheckedChange = { onUpdate(it, null, null) },
            colorScheme = colorScheme, typography = typography
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant)

        NotificationSwitchItem(
            label = "Esito Pubblicazione",
            description = "Notifiche quando un'agenzia accetta o rifiuta il tuo immobile.",
            checked = profileData.notifPubblicazione,
            onCheckedChange = { onUpdate(null, it, null) },
            colorScheme = colorScheme, typography = typography
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant)

        NotificationSwitchItem(
            label = "Nuovi Immobili in Zona",
            description = "Ricevi avvisi quando vengono pubblicati immobili nelle zone che hai cercato.",
            checked = profileData.notifNuoviImmobili,
            onCheckedChange = { onUpdate(null, null, it) },
            colorScheme = colorScheme, typography = typography
        )
    }
}

@Composable
private fun NotificationSwitchItem(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = typography.bodyLarge, color = colorScheme.onSurface)
            Text(text = description, style = typography.bodySmall, color = colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colorScheme.primary,
                checkedTrackColor = colorScheme.primaryContainer,
                uncheckedThumbColor = colorScheme.outline,
                uncheckedTrackColor = colorScheme.surface
            )
        )
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
            .background(colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
            .padding(dimensions.paddingMedium)
    ) {
        ProfileFieldItem("Nome", profileData.name, colorScheme, typography, dimensions)
        HorizontalDivider(color = colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        ProfileFieldItem("Email", profileData.email, colorScheme, typography, dimensions)
        HorizontalDivider(color = colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        val phoneDisplay = if (profileData.phoneNumberWithoutPrefix.isNotBlank())
            "${profileData.selectedPrefix.prefix} ${profileData.phoneNumberWithoutPrefix}" else "Non specificato"
        ProfileFieldItem("Telefono", phoneDisplay, colorScheme, typography, dimensions)
    }
}

@Composable
private fun ProfileFieldItem(label: String, value: String, colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions) {
    Text(text = label, style = typography.labelMedium, color = colorScheme.primary)
    Text(text = value, style = typography.bodyLarge, color = colorScheme.onSurface, modifier = Modifier.padding(top = dimensions.paddingExtraSmall, bottom = dimensions.paddingMedium))
}

@Composable
private fun ProfileActionButtons(onLogoutClick: () -> Unit, onDeleteProfileClick: () -> Unit, dimensions: Dimensions) {
    AppPrimaryButton(onClick = onLogoutClick, text = "Esci Dal Profilo", modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
    AppRedButton(onClick = onDeleteProfileClick, text = "Elimina Profilo", modifier = Modifier.fillMaxWidth())
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun ProfileScreenPreviewLight() {
    val mockData = ProfileData("Mario Rossi", "mario@email.it", com.dieti.dietiestates25.data.model.modelsource.PhonePrefix("+39", "", ""), "3331234567")
    DietiEstatesTheme {
        ProfileScreenContent(rememberNavController(), "id", ProfileUiState.Success(mockData), {}, {}, {}, { _, _, _ -> })
    }
}