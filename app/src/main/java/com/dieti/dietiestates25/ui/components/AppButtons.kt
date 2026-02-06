package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.theme.Dimensions
import androidx.compose.ui.Modifier.Companion as Modifier1

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    icon: ImageVector? = null,
    iconContentDescription: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = modifier.height(Dimensions.buttonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimensions.elevationLarge,
            pressedElevation = Dimensions.elevationMedium
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription ?: text,
                    tint = colorScheme.onPrimary
                )
                Spacer(modifier = Modifier1.width(Dimensions.spacingSmall))
            }
            Text(text = text, style = textStyle)
        }
    }
}

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    icon: ImageVector? = null,
    iconContentDescription: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = modifier.height(Dimensions.buttonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(Dimensions.spacingSmall),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondary,
            contentColor = colorScheme.onSecondary,
            disabledContainerColor = colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimensions.elevationMedium,
            pressedElevation = Dimensions.elevationSmall
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription ?: text,
                    tint = colorScheme.onSecondary
                )
                Spacer(modifier = Modifier1.width(Dimensions.spacingSmall))
            }
            Text(text = text, style = textStyle)
        }
    }
}

@Composable
fun AppRedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    icon: ImageVector? = null,
    iconContentDescription: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = modifier.height(Dimensions.buttonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.error,
            contentColor = colorScheme.onError,
            disabledContainerColor = colorScheme.error.copy(alpha = 0.12f),
            disabledContentColor = colorScheme.onError.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimensions.elevationMedium,
            pressedElevation = Dimensions.elevationSmall
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription ?: text,
                    tint = colorScheme.onError
                )
                Spacer(modifier = Modifier1.width(Dimensions.spacingSmall))
            }
            Text(text = text, style = textStyle)
        }
    }
}

@Composable
fun AppIconDisplay(
    modifier: Modifier = Modifier,
    iconResId: Int = R.drawable.appicon1,
    contentDescription: String = "App Icon",
    size: Dp = Dimensions.iconSizeMedium,
    shapeRadius: Dp = Dimensions.spacingMedium,
    internalPadding: Dp = Dimensions.paddingSmall,
    imageClipRadius: Dp = Dimensions.spacingSmall,
    elevation: Dp = Dimensions.elevationMedium
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(shapeRadius),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = elevation
    ) {
        Box(
            modifier = Modifier1
                .padding(internalPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,
                modifier = Modifier1
                    .fillMaxSize()
                    .clip(RoundedCornerShape(imageClipRadius)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun SelectableOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = dimensions.iconSizeLarge + dimensions.paddingExtraSmall),
        shape = RoundedCornerShape(dimensions.spacingSmall),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceDim,
            contentColor = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
        ),
        border = BorderStroke(
            dimensions.borderStrokeSmall,
            if (isSelected) colorScheme.primaryContainer else colorScheme.outline.copy(alpha = 0.5f)
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(dimensions.elevationSmall) else null,
        contentPadding = PaddingValues(
            horizontal = dimensions.paddingSmall,
            vertical = dimensions.cornerRadiusMedium
        )
    ) {
        Text(
            text = text,
            style = typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SingleChoiceToggleGroup(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.spacingSmall))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(dimensions.paddingExtraSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingExtraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { optionText ->
            val isSelected = optionText == selectedOption
            OutlinedButton(
                onClick = {
                    onOptionSelected(if (isSelected) null else optionText)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.iconSizeLarge + dimensions.paddingExtraSmall),
                shape = RoundedCornerShape(dimensions.spacingSmall - dimensions.spacingExtraSmall),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) colorScheme.primaryContainer else colorScheme.surfaceDim,
                    contentColor = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                ),
                border = if (isSelected) null else BorderStroke(dimensions.borderStrokeSmall, colorScheme.outline),
                elevation = if (isSelected) ButtonDefaults.buttonElevation(dimensions.elevationSmall) else null,
                contentPadding = PaddingValues(horizontal = dimensions.paddingSmall)
            ) {
                Text(
                    text = optionText,
                    style = typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

data class PredefinedRange(val label: String, val min: Float, val max: Float)

@Composable
fun CustomFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(dimensions.iconSizeLarge - dimensions.spacingExtraSmall),
        enabled = enabled,
        shape = RoundedCornerShape(dimensions.spacingSmall),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) colorScheme.secondaryContainer else colorScheme.surfaceDim,
            contentColor = if (isSelected) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        border = BorderStroke(
            width = dimensions.borderStrokeSmall,
            color = when {
                !enabled && isSelected -> colorScheme.outline.copy(alpha = 0.12f)
                !enabled && !isSelected -> colorScheme.outline.copy(alpha = 0.12f)
                isSelected -> colorScheme.secondaryContainer.copy(alpha = 0.9f)
                else -> colorScheme.outline.copy(alpha = 0.5f)
            }
        ),
        contentPadding = PaddingValues(horizontal = dimensions.paddingMedium)
    ) {
        Text(
            text = label,
            style = typography.labelMedium, // Stile testo per chip
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun RangeFilterInput(
    title: String,
    minTextFieldValue: String,
    onMinTextFieldChange: (String) -> Unit,
    maxTextFieldValue: String,
    onMaxTextFieldChange: (String) -> Unit,
    sliderPosition: ClosedFloatingPointRange<Float>,
    onSliderPositionChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    predefinedRanges: List<PredefinedRange>,
    onPredefinedRangeSelected: (PredefinedRange) -> Unit,
    unitSuffix: String = "",
    steps: Int = 0,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme,
    componentEnabled: Boolean = true
) {
    FilterSection(
        title = title,
        dimensions = dimensions,
        typography = typography,
        colorScheme = colorScheme
    ) {
        Row(
            modifier = Modifier1
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
        ) {
            predefinedRanges.forEach { range ->
                val isSelected = sliderPosition.start == range.min && sliderPosition.endInclusive == range.max
                CustomFilterChip(
                    label = range.label,
                    isSelected = isSelected,
                    onClick = { if (componentEnabled) onPredefinedRangeSelected(range) },
                    enabled = componentEnabled,
                    dimensions = dimensions,
                    typography = typography,
                    colorScheme = colorScheme
                )
            }
        }

        Spacer(modifier = Modifier1.height(dimensions.spacingSmall))

        RangeSlider(
            value = sliderPosition,
            onValueChange = onSliderPositionChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier1.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = colorScheme.primary,
                activeTrackColor = colorScheme.primary,
                inactiveTrackColor = colorScheme.primary.copy(alpha = 0.3f),
                disabledThumbColor = colorScheme.onSurface.copy(alpha = 0.38f),
                disabledActiveTrackColor = colorScheme.onSurface.copy(alpha = 0.12f),
                disabledInactiveTrackColor = colorScheme.onSurface.copy(alpha = 0.12f)
            ),
            enabled = componentEnabled
        )
        Row(modifier = Modifier1.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = if (sliderPosition.start <= valueRange.start) "Min" else "${sliderPosition.start.toInt()}$unitSuffix",
                style = typography.labelSmall,
                color = if (componentEnabled) colorScheme.onSurfaceVariant else colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )
            Text(
                text = if (sliderPosition.endInclusive >= valueRange.endInclusive) "Max" else "${sliderPosition.endInclusive.toInt()}$unitSuffix",
                style = typography.labelSmall,
                color = if (componentEnabled) colorScheme.onSurfaceVariant else colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )
        }

        Spacer(modifier = Modifier1.height(dimensions.spacingMedium))

        Row(
            modifier = Modifier1.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            OutlinedTextField(
                value = minTextFieldValue,
                onValueChange = onMinTextFieldChange,
                label = { Text("Minimo $unitSuffix") },
                modifier = Modifier1.weight(1f),
                shape = RoundedCornerShape(dimensions.spacingSmall),
                colors = defaultOutlineTextFieldColors(colorScheme, typography),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = typography.bodyMedium.copy(color = if (componentEnabled) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.38f)),
                enabled = componentEnabled
            )
            OutlinedTextField(
                value = maxTextFieldValue,
                onValueChange = onMaxTextFieldChange,
                label = { Text("Massimo $unitSuffix") },
                modifier = Modifier1.weight(1f),
                shape = RoundedCornerShape(dimensions.spacingSmall),
                colors = defaultOutlineTextFieldColors(colorScheme, typography),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = typography.bodyMedium.copy(color = if (componentEnabled) colorScheme.onSurface else colorScheme.onSurface.copy(alpha = 0.38f)),
                enabled = componentEnabled
            )
        }
    }
}

@Composable
internal fun defaultOutlineTextFieldColors(colorScheme: ColorScheme, typography: Typography): TextFieldColors =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.7f),
        disabledBorderColor = colorScheme.outline.copy(alpha = 0.38f),
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.onSurfaceVariant,
        disabledLabelColor = colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
        cursorColor = colorScheme.primary,
        focusedTextColor = colorScheme.onSurface,
        unfocusedTextColor = colorScheme.onSurface,
        disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f),
        focusedPlaceholderColor = colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = colorScheme.onSurfaceVariant,
        disabledPlaceholderColor = colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    )

@Composable
fun AppPropertyViewButton(
    text: String = "Visualizza",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Button( // Potrebbe essere un TextButton o OutlinedButton per meno enfasi
        onClick = onClick,
        shape = CircleShape, // Forma a pillola
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimensions.elevationSmall,
            pressedElevation = Dimensions.elevationSmall / 2 // Meno ombra alla pressione
        ),
        contentPadding = PaddingValues(horizontal = Dimensions.paddingMedium, vertical = Dimensions.spacingExtraSmall),
        modifier = modifier.heightIn(min = Dimensions.iconSizeLarge) // Altezza minima basata su icona/testo
    ) {
        Text(text = text, style = typography.labelMedium) // Testo più piccolo per un bottone compatto
    }
}

/**
 * Un IconButton generalizzato con uno sfondo circolare.
 *
 * @param onClick Azione da eseguire al click.
 * @param iconVector L'ImageVector per l'icona.
 * @param contentDescription Descrizione per l'accessibilità.
 * @param modifier Modificatori per l'IconButton.
 * @param backgroundColor Colore di sfondo del cerchio. Defaulta a `MaterialTheme.colorScheme.primaryContainer`.
 * @param iconTint Colore dell'icona. Defaulta a `MaterialTheme.colorScheme.onPrimaryContainer`.
 * @param buttonSize Dimensione del cerchio di sfondo. Defaulta a 40.dp.
 * @param iconSize Dimensione dell'icona. Defaulta a 22.dp.
 */
@Composable
fun CircularIconActionButton(
    onClick: () -> Unit,
    iconVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    buttonSize: Dp = Dimensions.buttonSize,
    circleSize: Dp = Dimensions.circularIconSize,
    iconSize: Dp = Dimensions.iconSizeMedium
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize) // Applica il modifier passato all'IconButton esterno
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(circleSize)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
fun SegmentedButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(dimensions.buttonSize),
        shape = RoundedCornerShape(dimensions.cornerRadiusExtraLarge),
        border = BorderStroke(
            width = dimensions.borderStrokeSmall,
            color = if (selected) colorScheme.primary else colorScheme.onBackground.copy(alpha = 0.2f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) colorScheme.primary else Color.Transparent,
            contentColor = if (selected) colorScheme.onPrimary else colorScheme.onBackground
        )
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Pulsante speciale in stile "Manager" (Bordo spesso, sfondo trasparente).
 * Replica il design richiesto: Icona SX - Testo Bold - Freccia DX
 */
@Composable
fun ManagerMenuButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector = Icons.Default.Apartment, // Icona di default (Immobile)
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
    dimensions: Dimensions = Dimensions
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()//64.dp
            .height(dimensions.buttonSize ), // Altezza maggiorata per impatto visivo
        shape = RoundedCornerShape(dimensions.cornerRadiusLarge),
        // Bordo spesso (2.dp) usando il colore onSurface (Grigio/Bianco a seconda del tema)
        border = BorderStroke(dimensions.borderStrokeSmall, colorScheme.onSurface),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent, // Background trasparente
            contentColor = colorScheme.onSurface // Testo e icone adattati al tema
        ),
        contentPadding = PaddingValues(horizontal = dimensions.paddingMedium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icona a Sinistra
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(dimensions.iconSizeMedium),
                tint = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.width(dimensions.spacingMedium))

            // Testo Centrale
            Text(
                text = text,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                color = colorScheme.onSurface
            )

            // Chevron a Destra
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
        }
    }
}