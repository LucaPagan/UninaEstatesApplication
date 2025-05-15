package com.dieti.dietiestates25.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium
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
        Text(text, style = textStyle)
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
                Spacer(modifier = Modifier.width(Dimensions.spacingSmall))
            }
            Text(text = text, style = textStyle)
        }
    }
}

@Composable
fun AppIconDisplay(
    modifier: Modifier = Modifier,
    iconResId: Int = R.drawable.appicon1, // Default app icon
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
            modifier = Modifier
                .padding(internalPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(imageClipRadius)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ClickableSearchBar(
    placeholderText: String = "Search...",
    onClick: () -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.paddingLarge)
            .height(Dimensions.searchBarHeight)
            .clip(RoundedCornerShape(28.dp))
            .background(colorScheme.primary.copy(alpha = 0.8f))
            .clickable(onClick = onClick)
            .padding(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.cornerRadiusMedium
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spacingSmall)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = placeholderText,
                tint = colorScheme.onPrimary,
            )
            Text(
                text = placeholderText,
                color = colorScheme.onPrimary,
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
    listHorizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimensions.spacingMedium)
) {
    /*if (items.isEmpty() && onSeeAllClick == null) {
        // Se non ci sono items e non c'è un'azione "vedi tutte",
        // potresti decidere di non mostrare affatto la sezione.
        // Per ora, la mostriamo comunque vuota se viene chiamata.
        // Considera di aggiungere un Box vuoto o un Text placeholder qui se items è vuoto.
    }*/
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
                items(items = items, key = { item -> item.hashCode() }) { item -> // Aggiungere una chiave se gli items possono cambiare
                    itemContent(item)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(listContentPadding)
                    .padding(vertical = Dimensions.paddingMedium),
                contentAlignment = Alignment.Center,
            ) {
                 Text(
                     text = "Nessun elemento da mostrare.",
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
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
            modifier = Modifier
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
        Spacer(modifier = Modifier.height(Dimensions.cornerRadiusMedium))
        Box(modifier = Modifier.padding(contentPadding)) {
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
    onFocusChanged: (hasFocus: Boolean) -> Unit = {} // Nuovo callback
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = colorScheme.primary,
        shadowElevation = Dimensions.elevationMedium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimensions.paddingSmall)
                .padding(start = Dimensions.paddingSmall, end = Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .size(Dimensions.iconSizeLarge)
                    .background(colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
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
                    .padding(horizontal = Dimensions.paddingSmall)
                    .clip(RoundedCornerShape(Dimensions.cornerRadiusLarge))
                    .background(colorScheme.surface.copy(alpha = 0.2f))
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused) // Notifica il cambio di focus
                        // Logica per mostrare/nascondere la tastiera può essere gestita qui
                        // o lasciata al sistema in base al focus.
                        // if (focusState.isFocused) keyboardController?.show() else keyboardController?.hide()
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.surface.copy(alpha = 0.3f),
                    unfocusedContainerColor = colorScheme.surface.copy(alpha = 0.2f),
                    disabledContainerColor = colorScheme.surface.copy(alpha = 0.2f),
                    cursorColor = colorScheme.onPrimary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = colorScheme.onPrimary,
                    unfocusedTextColor = colorScheme.onPrimary,
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