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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.dieti.dietiestates25.ui.utils.SessionManager
import com.dieti.dietiestates25.ui.features.auth.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    idUtente: String?, // NUOVO PARAMETRO: Riceviamo l'ID da fuori
    viewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    Log.d("PROFILE_UI_ENTRY", ">>> COMPOSABLE PROFILE SCREEN AVVIATO <<<")
    Log.d("PROFILE_UI_ENTRY", "ID ricevuto come parametro: $idUtente")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // RIMOSSO: Non estraiamo più l'ID dal backstack qui, usiamo il parametro idUtente

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(idUtente) { // Reagisce al parametro
        Log.d("PROFILE_UI_ENTRY", "Lancio loadUserProfile con ID: $idUtente")
        viewModel.loadUserProfile(context, idUtente)
    }

    val onLogout = {
        authViewModel.logout()
        Toast.makeText(context, "Logout effettuato", Toast.LENGTH_SHORT).show()
        navController.navigate(Screen.LoginScreen.route) {
            popUpTo(0) { inclusive = true }
        }
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

    val onRetry = { viewModel.loadUserProfile(context, idUtente) }

    ProfileScreenContent(
        navController = navController,
        idUtente = idUtente, // Passiamo l'ID giù alla UI
        uiState = uiState,
        onLogout = { onLogout() },
        onDelete = onDelete,
        onRetry = onRetry
    )
}

@Composable
fun ProfileScreenContent(
    navController: NavController,
    idUtente: String?, // Parametro esplicito anche qui
    uiState: ProfileUiState,
    onLogout: () -> Unit,
    onDelete: () -> Unit,
    onRetry: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val focusManager = LocalFocusManager.current
    val dimensions = Dimensions

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    // Logica di fallback per la BottomBar: se l'ID è null, proviamo SessionManager, altrimenti "utente"
    val currentUserId = idUtente ?: SessionManager.getUserId(context) ?: "utente"

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
                idUtente = currentUserId,
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(dimensions.paddingLarge)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = colorScheme.error,
                                modifier = Modifier.height(48.dp).width(48.dp)
                            )
                            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                            Text(
                                text = "Ops! Qualcosa non va.",
                                style = typography.titleMedium,
                                color = colorScheme.onSurface
                            )

                            val displayError = if (uiState.message.contains("404")) {
                                "Utente non trovato sul server.\nL'ID salvato non è più valido.\nEffettua il logout per aggiornare."
                            } else {
                                uiState.message
                            }

                            Text(
                                text = displayError,
                                color = colorScheme.error,
                                style = typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = dimensions.paddingSmall)
                            )
                            Spacer(modifier = Modifier.height(dimensions.spacingLarge))

                            if (!uiState.message.contains("404")) {
                                AppPrimaryButton(
                                    onClick = onRetry,
                                    text = "Riprova",
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                            }

                            AppRedButton(
                                onClick = onLogout,
                                text = "Logout e cambia utente",
                                modifier = Modifier.fillMaxWidth()
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
            .background(colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium)
            .padding(dimensions.paddingMedium)
    ) {
        // Label Nome
        Text(
            text = "Nome",
            style = typography.labelMedium,
            color = colorScheme.primary
        )
        Text(
            text = profileData.name,
            style = typography.bodyLarge,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall, bottom = dimensions.paddingMedium)
        )

        HorizontalDivider(color = colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Label Email
        Text(
            text = "Email",
            style = typography.labelMedium,
            color = colorScheme.primary
        )
        Text(
            text = profileData.email,
            style = typography.bodyLarge,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall, bottom = dimensions.paddingMedium)
        )

        HorizontalDivider(color = colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(dimensions.paddingMedium))

        // Label Telefono
        Text(
            text = "Telefono",
            style = typography.labelMedium,
            color = colorScheme.primary
        )
        val phoneDisplay = if (profileData.phoneNumberWithoutPrefix.isNotBlank()) {
            "${profileData.selectedPrefix.prefix} ${profileData.phoneNumberWithoutPrefix}"
        } else {
            "Non specificato"
        }

        Text(
            text = phoneDisplay,
            style = typography.bodyLarge,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall)
        )
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

@Preview(name = "Light Mode", showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun ProfileScreenPreviewLight() {
    val navController = rememberNavController()
    val mockData = ProfileData(name = "Mario Rossi", email = "mario.rossi@unina.it", phoneNumberWithoutPrefix = "3339998877")
    DietiEstatesTheme {
        // Passiamo null come idUtente per la preview, o un ID finto
        ProfileScreenContent(navController = navController, idUtente = "mock-id", uiState = ProfileUiState.Success(mockData), onLogout = {}, onDelete = {}, onRetry = {})
    }
}

@Preview(name = "Dark Mode", showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreviewDark() {
    val navController = rememberNavController()
    val mockData = ProfileData(name = "Mario Rossi", email = "mario.rossi@unina.it", phoneNumberWithoutPrefix = "3339998877")
    DietiEstatesTheme(darkTheme = true) {
        ProfileScreenContent(navController = navController, idUtente = "mock-id", uiState = ProfileUiState.Success(mockData), onLogout = {}, onDelete = {}, onRetry = {})
    }
}