package com.dieti.dietiestates25.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.dieti.dietiestates25.ui.theme.Dimensions

/**
 * A reusable OutlinedTextField with a special border animation on focus.
 *
 * @param value The input text to be shown in the text field.
 * @param onValueChange The callback that is triggered when the input service updates the text.
 * @param modifier Modifier for this text field.
 * @param enabled Controls the enabled state of the text field.
 * @param label The optional label to be displayed inside the text field container.
 * @param placeholder The optional placeholder to be displayed when the text field is in focus and the input text is empty.
 * @param leadingIcon The optional leading icon to be displayed at the beginning of the text field container.
 * @param suffix The optional suffix to be displayed at the end of the text field container.
 * @param singleLine When set to true, this text field becomes a single horizontally scrolling text field.
 * @param keyboardOptions Software keyboard options that contains configuration such as [KeyboardType] and [ImeAction].
 * @param keyboardActions The keyboard actions.
 * @param colors The colors for this text field.
 * @param typography The MaterialTheme typography to use for styling the text.
 * @param colorScheme The MaterialTheme color scheme to use for styling.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    typography: Typography = MaterialTheme.typography, // <-- PARAMETRO MODIFICATO
    colorScheme: ColorScheme = MaterialTheme.colorScheme
) {
    var isFocused by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "border-animation-transition")
    val borderWidth by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border-width-animation"
    )

    val borderModifier = if (isFocused) {
        Modifier.border(
            width = borderWidth.dp,
            color = colorScheme.primary,
            shape = RoundedCornerShape(Dimensions.cornerRadiusExtraSmall)
        )
    } else {
        Modifier.border(
            width = 1.dp,
            color = colorScheme.outline.copy(alpha = 0.5f),
            shape = RoundedCornerShape(Dimensions.cornerRadiusExtraSmall)
        )
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .then(borderModifier),
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        suffix = suffix,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = colors,
        textStyle = typography.bodyLarge, // <-- Usa il typography passato per definire lo stile
        shape = RoundedCornerShape(Dimensions.cornerRadiusExtraSmall)
    )
}
