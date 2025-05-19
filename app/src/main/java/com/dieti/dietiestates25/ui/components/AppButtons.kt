package com.dieti.dietiestates25.ui.components

// Common Imports
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

// --- ALTRI COMPONENTI UI ---
@Composable
fun AppIconDisplay(
    modifier: Modifier = Modifier,
    iconResId: Int = R.drawable.appicon1,
    contentDescription: String = "App Icon",
    size: Dp = 60.dp,
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
fun AppPropertyCard(
    price: String,
    imageResId: Int = R.drawable.property1,
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier1,
    address: String? = null,
    details: List<String> = emptyList(),
    actionButton: @Composable (() -> Unit)? = null,
    cardHeight: Dp = 210.dp,
    imageHeight: Dp = 120.dp,
    elevation: Dp = Dimensions.cardDefaultElevation,
    horizontalMode: Boolean = false,
    cardWidthHorizontal: Dp = 280.dp,
    imageWidthHorizontal: Dp = 110.dp
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val cardModifierCombined = if (horizontalMode) {
        modifier.width(cardWidthHorizontal).heightIn(min = Dimensions.buttonHeight * 2)
    } else {
        modifier.height(cardHeight)
    }

    Card(
        modifier = cardModifierCombined,
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        val contentModifier = if (actionButton == null) Modifier1.clickable(onClick = onClick) else Modifier1

        if (horizontalMode) {
            Row(modifier = Modifier1.fillMaxSize().then(contentModifier)) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Property Image: $price",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier1
                        .width(imageWidthHorizontal)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = Dimensions.cornerRadiusMedium, bottomStart = Dimensions.cornerRadiusMedium))
                )
                Column(
                    modifier = Modifier1.weight(1f).fillMaxHeight().padding(Dimensions.paddingSmall),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = price, style = typography.titleMedium, color = colorScheme.onSurface)
                        address?.let { Text(it, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        if (details.isNotEmpty()) {
                            Spacer(modifier = Modifier1.height(Dimensions.spacingExtraSmall))
                            Text(details.joinToString(" • "), style = typography.bodySmall, color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    actionButton?.let { Box(modifier = Modifier1.align(Alignment.End).padding(top = Dimensions.spacingSmall)) { it() } }
                }
            }
        } else { // Vertical Mode
            Column(modifier = Modifier1.fillMaxSize().then(contentModifier)) {
                Image(painter = painterResource(id = imageResId), contentDescription = "Property Image: $price", contentScale = ContentScale.Crop, modifier = Modifier1.fillMaxWidth().height(imageHeight))
                Column(modifier = Modifier1.fillMaxWidth().padding(Dimensions.paddingSmall), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier1.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text(text = price, style = typography.titleMedium, color = colorScheme.onSurface)
                        actionButton?.invoke()
                    }
                    address?.let { Text(it, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier1.padding(top = Dimensions.spacingExtraSmall)) }
                    if (details.isNotEmpty()) {
                        Spacer(modifier = Modifier1.height(Dimensions.spacingExtraSmall))
                        Text(details.joinToString(" • "), style = typography.bodySmall, color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun AppPropertyViewButton(
    text: String = "Visualizza",
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier1
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Dimensions.elevationSmall,
            pressedElevation = Dimensions.elevationSmall / 2
        ),
        contentPadding = PaddingValues(horizontal = Dimensions.paddingMedium, vertical = Dimensions.spacingExtraSmall),
        modifier = modifier.height(Dimensions.iconSizeLarge)
    ) {
        Text(text = text, style = typography.labelMedium)
    }
}

@Composable
fun FilterSection(
    title: String,
    modifier: Modifier = Modifier,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.cornerRadiusMedium)
    ) {
        Text(
            text = title,
            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.paddingSmall)
        )
        content()
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
            containerColor = if (isSelected) colorScheme.primaryContainer else Color.Transparent,
            contentColor = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurface
        ),
        border = BorderStroke(
            1.dp,
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
                    containerColor = if (isSelected) colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant
                ),
                border = if (isSelected) null else BorderStroke(1.dp, colorScheme.outline),
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
            containerColor = if (isSelected) colorScheme.secondaryContainer else Color.Transparent,
            contentColor = if (isSelected) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        border = BorderStroke(
            width = 1.dp,
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
fun ClickableSearchBar(
    placeholderText: String = "Search...",
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge)
            .height(Dimensions.searchBarHeight),
        shape = CircleShape,
        color = colorScheme.primaryContainer.copy(alpha = 0.8f),
        shadowElevation = Dimensions.elevationSmall,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier1
                .fillMaxSize()
                .padding(horizontal = Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = placeholderText,
                tint = colorScheme.onPrimaryContainer,
            )
            Text(
                text = placeholderText,
                color = colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                style = typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun <T> PropertyShowcaseSection(
    title: String,
    items: List<T>,
    itemContent: @Composable (item: T) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
    seeAllText: String = "Vedi tutte",
    listContentPadding: PaddingValues = PaddingValues(horizontal = Dimensions.paddingLarge),
    listHorizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimensions.spacingMedium),
    keyProvider: ((item: T) -> Any)? = null
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    TitledSection(
        title = title,
        modifier = modifier,
        onSeeAllClick = onSeeAllClick,
        seeAllText = seeAllText,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (items.isNotEmpty()) {
            LazyRow(
                contentPadding = listContentPadding,
                horizontalArrangement = listHorizontalArrangement
            ) {
                items(
                    items = items,
                    key = keyProvider ?: { item -> item.hashCode() } // Fallback a hashCode se keyProvider è null
                ) { item ->
                    itemContent(item)
                }
            }
        } else {
            Box(
                modifier = Modifier1
                    .fillMaxWidth()
                    .padding(listContentPadding)
                    .padding(vertical = Dimensions.paddingLarge),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Nessun elemento da mostrare.",
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TitledSection(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
    seeAllText: String = "See all",
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    seeAllTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier1
                .padding(horizontal = Dimensions.paddingLarge)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = colorScheme.onBackground,
                style = titleStyle,
                fontWeight = FontWeight.SemiBold
            )
            onSeeAllClick?.let { onClick ->
                TextButton(
                    onClick = onClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.primary)
                ) {
                    Text(seeAllText, style = seeAllTextStyle)
                }
            }
        }
        Spacer(modifier = Modifier1.height(Dimensions.cornerRadiusMedium))
        Box(modifier = Modifier1.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun CustomSearchAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackPressed: () -> Unit,
    onClearSearch: () -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = remember { FocusRequester() },
    imeAction: ImeAction = ImeAction.Search,
    onSearchKeyboardAction: (String) -> Unit = {},
    onFocusChanged: (hasFocus: Boolean) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colorScheme.primary,
        shadowElevation = Dimensions.elevationMedium
    ) {
        Row(
            modifier = Modifier1
                .fillMaxWidth()
                .padding(vertical = Dimensions.paddingSmall)
                .padding(start = Dimensions.paddingSmall, end = Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier1
                    .size(Dimensions.iconSizeLarge + Dimensions.spacingSmall)
                    .background(colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier1.size(Dimensions.iconSizeMedium)
                )
            }

            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = placeholderText,
                        style = typography.bodyLarge,
                        color = colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                },
                modifier = Modifier1
                    .weight(1f)
                    .padding(horizontal = Dimensions.spacingSmall)
                    .height(Dimensions.buttonHeight - Dimensions.spacingMedium)
                    .clip(CircleShape)
                    .background(colorScheme.surface.copy(alpha = 0.15f))
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState -> onFocusChanged(focusState.isFocused) },
                colors = TextFieldDefaults.colors( // TextFieldColors in M3
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = colorScheme.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = colorScheme.onPrimary,
                    unfocusedTextColor = colorScheme.onPrimary,
                    disabledTextColor = colorScheme.onPrimary.copy(alpha = 0.38f),
                    focusedPlaceholderColor = colorScheme.onPrimary.copy(alpha = 0.7f),
                    unfocusedPlaceholderColor = colorScheme.onPrimary.copy(alpha = 0.7f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = imeAction),
                keyboardActions = KeyboardActions(
                    onSearch = { onSearchKeyboardAction(searchQuery); defaultKeyboardAction(imeAction) }, // Chiamata corretta
                    onDone = { onSearchKeyboardAction(searchQuery); defaultKeyboardAction(imeAction) } // Chiamata corretta
                ),
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancella",
                                tint = colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca",
                            tint = colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                textStyle = typography.bodyLarge.copy(color = colorScheme.onPrimary)
            )
        }
    }
}