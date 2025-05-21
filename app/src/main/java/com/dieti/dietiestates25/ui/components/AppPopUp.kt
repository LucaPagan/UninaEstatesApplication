package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun UnsavedChangesAlertDialog( // Rinominato per chiarezza e convenzione
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onDontSave: () -> Unit,
    canSave: Boolean,
    colorScheme: ColorScheme = MaterialTheme.colorScheme // Fornisce un default
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Modifiche Non Salvate") },
        text = { Text("Hai delle modifiche non salvate. Vuoi salvarle prima di uscire dalla modalità modifica?") },
        confirmButton = {
            AppPrimaryButton(
                onClick = onSave,
                text = "Salva Modifiche",
                enabled = canSave
            )
        },
        dismissButton = {
            AppRedButton(onClick = onDontSave, text = "Non Salvare")
        },
        containerColor = colorScheme.surfaceVariant,
        titleContentColor = colorScheme.onSurfaceVariant,
        textContentColor = colorScheme.onSurfaceVariant
    )
}

@Composable
fun LogoutConfirmAlertDialog( // Rinominato per chiarezza e convenzione
    onDismissRequest: () -> Unit,
    onLogoutConfirm: (saveFirst: Boolean) -> Unit,
    isEditMode: Boolean,
    hasUnsavedChanges: Boolean,
    canSaveChanges: Boolean,
    colorScheme: ColorScheme = MaterialTheme.colorScheme, // Fornisce un default
    dimensions: Dimensions = Dimensions // Fornisce un default o assicurati sia passato
) {
    val title = "Conferma Uscita"
    var message = "Stai per uscire dal profilo. Continuare?"
    if (isEditMode && hasUnsavedChanges) {
        message = "Hai modifiche non salvate. Vuoi salvarle prima di uscire?"
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            if (isEditMode && hasUnsavedChanges) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall) // Usa dimensions
                ) {
                    AppPrimaryButton(
                        onClick = { onLogoutConfirm(true) },
                        text = "Salva ed Esci",
                        enabled = canSaveChanges,
                        modifier = Modifier.weight(1f)
                    )
                    AppRedButton(
                        onClick = { onLogoutConfirm(false) },
                        text = "Esci Senza Salvare",
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                AppPrimaryButton(onClick = { onLogoutConfirm(false) }, text = "Esci")
            }
        },
        dismissButton = {
            // Mostra "Annulla" solo se non ci sono le opzioni Salva/Non Salvare nel confirmButton
            if (!(isEditMode && hasUnsavedChanges)) {
                TextButton(onClick = onDismissRequest) {
                    Text("Annulla")
                }
            }
        },
        containerColor = colorScheme.surfaceVariant,
        titleContentColor = colorScheme.onSurfaceVariant,
        textContentColor = colorScheme.onSurfaceVariant
    )
}