package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun ClickableSearchBar(
    placeholderText: String = "Cerca...",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingLarge)
            .height(Dimensions.searchBarHeight),
        shape = CircleShape,
        color = colorScheme.surface,
        shadowElevation = Dimensions.elevationSmall,
        border = BorderStroke(dimensions.borderStrokeSmall, colorScheme.outline.copy(alpha = 0.3f)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize() // Riempie la Surface
                .padding(horizontal = Dimensions.paddingMedium), // Padding interno per icona e testo
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall) // Spazio tra icona e testo
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Icona di ricerca", // Descrizione più generica per l'icona
                tint = colorScheme.onSurfaceVariant, // Colore per icone/testo placeholder
            )
            Text(
                text = placeholderText,
                color = colorScheme.onSurfaceVariant, // Colore standard per placeholder
                style = typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                modifier = Modifier
                    .fillMaxWidth()
                    // Aggiungi padding verticale all'intera Row. Questo ingrandirà la barra.
                    .padding(
                        horizontal = Dimensions.spacingSmall, // Padding ai lati della barra
                        vertical = Dimensions.spacingSmall    // Padding sopra e sotto il contenuto (es. 8dp + 8dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                // Spazio tra IconButton e TextField gestito da Arrangement.spacedBy
                horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
            ) {
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier // Rimosso padding orizzontale specifico, gestito da Row
                        .size(Dimensions.iconSizeLarge + Dimensions.spacingSmall) // Es. 44dp
                        .background(colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = colorScheme.onPrimary,
                        modifier = Modifier.size(Dimensions.iconSizeMedium)
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
                    modifier = Modifier
                        .weight(1f)
                        // Rimuoviamo l'altezza fissa dal TextField.
                        // La sua altezza sarà determinata intrinsecamente dal testo e dal padding interno di TextField,
                        // e sarà centrata verticalmente dalla Row.
                        // Per assicurare che non sia troppo "schiacciato" se la Row è molto bassa:
                        .defaultMinSize(minHeight = Dimensions.buttonHeight - Dimensions.spacingMedium) // Es. 40dp altezza minima desiderata per il TextField
                        .clip(CircleShape) // O RoundedCornerShape(Dimensions.cornerRadiusLarge) per un aspetto più da "pillola"
                        .background(colorScheme.surface.copy(alpha = 0.15f))
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState -> onFocusChanged(focusState.isFocused) },
                        colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surfaceDim,
                        unfocusedContainerColor = colorScheme.surfaceDim,
                        disabledContainerColor = colorScheme.surfaceDim,
                        cursorColor = colorScheme.onPrimary,
                        focusedIndicatorColor = colorScheme.surfaceDim,
                        unfocusedIndicatorColor = colorScheme.surfaceDim,
                        disabledIndicatorColor = colorScheme.surfaceDim,
                        errorIndicatorColor = colorScheme.surfaceDim,
                        focusedTextColor = colorScheme.onPrimary,
                        unfocusedTextColor = colorScheme.onPrimary,
                        disabledTextColor = colorScheme.onPrimary.copy(alpha = 0.38f),
                        focusedPlaceholderColor = colorScheme.onPrimary.copy(alpha = 0.7f),
                        unfocusedPlaceholderColor = colorScheme.onPrimary.copy(alpha = 0.7f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = imeAction),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearchKeyboardAction(searchQuery) },
                        onDone = { onSearchKeyboardAction(searchQuery) }
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

@Composable
fun GeneralHeaderBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions // Assuming Dimensions is a custom object for sizes

    // Container that includes the status bar background
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // This Box draws the background behind the system status bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(colorScheme.primary) // Use the same color as the header
        )
        // Header content
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                // A standard height for a top app bar is 56.dp
                .height(dimensions.headerBarHeight),
            color = colorScheme.primary,
            // No shape attribute means it will be rectangular
            shadowElevation = dimensions.elevationMedium // Standard elevation for visual separation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensions.paddingExtraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (Left)
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = colorScheme.onPrimary
                    )
                }

                // Title (Center)
                Text(
                    text = title.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensions.paddingMedium),
                    color = colorScheme.onPrimary,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Optional Actions (Right)
                // The content provided in the `actions` lambda will be placed here.
                // RowScope allows the caller to use modifiers like `align`.
                actions()
            }
        }
    }
}

/**
 * Componente AppBar generale per le schermate dell'app
 *
 * @param title Titolo da mostrare nella barra
 * @param actionIcon Icona del pulsante d'azione (opzionale)
 * @param actionContentDescription Descrizione per l'accessibilità del pulsante d'azione
 * @param onActionClick Callback per il click del pulsante d'azione
 * @param actionBackgroundColor Colore di sfondo del pulsante d'azione (default: primaryContainer)
 * @param actionIconTint Colore dell'icona del pulsante d'azione (default: onPrimaryContainer)
 * @param showAppIcon Se mostrare l'icona dell'app a sinistra del titolo (default: true)
 * @param statusBarColor Colore della status bar (default: primaryContainer)
 * @param backgroundColor Colore di sfondo della barra principale (default: primary)
 * @param titleColor Colore del titolo (default: onPrimary)
 * @param colorScheme Schema di colori (default: MaterialTheme.colorScheme)
 * @param typography Tipografia (default: MaterialTheme.typography)
 * @param dimensions Dimensioni (default: Dimensions)
 */
@Composable
fun AppTopBar(
    title: String,
    actionIcon: ImageVector? = null,
    actionContentDescription: String = "",
    onActionClick: (() -> Unit)? = null,
    actionBackgroundColor: Color? = null,
    actionIconTint: Color? = null,
    showAppIcon: Boolean = true,
    statusBarColor: Color? = null,
    backgroundColor: Color? = null,
    titleColor: Color? = null,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
    dimensions: Dimensions = Dimensions
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Status Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(statusBarColor ?: colorScheme.primaryContainer)
        )

        // Main AppBar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor ?: colorScheme.primary)
                .clip(
                    RoundedCornerShape(
                        bottomStart = dimensions.cornerRadiusLarge,
                        bottomEnd = dimensions.cornerRadiusLarge
                    )
                )
                .padding(horizontal = dimensions.paddingLarge)
                .padding(top = dimensions.paddingMedium, bottom = dimensions.paddingLarge),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side: App icon + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (showAppIcon) {
                        AppIconDisplay(
                            size = dimensions.logoMedium,
                            shapeRadius = dimensions.cornerRadiusMedium
                        )
                        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
                    }
                    Text(
                        text = title,
                        style = typography.titleLarge,
                        color = titleColor ?: colorScheme.onPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Right side: Action button (if provided)
                if (actionIcon != null && onActionClick != null) {
                    CircularIconActionButton(
                        onClick = onActionClick,
                        iconVector = actionIcon,
                        contentDescription = actionContentDescription,
                        backgroundColor = actionBackgroundColor ?: colorScheme.primaryContainer,
                        iconTint = actionIconTint ?: colorScheme.onPrimaryContainer,
                        iconSize = dimensions.iconSizeMedium,
                    )
                }
            }
        }
    }
}

