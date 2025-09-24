package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val requiredFieldMark = buildAnnotatedString {
        if (isRequired) {
            append("*")
            withStyle(style = SpanStyle(color = Color.Red)) {
                append(" ")
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacingMedium)
    ) {
        Text(
            text = buildAnnotatedString {
                append(title)
                append(" ")
                append(requiredFieldMark)
            },
            style = typography.titleMedium,
            color = colorScheme.primary
        )
        // Il content() slot permette di inserire qualsiasi Composable qui dentro
        content()
        HorizontalDivider()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    required: Boolean = false,
) {
    val colorScheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = if (required) "$label *" else label

    // Il contenitore principale per questo tipo di menu
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }, // Gestisce l'apertura e la chiusura del menu
        modifier = Modifier.fillMaxWidth()
    ) {
        // Questo è il campo di testo che l'utente vede e tocca.
        // È importante che sia `readOnly = true`.
        OutlinedTextField(
            value = value,
            onValueChange = {}, // Lasciare vuoto perché il campo è di sola lettura
            readOnly = true,
            label = { Text(displayLabel) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
            ),
            // Il modifier .menuAnchor() è FONDAMENTALE.
            // Dice al menu a tendina a quale componente ancorarsi.
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        // Questo è il menu a tendina che appare quando `expanded` è true.
        ExposedDropdownMenu(
            expanded = expanded,
            // Chiude il menu se l'utente clicca fuori
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colorScheme.surface) // Usa surface per coerenza
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option) // Aggiorna lo stato
                        expanded = false      // Chiude il menu
                    },
                    // Usa i contentPadding di default per un aspetto corretto
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}


@Composable
fun RoomCounter(
    icon: ImageVector,
    title: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    // Ora 'Dimensions' esiste e può essere referenziato
    val dimensions = Dimensions

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = colorScheme.primary,
            modifier = Modifier.size(dimensions.iconSizeMedium)
        )
        Spacer(modifier = Modifier.width(dimensions.spacingSmall))
        Text(
            text = title,
            style = typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = colorScheme.onBackground
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier
                    .size(dimensions.iconSizeLarge)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Diminuisci",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(dimensions.iconSizeSmall)
                )
            }
            Text(
                text = "$count",
                style = typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = colorScheme.onBackground
            )
            IconButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(dimensions.iconSizeLarge)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumenta",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(dimensions.iconSizeSmall)
                )
            }
        }
    }
}

@Composable
fun CheckboxField(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = Dimensions.paddingExtraSmall)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colorScheme.primary,
                uncheckedColor = colorScheme.onBackground.copy(alpha = 0.5f)
            )
        )
        Text(
            text = text,
            style = typography.bodyMedium,
            color = colorScheme.onBackground
        )
    }
}