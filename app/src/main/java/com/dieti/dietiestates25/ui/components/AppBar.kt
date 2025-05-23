package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.3f)),
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
