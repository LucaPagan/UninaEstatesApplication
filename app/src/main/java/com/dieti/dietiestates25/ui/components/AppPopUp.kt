package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun UnsavedChangesAlertDialog( // Rinominato per chiarezza e convenzione
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onDontSave: () -> Unit,
    canSave: Boolean,
    confirmButtonText: String = "Salva",
    dismissButtonText: String = "Annulla",
    colorScheme: ColorScheme = MaterialTheme.colorScheme // Fornisce un default
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Modifiche Non Salvate") },
        text = { Text("Hai delle modifiche non salvate. Vuoi salvarle prima di uscire dalla modalità modifica?") },
        confirmButton = {
            AppPrimaryButton(
                onClick = onSave,
                text = confirmButtonText,
                enabled = canSave,
            )
        },
        dismissButton = {
            AppRedButton(
                onClick = onDontSave,
                text = dismissButtonText)
        },
        containerColor = colorScheme.surfaceVariant,
        titleContentColor = colorScheme.onSurfaceVariant,
        textContentColor = colorScheme.onSurfaceVariant
    )
}

@Composable
fun LogoutConfirmAlertDialog(
    onDismissRequest: () -> Unit,
    onLogoutConfirm: (saveFirst: Boolean) -> Unit,
    isEditMode: Boolean,
    hasUnsavedChanges: Boolean,
    canSaveChanges: Boolean,
    confirmButtonText: String = "Salva ed Esci", // Testo per il pulsante di conferma (quando ce ne sono due)
    dismissButtonText: String = "Esci", // Testo per il pulsante di "uscita" o "uscita senza salvare"
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography, // Usato per lo stile del testo dei bottoni
    dimensions: Dimensions = Dimensions
) {
    val title = "Conferma Uscita"
    var message = "Stai per uscire dal profilo. Continuare?"
    if (isEditMode && hasUnsavedChanges) {
        message = "Hai modifiche non salvate. Vuoi salvarle prima di uscire?"
    }

    val centeredButtonTextStyle = typography.bodySmall.copy(
        textAlign = TextAlign.Center
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            if (isEditMode && hasUnsavedChanges) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                ) {
                    AppRedButton(
                        onClick = { onLogoutConfirm(false) },
                        text = dismissButtonText,
                        textStyle = centeredButtonTextStyle
                    )
                    AppPrimaryButton(
                        onClick = { onLogoutConfirm(true) },
                        text = confirmButtonText,
                        textStyle = centeredButtonTextStyle,
                        enabled = canSaveChanges
                    )
                }
            } else {
                AppPrimaryButton(
                    onClick = { onLogoutConfirm(false) },
                    text = "Annulla",
                    textStyle = centeredButtonTextStyle
                )
            }
        },
        dismissButton = {
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

@Composable
fun DeleteConfirmAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
    title: String = "Conferma Eliminazione",
    text: String = "Sei sicuro di voler eliminare il tuo profilo? Questa azione è irreversibile.",
    confirmButtonText: String = "Elimina",
    dismissButtonText: String = "Annulla",
    colorScheme: ColorScheme = MaterialTheme.colorScheme
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            AppRedButton( // Il pulsante di conferma è un'azione distruttiva
                onClick = onConfirmDelete,
                text = confirmButtonText
            )
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { // TextButton per "Annulla"
                Text(text = dismissButtonText)
            }
        },
        containerColor = colorScheme.surfaceVariant,
        titleContentColor = colorScheme.onSurfaceVariant,
        textContentColor = colorScheme.onSurfaceVariant
    )
}