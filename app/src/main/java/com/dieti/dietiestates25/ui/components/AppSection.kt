package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.dieti.dietiestates25.ui.theme.Dimensions

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
fun <T> PropertyShowcaseSection(
    title: String,
    items: List<T>, // Lista di oggetti generici (es. PropertyModel)
    itemContent: @Composable (item: T) -> Unit, // Lambda per renderizzare ogni item
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
    seeAllText: String = "Vedi tutte",
    listContentPadding: PaddingValues = PaddingValues(horizontal = Dimensions.paddingLarge),
    listHorizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(Dimensions.spacingMedium),
    keyProvider: ((item: T) -> Any)? = null
) {
    TitledSection(
        title = title,
        modifier = modifier,
        onSeeAllClick = onSeeAllClick,
        seeAllText = seeAllText,
        contentPadding = PaddingValues(top = Dimensions.spacingSmall)
    ) {
        if (items.isNotEmpty()) {
            LazyRow(
                contentPadding = listContentPadding,
                horizontalArrangement = listHorizontalArrangement
            ) {
                items(
                    items = items,
                    key = keyProvider ?: { item -> item.hashCode() }
                ) { item ->
                    itemContent(item)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(listContentPadding)
                    .padding(vertical = Dimensions.paddingLarge),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Nessun elemento da mostrare in questa sezione.",
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
    contentPadding: PaddingValues = PaddingValues(Dimensions.paddingNone),
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