package com.dieti.dietiestates25.ui.features.profile

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.DeleteConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.LogoutConfirmAlertDialog
import com.dieti.dietiestates25.ui.components.UnsavedChangesAlertDialog
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- DATI MOCK LOCALI (Sostituiscono Model e ViewModel) ---
data class PhonePrefix(
    val prefix: String,
    val flagEmoji: String,
    val displayName: String
)

data class ProfileData(
    val email: String,
    val selectedPrefix: PhonePrefix,
    val phoneNumberWithoutPrefix: String
)

@Composable
fun ProfileScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val focusManager = LocalFocusManager.current
    val dimensions = Dimensions
    val scope = rememberCoroutineScope()

    // --- GESTIONE STATO LOCALE ---
    var isLoading by remember { mutableStateOf(true) }
    var isEditMode by remember { mutableStateOf(false) }

    // Dialogs
    var showExitEditModeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Dati Mock
    val availablePhonePrefixes = remember {
        listOf(
            PhonePrefix("+39", "🇮🇹", "Italia"),
            PhonePrefix("+1", "🇺🇸", "USA"),
            PhonePrefix("+44", "🇬🇧", "UK"),
            PhonePrefix("+33", "🇫🇷", "Francia"),
            PhonePrefix("+49", "🇩🇪", "Germania")
        )
    }

    // Usiamo due stati: uno per i dati salvati (originali) e uno per quelli in modifica (correnti)
    var originalProfileData by remember { mutableStateOf<ProfileData?>(null) }
    var currentProfileData by remember { mutableStateOf<ProfileData?>(null) }

    // --- CARICAMENTO DATI SIMULATO ---
    LaunchedEffect(Unit) {
        isLoading = true
        delay(1000) // Simula loading da DB/API
        val data = ProfileData(
            email = "mario.rossi@studenti.unina.it",
            selectedPrefix = availablePhonePrefixes.first(),
            phoneNumberWithoutPrefix = "3331234567"
        )
        originalProfileData = data
        currentProfileData = data
        isLoading = false
    }

    // --- LOGICA COMPUTATA ---
    val hasUnsavedChanges = remember(originalProfileData, currentProfileData) {
        originalProfileData != currentProfileData
    }

    val canSaveChanges = remember(currentProfileData) {
        currentProfileData?.let {
            it.email.isNotBlank() && it.phoneNumberWithoutPrefix.isNotBlank()
        } ?: false
    }

    // --- AZIONI ---

    fun handleSave() {
        scope.launch {
            isLoading = true
            delay(1000) // Simula salvataggio
            originalProfileData = currentProfileData
            isEditMode = false
            isLoading = false
            Toast.makeText(context, "Profilo aggiornato con successo", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleToggleEditMode() {
        if (isEditMode) {
            if (hasUnsavedChanges) {
                showExitEditModeDialog = true
            } else {
                isEditMode = false
            }
        } else {
            isEditMode = true
        }
    }

    fun handleLogout(save: Boolean) {
        scope.launch {
            if (save && hasUnsavedChanges && canSaveChanges) {
                // Simula salvataggio prima del logout
                delay(500)
            }
            Toast.makeText(context, "Logout effettuato", Toast.LENGTH_SHORT).show()
            showLogoutDialog = false
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    fun handleDelete() {
        scope.launch {
            delay(1000) // Simula eliminazione
            Toast.makeText(context, "Profilo eliminato", Toast.LENGTH_SHORT).show()
            showDeleteDialog = false
            navController.navigate(Screen.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Titolo dinamico
    val baseTitle = if (isEditMode) "Modifica Profilo" else "Profilo Utente"
    val screenTitle = if (isEditMode && hasUnsavedChanges) "$baseTitle*" else baseTitle

    // Icona Action
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
                onActionClick = { handleToggleEditMode() },
                actionBackgroundColor = actionBackgroundColor,
                actionIconTint = actionIconTint,
                showAppIcon = true,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            AppBottomNavigation(
                navController = navController,
                idUtente = currentProfileData?.email ?: "",
                onNavigateAttempt = {
                    if (isEditMode && hasUnsavedChanges) {
                        showExitEditModeDialog = true
                        false
                    } else {
                        true
                    }
                }
            )
        }
    ) { scaffoldPaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(scaffoldPaddingValues)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                currentProfileData?.let { data ->
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
                        ProfileContent(
                            profileData = data,
                            isEditMode = isEditMode,
                            canSaveChanges = canSaveChanges,
                            availablePhonePrefixes = availablePhonePrefixes,
                            onEmailChange = { currentProfileData = currentProfileData?.copy(email = it) },
                            onPhonePrefixChange = { currentProfileData = currentProfileData?.copy(selectedPrefix = it) },
                            onPhoneNumberWithoutPrefixChange = { currentProfileData = currentProfileData?.copy(phoneNumberWithoutPrefix = it) },
                            onSaveChanges = { handleSave() },
                            onLogout = { showLogoutDialog = true },
                            onDeleteProfile = { showDeleteDialog = true },
                            typography = typography,
                            colorScheme = colorScheme,
                            navController = navController,
                            dimensions = dimensions
                        )
                    }
                }
            }
        }

        if (showExitEditModeDialog) {
            UnsavedChangesAlertDialog(
                onDismissRequest = { showExitEditModeDialog = false },
                onSave = {
                    if (canSaveChanges) {
                        handleSave()
                        showExitEditModeDialog = false
                    }
                },
                onDontSave = {
                    currentProfileData = originalProfileData // Revert modifiche
                    isEditMode = false
                    showExitEditModeDialog = false
                },
                canSave = canSaveChanges,
                colorScheme = colorScheme
            )
        }

        if (showLogoutDialog) {
            LogoutConfirmAlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                onLogoutConfirm = { save -> handleLogout(save) },
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
                onDismissRequest = { showDeleteDialog = false },
                onConfirmDelete = { handleDelete() },
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
            onEmailChange = onEmailChange,
            onPhonePrefixChange = onPhonePrefixChange,
            onPhoneNumberWithoutPrefixChange = onPhoneNumberWithoutPrefixChange,
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        ProfileOtherOptions(
            isEditMode = isEditMode,
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

    // Campo Email modificabile
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

    // Campo Telefono modificabile
    Row(verticalAlignment = Alignment.Top) {
        ExposedDropdownMenuBox(
            expanded = prefixDropdownExpanded && isEditMode,
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
    isEditMode: Boolean,
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
            enabled = !isEditMode,
            dimensions = dimensions,
            colorScheme = colorScheme
        )
        ProfileOptionRow(
            text = "Immobili salvati",
            icon = Icons.Default.NightsStay,
            onClick = { navController.navigate(
                Screen.ApartmentListingScreen.buildRoute(
                    idUtentePath = "",
                    comunePath = "",
                    ricercaPath = ""
                )
            ) },
            enabled = !isEditMode,
            dimensions = dimensions,
            colorScheme = colorScheme
        )
        ProfileOptionRow(
            text = "Richieste appuntamenti",
            icon = Icons.Default.NightsStay,
            onClick = { navController.navigate(
                Screen.RequestsScreen.withIdUtente("")
            ) },
            enabled = !isEditMode,
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
    enabled: Boolean,
    dimensions: Dimensions,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography
) {
    val contentColor = if (enabled) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.38f)
    val iconColor = if (enabled) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.38f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
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