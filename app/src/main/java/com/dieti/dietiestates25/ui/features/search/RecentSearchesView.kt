package com.dieti.dietiestates25.ui.features.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun RecentSearchesView(
    recentSearches: List<String>,
    onRecentSearchClicked: (String) -> Unit,
    onClearRecentSearch: (String) -> Unit,
    typography: Typography,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensions.paddingLarge)
    ) {
        if (recentSearches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensions.paddingLarge),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Nessuna ricerca recente.",
                    color = colorScheme.onBackground.copy(alpha = 0.7f),
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = dimensions.paddingLarge)
                )
            }
        } else {
            Text(
                text = "Ricerche Recenti",
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = dimensions.paddingLarge, vertical = dimensions.spacingMedium)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = dimensions.paddingLarge),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
            ) {
                items(items = recentSearches, key = { it }) { query ->
                    RecentSearchItem(
                        query = query,
                        onClick = { onRecentSearchClicked(query) },
                        onClearClick = { onClearRecentSearch(query) },
                        typography = typography,
                        colorScheme = colorScheme,
                        dimensions = dimensions
                    )
                }
            }
        }
    }
}

@Composable
fun RecentSearchItem(
    query: String,
    onClick: () -> Unit,
    onClearClick: () -> Unit,
    typography: Typography,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Ricerca Recente",
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimensions.iconSizeMedium)
            )
            Spacer(modifier = Modifier.width(dimensions.spacingMedium))
            Text(
                text = query,
                style = typography.bodyLarge,
                color = colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(
            onClick = onClearClick, 
            modifier = Modifier.size(dimensions.iconSizeMedium + dimensions.spacingSmall)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Cancella ricerca recente",
                tint = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(dimensions.iconSizeSmall)
            )
        }
    }
}