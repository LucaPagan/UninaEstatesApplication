package com.dieti.dietiestates25.ui.features.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.ProfileData
import com.dieti.dietiestates25.ui.components.*
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.SessionManager
import com.dieti.dietiestates25.ui.features.auth.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    idUtente: String?,
    onLogout: () -> Unit, // Callback per gestire l'uscita (come in AdminDashboard)
    viewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()

    val performFullLogout = {
        viewModel.logout(context) // Pulisce SessionManager (Token, ID, etc)
        authViewModel.logout()    // Pulisce lo stato del ViewModel Auth
        onLogout()         // Chiama il callback di navigazione definito in Navigation.kt
    }

    // --- STATO PERMESSI NOTIFICHE (Sistema Android) ---
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    // Launcher per richiedere il permesso
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (isGranted) {
                Toast.makeText(context, "Notifiche attivate!", Toast.LENGTH_SHORT).show()
                // Ricarichiamo il profilo per sincronizzare il token se necessario
                viewModel.loadUserProfile(context, idUtente)
            } else {
                Toast.makeText(
                    context,
                    "Notifiche disabilitate. Potrai attivarle dalle impostazioni.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    // Funzione per aprire le impostazioni di sistema se il permesso è stato negato permanentemente
    val openAppSettings = {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    // Observer per aggiornare lo stato del permesso quando l'app torna in primo piano (onResume)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    hasNotificationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Caricamento dati iniziale
    LaunchedEffect(idUtente) {
        viewModel.loadUserProfile(context, idUtente)
    }

    val onUpdateNotification: (Boolean?, Boolean?, Boolean?) -> Unit = { t, p, n ->
        viewModel.updateNotificationPreference(t, p, n)
    }

    ProfileScreenContent(
        navController = navController,
        idUtente = idUtente,
        uiState = uiState,
        onLogout = performFullLogout,
        onDelete = { viewModel.deleteProfile(context) },
        onRetry = { viewModel.loadUserProfile(context, idUtente) },
        onUpdateNotification = onUpdateNotification,
        hasNotificationPermission = hasNotificationPermission,
        onRequestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        },
        onOpenSettings = openAppSettings
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
    onUpdateNotification: (Boolean?, Boolean?, Boolean?) -> Unit,
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val snackbarHostState = remember { SnackbarHostState() }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val sessionUserId = remember { SessionManager.getUserId(context) }
    val userRole = remember { SessionManager.getUserRole(context) }

    // MODIFICA CRITICA: Aggiunto controllo per keyword "session" e "utente"
    // Questo risolve il bug della scomparsa dei settings quando si naviga tramite NavBar
    val isMyProfile by remember(idUtente, sessionUserId) {
        derivedStateOf {
            idUtente == null ||
                    idUtente == "session" ||
                    idUtente == "utente" ||
                    idUtente == "{idUtente}" || // Gestione parametri di default Navigation
                    idUtente == sessionUserId
        }
    }

    // Mostra settings se è il mio profilo e NON sono manager
    val showSettings = isMyProfile && userRole != "MANAGER"
    val showActions = isMyProfile

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

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
                    ErrorContent(
                        uiState.message,
                        onRetry,
                        onLogout,
                        colorScheme,
                        typography,
                        dimensions
                    )
                }

                is ProfileUiState.Success -> {
                    val data = uiState.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {})
                            .verticalScroll(rememberScrollState())
                    ) {
                        ProfileInnerContent(
                            profileData = data,
                            onLogoutClick = { showLogoutDialog = true },
                            onDeleteClick = { showDeleteDialog = true },
                            onUpdateNotification = onUpdateNotification,
                            showSettings = showSettings,
                            showActions = showActions,
                            hasNotificationPermission = hasNotificationPermission,
                            onRequestPermission = onRequestPermission,
                            onOpenSettings = onOpenSettings,
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
                onLogoutConfirm = { showLogoutDialog = false; onLogout() } as () -> Unit,
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
    dimensions: Dimensions,
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(dimensions.paddingLarge)
        ) {
            Icon(
                Icons.Default.Warning,
                null,
                tint = colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            Text(
                "Ops! Qualcosa non va.",
                style = typography.titleMedium,
                color = colorScheme.onSurface
            )

            val displayError =
                if (message.contains("404")) "Utente non trovato.\nEffettua il logout." else message
            Text(
                displayError,
                color = colorScheme.error,
                style = typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = dimensions.paddingSmall)
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

            if (!message.contains("404")) {
                AppPrimaryButton(
                    onClick = onRetry,
                    text = "Riprova",
                    modifier = Modifier.fillMaxWidth()
                )
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
    hasNotificationPermission: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    typography: Typography,
    colorScheme: ColorScheme,
    dimensions: Dimensions,
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

        // --- SEZIONE NOTIFICHE ---
        if (showSettings) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensions.spacingLarge, bottom = dimensions.paddingMedium)
            ) {
                Icon(
                    if (hasNotificationPermission) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = if (hasNotificationPermission) colorScheme.primary else colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Impostazioni Notifiche",
                    style = typography.titleMedium
                )
            }

            // --- BOX PERMESSI DI SISTEMA ---
            // Se l'utente non ha i permessi di sistema, mostriamo un box per abilitarli
            if (!hasNotificationPermission) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensions.paddingMedium)
                        .clickable { onRequestPermission() } // Cliccando si tenta di chiedere il permesso
                ) {
                    Column(modifier = Modifier.padding(dimensions.paddingMedium)) {
                        Text(
                            text = "Notifiche disabilitate dal sistema",
                            style = typography.labelLarge,
                            color = colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Non riceverai notifiche push. Tocca qui per abilitarle o vai nelle impostazioni.",
                            style = typography.bodySmall,
                            color = colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = onRequestPermission,
                                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                            ) {
                                Text("Abilita Ora", color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = onOpenSettings,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error)
                            ) {
                                Text("Impostazioni")
                            }
                        }
                    }
                }
            }

            NotificationSettingsSection(
                profileData = profileData,
                onUpdate = onUpdateNotification,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions,
                isEnabled = hasNotificationPermission // Opzionale: puoi disabilitare gli switch se non c'è permesso, o lasciarli attivi per le preferenze DB
            )
        }

        Spacer(modifier = Modifier.height(dimensions.spacingLarge))

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
    dimensions: Dimensions,
    isEnabled: Boolean, // Passiamo lo stato dei permessi
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(dimensions.paddingMedium)
    ) {
        NotificationSwitchItem(
            label = "Aggiornamenti Trattative",
            description = "Ricevi notifiche quando ricevi offerte o risposte.",
            checked = profileData.notifTrattative,
            onCheckedChange = { onUpdate(it, null, null) },
            colorScheme = colorScheme, typography = typography
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = colorScheme.outlineVariant
        )

        NotificationSwitchItem(
            label = "Esito Pubblicazione",
            description = "Notifiche quando un'agenzia accetta o rifiuta il tuo immobile.",
            checked = profileData.notifPubblicazione,
            onCheckedChange = { onUpdate(null, it, null) },
            colorScheme = colorScheme, typography = typography
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = colorScheme.outlineVariant
        )

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
    typography: Typography,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = typography.bodyLarge, color = colorScheme.onSurface)
            Text(
                text = description,
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant
            )
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
    dimensions: Dimensions,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
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
private fun ProfileFieldItem(
    label: String,
    value: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
) {
    Text(text = label, style = typography.labelMedium, color = colorScheme.primary)
    Text(
        text = value,
        style = typography.bodyLarge,
        color = colorScheme.onSurface,
        modifier = Modifier.padding(
            top = dimensions.paddingExtraSmall,
            bottom = dimensions.paddingMedium
        )
    )
}

@Composable
private fun ProfileActionButtons(
    onLogoutClick: () -> Unit,
    onDeleteProfileClick: () -> Unit,
    dimensions: Dimensions,
) {
    AppPrimaryButton(
        onClick = onLogoutClick,
        text = "Esci Dal Profilo",
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
    AppRedButton(
        onClick = onDeleteProfileClick,
        text = "Elimina Profilo",
        modifier = Modifier.fillMaxWidth()
    )
}