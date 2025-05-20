package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.components.CircularIconActionButton // Assicurati che questo import ci sia
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.theme.Dimensions // Importa il tuo oggetto Dimensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp // Mantieni per i valori hardcoded non sostituibili
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModel

// Data class ProfileData (invariata)
data class ProfileData(
    val name: String = "Lorenzo",
    val email: String = "LorenzoTrignano@gmail.com",
    val phone: String = "+39 123456789"
)

// ProfileViewModel (invariato)
class ProfileViewModel : ViewModel() {
    private val _profileData = MutableStateFlow(ProfileData())
    val profileData = _profileData.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()

    fun onNameChange(newName: String) {
        _profileData.value = _profileData.value.copy(name = newName)
    }

    fun onEmailChange(newEmail: String) {
        _profileData.value = _profileData.value.copy(email = newEmail)
    }

    fun onPhoneChange(newPhone: String) {
        _profileData.value = _profileData.value.copy(phone = newPhone)
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun saveChanges() {
        println("Salvataggio modifiche: ${_profileData.value}")
        _isEditMode.value = false
    }

    fun logout() {
        println("Logout eseguito")
    }

    fun deleteProfile() {
        println("Profilo eliminato")
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val focusManager = LocalFocusManager.current
        val dimensions = Dimensions // Istanza locale per accesso breve

        val profileData by viewModel.profileData.collectAsState()
        val isEditMode by viewModel.isEditMode.collectAsState()

        Scaffold(
            topBar = {
                ProfileScreenHeader(
                    isEditMode = isEditMode,
                    onToggleEditMode = viewModel::toggleEditMode,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions // Passa dimensions
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
                // L'immagine del profilo era qui, la rimuoviamo come da richiesta
                // per basarci solo sul codice fornito nell'ultima interazione per ProfileScreenHeader

                ProfileContent(
                    profileData = profileData,
                    isEditMode = isEditMode,
                    onNameChange = viewModel::onNameChange,
                    onEmailChange = viewModel::onEmailChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onSaveChanges = {
                        viewModel.saveChanges()
                        focusManager.clearFocus()
                    },
                    onLogout = viewModel::logout,
                    onDeleteProfile = viewModel::deleteProfile,
                    typography = typography,
                    colorScheme = colorScheme,
                    focusManager = focusManager,
                    navController = navController,
                    dimensions = dimensions // Passa dimensions
                )
            }
        }
    }
}

@Composable
private fun ProfileScreenHeader(
    isEditMode: Boolean,
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
            .clip(RoundedCornerShape(bottomStart = dimensions.cornerRadiusLarge, bottomEnd = dimensions.cornerRadiusLarge))
            .padding(horizontal = dimensions.paddingLarge)
            .padding(
                top = 25.dp, // 25.dp non in Dimensions, lasciato invariato
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
                    size = 60.dp, // 60.dp non in Dimensions.iconSize*, lasciato come dimensione specifica
                    shapeRadius = dimensions.cornerRadiusMedium
                )
                Spacer(modifier = Modifier.width(dimensions.spacingMedium))
                Text(
                    text = if (isEditMode) "Modifica Profilo" else "Profilo Utente",
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
                buttonSize = 40.dp, // 40.dp non in Dimensions (tra iconSizeLarge e iconSizeExtraLarge), lasciato come dimensione specifica del bottone
                backgroundColor = currentBackgroundColor,
                iconTint = currentIconTint,
                iconSize = dimensions.iconSizeMedium // SOSTITUITO 24.dp
            )
        }
    }
}

@Composable
private fun ProfileContent(
    profileData: ProfileData,
    isEditMode: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onLogout: () -> Unit,
    onDeleteProfile: () -> Unit,
    typography: Typography,
    colorScheme: ColorScheme,
    focusManager: FocusManager,
    navController: NavController,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium) // SOSTITUITO 16.dp
            .padding(top = dimensions.paddingSmall), // SOSTITUITO 8.dp
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dati personali",
            style = typography.titleMedium,
            modifier = Modifier
                .padding(top = dimensions.paddingMedium, bottom = dimensions.paddingMedium) // SOSTITUITO 16.dp per entrambi
                .align(Alignment.Start)
        )

        ProfileDataFields(
            name = profileData.name,
            email = profileData.email,
            phone = profileData.phone,
            isEditMode = isEditMode,
            onNameChange = onNameChange,
            onEmailChange = onEmailChange,
            onPhoneChange = onPhoneChange,
            colorScheme = colorScheme,
            typography = typography
        )

        Spacer(modifier = Modifier.height(dimensions.spacingLarge)) // SOSTITUITO 24.dp

        ProfileOtherOptions(
            typography = typography,
            colorScheme = colorScheme,
            navController = navController,
            dimensions = dimensions // Passa dimensions
        )

        Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge)) // SOSTITUITO 32.dp

        ProfileActionButtons(
            isEditMode = isEditMode,
            onSaveUpdateClick = {
                if (isEditMode) {
                    onSaveChanges()
                }
            },
            onLogoutClick = onLogout,
            onDeleteProfileClick = onDeleteProfile,
            focusManager = focusManager,
            dimensions = dimensions // Passa dimensions
        )
        Spacer(modifier = Modifier.height(dimensions.spacingLarge)) // SOSTITUITO 24.dp
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDataFields(
    name: String,
    email: String,
    phone: String,
    isEditMode: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        disabledTextColor = colorScheme.onSurface.copy(alpha = 0.7f),
        disabledBorderColor = colorScheme.outline.copy(alpha = 0.5f),
        disabledLabelColor = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
    )

    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Nome Utente") },
        enabled = isEditMode,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), // 12.dp non in Dimensions, lasciato invariato
        singleLine = true,
        colors = textFieldColors,
        textStyle = typography.bodyLarge
    )

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        enabled = isEditMode,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), // 12.dp non in Dimensions, lasciato invariato
        singleLine = true,
        colors = textFieldColors,
        textStyle = typography.bodyLarge
    )

    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text("Numero di telefono") },
        enabled = isEditMode,
        modifier = Modifier.fillMaxWidth(), // Nessun padding(bottom) qui come da codice originale
        singleLine = true,
        colors = textFieldColors,
        textStyle = typography.bodyLarge
    )
}

@Composable
private fun ProfileOtherOptions(
    typography: Typography,
    colorScheme: ColorScheme,
    navController: NavController,
    dimensions: Dimensions // Aggiunto
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Altro",
            style = typography.titleMedium,
            modifier = Modifier
                .padding(bottom = dimensions.paddingSmall) // SOSTITUITO 8.dp
                .align(Alignment.Start)
        )
        ProfileOptionRow(
            text = "Controlla immobili salvati",
            icon = Icons.Default.NightsStay,
            onClick = { /* navController.navigate("saved_properties") */ },
            dimensions = dimensions // Passa dimensions
        )
        ProfileOptionRow(
            text = "Controlla richieste agenzia",
            icon = Icons.Default.NightsStay,
            onClick = { /* navController.navigate("agency_requests") */ },
            dimensions = dimensions // Passa dimensions
        )
        ProfileOptionRow(
            text = "Impostazioni Notifiche",
            icon = Icons.Default.NightsStay,
            onClick = { /* navController.navigate("notification_settings") */ },
            dimensions = dimensions // Passa dimensions
        )
    }
}

@Composable
fun ProfileOptionRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    dimensions: Dimensions // Aggiunto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensions.paddingMedium), // SOSTITUITO 16.dp
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(dimensions.spacingMedium)) // SOSTITUITO 16.dp
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "Vai a $text", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProfileActionButtons(
    isEditMode: Boolean,
    onSaveUpdateClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteProfileClick: () -> Unit,
    focusManager: FocusManager,
    dimensions: Dimensions // Aggiunto
) {
    val textForMainButton = if (isEditMode) "Salva Modifiche" else "Aggiorna Profilo"

    AppPrimaryButton(
        onClick = {
            onSaveUpdateClick()
            if (isEditMode) focusManager.clearFocus()
        },
        text = textForMainButton,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(dimensions.spacingMedium)) // SOSTITUITO 16.dp
    AppPrimaryButton(
        onClick = onLogoutClick,
        text = "Esci Dal Profilo",
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(modifier = Modifier.height(dimensions.spacingMedium)) // SOSTITUITO 16.dp
    AppRedButton(
        onClick = onDeleteProfileClick,
        text = "Elimina Profilo",
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}