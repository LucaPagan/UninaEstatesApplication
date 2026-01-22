package com.dieti.dietiestates25.ui.components

import androidx.annotation.DrawableRes
import com.dieti.dietiestates25.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dieti.dietiestates25.data.model.PropertyMarker
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.google.android.gms.maps.model.LatLng
@Composable
fun AppPropertyCard(
    price: String,
    // NUOVO: URL dell'immagine (dal Backend)
    imageUrl: String? = null,
    // ESISTENTE: ID Risorsa locale (Fallback o Preview)
    @DrawableRes imageResId: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    address: String? = null,
    details: List<String> = emptyList(),
    actionButton: @Composable (() -> Unit)? = null,
    elevationDp: Dp = Dimensions.cardDefaultElevation,
    horizontalMode: Boolean = false,
    imageHeightVerticalRatio: Float = 0.55f,
    imageWidthHorizontalRatio: Float = 0.4f
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Gestione click sulla card intera se non c'è un bottone azione specifico
    val cardClickModifier = if (actionButton == null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    // LOGICA DI CARICAMENTO IMMAGINE (Helper locale)
    // Se c'è un URL usa Coil, altrimenti usa la risorsa locale
    val imageContent: @Composable (Modifier) -> Unit = { imgModifier ->
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    // Immagine da mostrare in caso di errore di caricamento
                    .error(imageResId ?: R.drawable.property1)
                    // Immagine da mostrare mentre carica
                    .placeholder(imageResId ?: R.drawable.property1)
                    .build(),
                contentDescription = "Immagine proprietà: $price",
                contentScale = ContentScale.Crop,
                modifier = imgModifier
            )
        } else {
            // Fallback su risorsa statica
            Image(
                painter = painterResource(id = imageResId ?: R.drawable.property1),
                contentDescription = "Immagine proprietà: $price",
                contentScale = ContentScale.Crop,
                modifier = imgModifier
            )
        }
    }

    Card(
        modifier = modifier.then(cardClickModifier),
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = elevationDp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        if (horizontalMode) {
            // --- LAYOUT ORIZZONTALE ---
            Row(modifier = Modifier.fillMaxSize()) {
                imageContent(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(imageWidthHorizontalRatio)
                        .clip(RoundedCornerShape(topStart = Dimensions.cornerRadiusMedium, bottomStart = Dimensions.cornerRadiusMedium))
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(Dimensions.paddingSmall),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = price,
                        style = typography.titleMedium,
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    address?.let {
                        Text(
                            text = it, style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = Dimensions.spacingExtraSmall)
                        )
                    }
                    if (details.isNotEmpty()) {
                        Text(
                            text = details.joinToString(" • "), style = typography.labelSmall,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = Dimensions.spacingExtraSmall)
                        )
                    }
                }
            }
        } else {
            // --- LAYOUT VERTICALE (Default) ---
            Column(modifier = Modifier.fillMaxSize()) {
                imageContent(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(imageHeightVerticalRatio)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = Dimensions.paddingMedium, vertical = Dimensions.paddingSmall)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = price,
                            style = typography.titleLarge,
                            color = colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .padding(end = Dimensions.spacingSmall)
                        )
                        actionButton?.invoke()
                    }

                    address?.let {
                        Text(
                            text = it,
                            style = typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = Dimensions.spacingSmall)
                        )
                    }

                    if (details.isNotEmpty()) {
                        Text(
                            text = details.joinToString("  •  "),
                            style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = Dimensions.spacingExtraSmall)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyPreviewInfoWindow(
    property: PropertyMarker,
    onClick: () -> Unit,
    onClose: () -> Unit,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = dimensions.elevationLarge,
                shape = RoundedCornerShape(dimensions.cornerRadiusLarge),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cornerRadiusLarge),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationNone)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = property.imageRes),
                    contentDescription = "Immagine proprietà",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensions.imagePrewiev)
                        .clip(RoundedCornerShape(topStart = dimensions.cornerRadiusLarge, topEnd = dimensions.cornerRadiusLarge)),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(dimensions.paddingSmall)
                        .size(dimensions.spacingExtraLarge)
                        .background(
                            color = colorScheme.onBackground.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = colorScheme.background,
                        modifier = Modifier.size(dimensions.iconSizeSmall)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(dimensions.paddingMedium),
                    shape = RoundedCornerShape(dimensions.cornerRadiusSmall),
                    color = colorScheme.primary,
                    shadowElevation = dimensions.elevationSmall
                ) {
                    Text(
                        text = property.price,
                        style = typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = dimensions.paddingSmall, vertical = dimensions.paddingExtraSmall)
                    )
                }
            }

            // Contenuto della card
            Column(
                modifier = Modifier.padding(dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
            ) {
                // Titolo e tipo
                Text(
                    text = property.title,
                    style = typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = property.type,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                // Descrizione
                if (property.description.isNotEmpty()) {
                    Text(
                        text = property.description,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Caratteristiche
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (property.surface.isNotEmpty()) {
                        PropertyFeature(
                            icon = Icons.Default.SquareFoot,
                            text = property.surface,
                            colorScheme = colorScheme,
                            typography = typography,
                            dimensions = dimensions
                        )
                    }

                    if (property.bathrooms > 0) {
                        PropertyFeature(
                            icon = Icons.Default.Bathtub,
                            text = "${property.bathrooms}",
                            colorScheme = colorScheme,
                            typography = typography,
                            dimensions = dimensions
                        )
                    }

                    if (property.bedrooms > 0) {
                        PropertyFeature(
                            icon = Icons.Default.Home,
                            text = "${property.bedrooms}",
                            colorScheme = colorScheme,
                            typography = typography,
                            dimensions = dimensions
                        )
                    }
                }

                // Pulsante "Visualizza dettagli"
                AppPrimaryButton(
                    text = "Visualizza dettagli",
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().padding(top = dimensions.paddingMedium)
                )
            }
        }
    }
}

@Composable
private fun PropertyFeature(
    icon: ImageVector,
    text: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingExtraSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(dimensions.iconSizeSmall)
        )
        Text(
            text = text,
            style = typography.bodySmall,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyPreviewInfoWindowPreview() {
    DietiEstatesTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            PropertyPreviewInfoWindow(
                property = PropertyMarker(
                    id = "1",
                    position = LatLng(40.8518, 14.2681),
                    title = "Appartamento Centro Storico",
                    price = "€850/mese",
                    type = "2 locali",
                    imageRes = PropertyMarker.getPropertyImage("1"), // Usa property1
                    description = "Splendido appartamento nel cuore del centro storico di Napoli con vista panoramica",
                    surface = "85 m²",
                    bathrooms = 1,
                    bedrooms = 2,
                    imageUrl = null,
                    purchaseType = "Vendita",
                    address = "Via Roma, 123",
                    condition = "Nuovo",
                    isAvailable = true,
                    priceValue = 300.0F,
                    surfaceValue = 300.0F
                ),
                onClick = { },
                onClose = { },
                dimensions = Dimensions,
                typography = MaterialTheme.typography,
                colorScheme = MaterialTheme.colorScheme
            )
        }
    }
}