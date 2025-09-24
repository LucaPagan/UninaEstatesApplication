package com.dieti.dietiestates25.ui.screen

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.DeleteConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.LogoutConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.UnsavedChangesAlertDialog
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.model.ProfileData
import com.dieti.dietiestates25.ui.model.ProfileViewModel
import com.dieti.dietiestates25.ui.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val focusManager = LocalFocusManager.current
    val dimensions = Dimensions

    val profileData by viewModel.currentProfileData.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()
    val canSaveChanges by viewModel.canSaveChanges.collectAsState()

    val showExitEditModeDialog by viewModel.showExitEditModeConfirmDialog.collectAsState()
    val showLogoutDialog by viewModel.showLogoutConfirmDialog.collectAsState()
    val showDeleteDialog by viewModel.showDeleteConfirmDialog.collectAsState()

    // Calcolo del titolo dinamico
    val baseTitle = if (isEditMode) "Modifica Profilo" else "Profilo Utente"
    val screenTitle = if (isEditMode && hasUnsavedChanges) "$baseTitle*" else baseTitle

    // Configurazione dell'action button
    val actionIcon = if (isEditMode) Icons.Filled.Close else Icons.Filled.Edit
    val actionContentDescription = if (isEditMode) "Annulla Modifiche" else "Modifica Dati"
    val actionBackgroundColor = if (isEditMode) colorScheme.errorContainer else colorScheme.primaryContainer
    val actionIconTint = if (isEditMode) colorScheme.onErrorContainer else colorScheme.onPrimaryContainer

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = screenTitle,
                actionIcon = actionIcon,
                actionContentDescription = actionContentDescription,
                onActionClick = viewModel::attemptToggleEditMode,
                actionBackgroundColor = actionBackgroundColor,
                actionIconTint = actionIconTint,
                showAppIcon = true,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            // --- MODIFICA PER GESTIRE LA NAVIGAZIONE CON MODIFICHE NON SALVATE ---
            // Nota: questo richiede di modificare il tuo componente AppBottomNavigation
            // per accettare una nuova lambda `onNavigateAttempt` che restituisce un Boolean.
            AppBottomNavigation(
                navController = navController,
                idUtente = profileData.email,
                onNavigateAttempt = {
                    // Controlla se la navigazione deve essere bloccata per mostrare il dialogo
                    if (isEditMode && hasUnsavedChanges) {
                        viewModel.triggerExitEditModeDialog() // Mostra il dialogo "Modifiche non salvate"
                        false // Blocca la navigazione
                    } else {
                        true // Permetti la navigazione
                    }
                }
            )
        }
    ) { scaffoldPaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddingValues)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { focusManager.clearFocus() }
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileContent(
                    profileData = profileData,
                    isEditMode = isEditMode, // Passa lo stato di modifica
                    canSaveChanges = canSaveChanges,
                    availablePhonePrefixes = viewModel.availablePhonePrefixes,
                    onNameChange = viewModel::onNameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPhonePrefixChange = viewModel::onPhonePrefixChange,
                    onPhoneNumberWithoutPrefixChange = viewModel::onPhoneNumberWithoutPrefixChange,
                    onSaveChanges = viewModel::saveChanges,
                    onLogout = viewModel::triggerLogoutDialog,
                    onDeleteProfile = viewModel::triggerDeleteProfileDialog,
                    typography = typography,
                    colorScheme = colorScheme,
                    navController = navController,
                    dimensions = dimensions
                )
            }
        }

        if (showExitEditModeDialog) {
            UnsavedChangesAlertDialog(
                onDismissRequest = viewModel::closeExitEditModeConfirmDialog,
                onSave = {
                    if (canSaveChanges) viewModel.confirmExitEditModeAndSave()
                },
                onDontSave = viewModel::confirmExitEditModeWithoutSaving,
                canSave = canSaveChanges,
                colorScheme = colorScheme
            )
        }

        if (showLogoutDialog) {
            LogoutConfirmAlertDialog(
                onDismissRequest = viewModel::cancelLogoutDialog,
                onLogoutConfirm = { save -> viewModel.confirmLogout(save) },
                isEditMode = isEditMode,
                hasUnsavedChanges = hasUnsavedChanges,
                canSaveChanges = canSaveChanges,
                colorScheme = colorScheme,
                dimensions = dimensions,
                typography = typography
            )
        }

        if (showDeleteDialog) {
            DeleteConfirmAlertDialog(
                onDismissRequest = viewModel::cancelDeleteProfileDialog,
                onConfirmDelete = {
                    viewModel.deleteProfile()
                },
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun ProfileContent(
    profileData: ProfileData,
    isEditMode: Boolean,
    canSaveChanges: Boolean,
    availablePhonePrefixes: List<PhonePrefix>,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhonePrefixChange: (PhonePrefix) -> Unit,
    onPhoneNumberWithoutPrefixChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onLogout: () -> Unit,
    onDeleteProfile: () -> Unit,
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
        ProfileDataFields(
            profileData = profileData,
            isEditMode = isEditMode,
            availablePhonePrefixes = availablePhonePrefixes,
            onNameChange = onNameChange,
            onEmailChange = onEmailChange,
            onPhonePrefixChange = onPhonePrefixChange,
            onPhoneNumberWithoutPrefixChange = onPhoneNumberWithoutPrefixChange,
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        ProfileOtherOptions(
            isEditMode = isEditMode, // Passa lo stato di modifica
            typography = typography,
            colorScheme = colorScheme,
            navController = navController,
            dimensions = dimensions
        )
        Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
        ProfileActionButtons(
            isEditMode = isEditMode,
            canSaveChanges = canSaveChanges,
            onSaveClick = onSaveChanges,
            onLogoutClick = onLogout,
            onDeleteProfileClick = onDeleteProfile,
            dimensions = dimensions
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDataFields(
    profileData: ProfileData,
    isEditMode: Boolean,
    availablePhonePrefixes: List<PhonePrefix>,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhonePrefixChange: (PhonePrefix) -> Unit,
    onPhoneNumberWithoutPrefixChange: (String) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    var prefixDropdownExpanded by remember { mutableStateOf(false) }

    val commonTextFieldModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = dimensions.paddingMedium)

    val nameIsError = isEditMode && profileData.name.isBlank()
    val emailIsError = isEditMode && profileData.email.isBlank()
    val phoneIsError = isEditMode && profileData.phoneNumberWithoutPrefix.isBlank()

    val textFieldErrorColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.onSurfaceVariant,
        cursorColor = colorScheme.primary,

        disabledTextColor = colorScheme.onSurface.copy(alpha = 0.7f),
        disabledBorderColor = colorScheme.outline.copy(alpha = 0.5f),
        disabledLabelColor = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),

        errorBorderColor = colorScheme.error,
        errorLabelColor = colorScheme.error,
        errorCursorColor = colorScheme.error,
        errorSupportingTextColor = colorScheme.error
    )

    val prefixTextFieldColors = OutlinedTextFieldDefaults.colors(
        disabledTextColor = colorScheme.onSurface.copy(alpha = 0.7f),
        disabledBorderColor = colorScheme.outline.copy(alpha = 0.5f),
        disabledLabelColor = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.onSurfaceVariant,
    )


    OutlinedTextField(
        value = profileData.name,
        onValueChange = onNameChange,
        label = { Text("Nome Utente") },
        enabled = isEditMode,
        modifier = commonTextFieldModifier,
        singleLine = true,
        isError = nameIsError,
        colors = textFieldErrorColors,
        textStyle = typography.bodyLarge
    )

    OutlinedTextField(
        value = profileData.email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        enabled = isEditMode,
        modifier = commonTextFieldModifier,
        singleLine = true,
        isError = emailIsError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        colors = textFieldErrorColors,
        textStyle = typography.bodyLarge
    )

    Row(verticalAlignment = Alignment.Top) {
        ExposedDropdownMenuBox(
            expanded = prefixDropdownExpanded && isEditMode, // Il menu si apre solo in edit mode
            onExpandedChange = { if (isEditMode) prefixDropdownExpanded = !prefixDropdownExpanded },
            modifier = Modifier.padding(end = dimensions.spacingSmall)
        ) {
            OutlinedTextField(
                value = "${profileData.selectedPrefix.flagEmoji} ${profileData.selectedPrefix.prefix}",
                onValueChange = {},
                readOnly = true,
                label = { Text("Pref.") },
                enabled = isEditMode,
                modifier = Modifier
                    .menuAnchor()
                    .width(dimensions.prefixWidth),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = prefixDropdownExpanded && isEditMode) },
                colors = prefixTextFieldColors,
                textStyle = typography.bodyLarge
            )
            ExposedDropdownMenu(
                expanded = prefixDropdownExpanded && isEditMode,
                onDismissRequest = { prefixDropdownExpanded = false }
            ) {
                availablePhonePrefixes.forEach { prefix ->
                    DropdownMenuItem(
                        text = { Text("${prefix.flagEmoji} ${prefix.displayName}") },
                        onClick = {
                            onPhonePrefixChange(prefix)
                            prefixDropdownExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = profileData.phoneNumberWithoutPrefix,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                    onPhoneNumberWithoutPrefixChange(newValue)
                }
            },
            label = { Text("Numero") },
            enabled = isEditMode,
            modifier = Modifier.weight(1f),
            singleLine = true,
            isError = phoneIsError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = textFieldErrorColors,
            textStyle = typography.bodyLarge
        )
    }
}

@Composable
private fun ProfileOtherOptions(
    isEditMode: Boolean, // Aggiunto per disabilitare i pulsanti
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
            icon = Icons.Default.NightsStay, // Cambia con un'icona appropriata
            onClick = { navController.navigate(Screen.YourPropertyScreen.route) },
            enabled = !isEditMode, // Disabilitato se in edit mode
            dimensions = dimensions,
            colorScheme = colorScheme
        )
        ProfileOptionRow(
            text = "Immobili salvati",
            icon = Icons.Default.NightsStay, // Cambia con un'icona appropriata
            onClick = { navController.navigate(
                Screen.ApartmentListingScreen.buildRoute(
                    idUtentePath = "",
                    comunePath = "",
                    ricercaPath = ""
                )
            ) },
            enabled = !isEditMode, // Disabilitato se in edit mode
            dimensions = dimensions,
            colorScheme = colorScheme
        )
        ProfileOptionRow(
            text = "Richieste appuntamenti",
            icon = Icons.Default.NightsStay, // Cambia con un'icona appropriata
            onClick = { navController.navigate(
                Screen.RequestsScreen.withIdUtente("")
            ) },
            enabled = !isEditMode, // Disabilitato se in edit mode
            dimensions = dimensions,
            colorScheme = colorScheme
        )
    }
}

@Composable
fun ProfileOptionRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean, // Nuovo parametro per l'abilitazione
    dimensions: Dimensions,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography
) {
    val contentColor = if (enabled) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.38f)
    val iconColor = if (enabled) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.38f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick) // Applica lo stato enabled
            .padding(vertical = dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor)
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Text(text, style = typography.bodyLarge, modifier = Modifier.weight(1f), color = contentColor)
        Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Vai a $text", tint = contentColor)
    }
}

@Composable
private fun ProfileActionButtons(
    isEditMode: Boolean,
    canSaveChanges: Boolean,
    onSaveClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteProfileClick: () -> Unit,
    dimensions: Dimensions
) {
    if (isEditMode) {
        AppPrimaryButton(
            onClick = onSaveClick,
            text = "Salva Modifiche",
            enabled = canSaveChanges,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(dimensions.spacingMedium))
    }
    AppPrimaryButton(
        onClick = onLogoutClick,
        text = "Esci Dal Profilo",
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(dimensions.spacingMedium))
    AppRedButton(
        onClick = onDeleteProfileClick,
        text = "Elimina Profilo",
        enabled = !isEditMode,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        ProfileScreen(navController = navController)
    }
}

