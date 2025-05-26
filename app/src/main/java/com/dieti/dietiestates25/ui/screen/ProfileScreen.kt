package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.model.ProfileData
import com.dieti.dietiestates25.ui.model.ProfileViewModel
import com.dieti.dietiestates25.ui.components.UnsavedChangesAlertDialog
import com.dieti.dietiestates25.ui.components.LogoutConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.DeleteConfirmAlertDialog
import com.dieti.dietiestates25.ui.model.modelsource.PhonePrefix

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

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

    Scaffold(
        topBar = {
            ProfileScreenHeader(
                isEditMode = isEditMode,
                hasUnsavedChanges = hasUnsavedChanges,
                onToggleEditMode = viewModel::attemptToggleEditMode,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController, idUtente = profileData.email)
        }
    ) { scaffoldPaddingValues ->
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
                isEditMode = isEditMode,
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
            dimensions = dimensions
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



@Composable
private fun ProfileScreenHeader(
    isEditMode: Boolean,
    hasUnsavedChanges: Boolean,
    onToggleEditMode: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(colorScheme.primary)
            .clip(
                RoundedCornerShape(
                    bottomStart = dimensions.cornerRadiusLarge,
                    bottomEnd = dimensions.cornerRadiusLarge
                )
            )
            .padding(horizontal = dimensions.paddingLarge)
            .padding(
                top = 25.dp,
                bottom = dimensions.paddingLarge
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AppIconDisplay(
                    size = 60.dp,
                    shapeRadius = dimensions.cornerRadiusMedium
                )
                Spacer(modifier = Modifier.width(dimensions.spacingMedium))
                val baseTitle = if (isEditMode) "Modifica Profilo" else "Profilo Utente"
                val screenTitle = if (isEditMode && hasUnsavedChanges) "$baseTitle*" else baseTitle
                Text(
                    text = screenTitle,
                    style = typography.titleLarge,
                    color = colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            val currentIconVector = if (isEditMode) Icons.Filled.Close else Icons.Filled.Edit
            val currentContentDescription = if (isEditMode) "Annulla Modifiche" else "Modifica Dati"
            val currentBackgroundColor = if (isEditMode) colorScheme.errorContainer else colorScheme.primaryContainer
            val currentIconTint = if (isEditMode) colorScheme.onErrorContainer else colorScheme.onPrimaryContainer
            CircularIconActionButton(
                onClick = onToggleEditMode,
                iconVector = currentIconVector,
                contentDescription = currentContentDescription,
                buttonSize = 40.dp, // Valore specifico
                backgroundColor = currentBackgroundColor,
                iconTint = currentIconTint,
                iconSize = dimensions.iconSizeMedium
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
        .padding(bottom = 12.dp)

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
            expanded = prefixDropdownExpanded,
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
                    .width(140.dp),
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
            text = "Controlla immobili salvati",
            icon = Icons.Default.NightsStay,
            onClick = { /* Naviga */ },
            dimensions = dimensions
        )
        ProfileOptionRow(
            text = "Controlla richieste agenzia",
            icon = Icons.Default.NightsStay,
            onClick = { /* Naviga */ },
            dimensions = dimensions
        )
        ProfileOptionRow(
            text = "Impostazioni Notifiche",
            icon = Icons.Default.NightsStay,
            onClick = { /* Naviga */ },
            dimensions = dimensions
        )
    }
}

@Composable
fun ProfileOptionRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    dimensions: Dimensions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Vai a $text", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfileActionButtons(
    isEditMode: Boolean,
    canSaveChanges: Boolean,
    onSaveClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteProfileClick: () -> Unit, // Questa ora chiamerà triggerDeleteProfileDialog
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
        onClick = onDeleteProfileClick, // Questa azione ora triggera il dialogo
        text = "Elimina Profilo",
        enabled = !isEditMode,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}