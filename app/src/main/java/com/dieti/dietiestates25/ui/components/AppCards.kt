package com.dieti.dietiestates25.ui.components

import com.dieti.dietiestates25.ui.model.Notification
import com.dieti.dietiestates25.ui.model.NotificationIconType
import com.dieti.dietiestates25.ui.model.Appointment
import com.dieti.dietiestates25.ui.model.AppointmentIconType
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.ui.model.PropertyMarker
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.google.android.gms.maps.model.LatLng


@Composable
fun NotificationCard(
    notification: Notification,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
    dimensions: Dimensions = Dimensions
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
            verticalAlignment = Alignment.Top
        ) {
            NotificationCardIcon(
                iconType = notification.iconType,
                colorScheme = colorScheme,
                dimensions = dimensions
            )

            Spacer(modifier = Modifier.width(dimensions.spacingMedium))

            NotificationCardContent(
                modifier = Modifier.weight(1f),
                senderType = notification.senderType,
                message = notification.message,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )

            Spacer(modifier = Modifier.width(12.dp))

            NotificationCardActions(
                date = notification.date.format(dateFormatter),
                isFavorite = notification.isFavorite,
                onToggleFavorite = onToggleFavorite,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
    }
}

@Composable
private fun NotificationCardIcon(
    iconType: NotificationIconType,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .size(dimensions.iconSizeExtraLarge)
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (iconType) {
                NotificationIconType.PHONE -> Icons.Default.Phone
                NotificationIconType.PERSON -> Icons.Default.Person
                NotificationIconType.BADGE -> Icons.AutoMirrored.Filled.More
            },
            contentDescription = "Notification Icon",
            modifier = Modifier.size(dimensions.iconSizeMedium),
            tint = colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun NotificationCardContent(
    modifier: Modifier = Modifier,
    senderType: String,
    message: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(modifier = modifier) {
        Text(
            text = senderType,
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))
        Text(
            text = message,
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            maxLines = 2
        )
    }
}

@Composable
private fun NotificationCardActions(
    date: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        Text(
            text = date,
            style = typography.labelSmall,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Spacer(
            modifier = Modifier.weight(
                1f,
                fill = false
            )
        )
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(dimensions.iconSizeLarge)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Aggiungi ai preferiti",
                tint = if (isFavorite) Color(0xFFFFC107) else colorScheme.onSurfaceVariant.copy(
                    alpha = 0.7f
                ),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
    dimensions: Dimensions = Dimensions
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppointmentCardIcon(
                iconType = appointment.iconType,
                colorScheme = colorScheme,
                dimensions = dimensions
            )

            Spacer(modifier = Modifier.width(dimensions.spacingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.title,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                appointment.description?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = it,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = appointment.date.format(dateFormatter),
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = appointment.timeSlot,
                    style = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun AppointmentCardIcon(
    iconType: AppointmentIconType,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .size(dimensions.iconSizeExtraLarge)
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (iconType) {
                AppointmentIconType.VISIT -> Icons.Filled.Event
                AppointmentIconType.MEETING -> Icons.Filled.BusinessCenter
                AppointmentIconType.GENERIC -> Icons.Filled.Build
            },
            contentDescription = "Appointment Icon",
            modifier = Modifier.size(dimensions.iconSizeMedium),
            tint = colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun AppPropertyCard(
    price: String,
    @DrawableRes imageResId: Int,
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

    val cardClickModifier = if (actionButton == null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Card(
        modifier = modifier.then(cardClickModifier),
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = elevationDp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        if (horizontalMode) {
            Row(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Immagine proprietà: $price",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
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
                    Text(text = price, style = typography.titleMedium, color = colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    address?.let {
                        Text(
                            text = it, style = typography.bodySmall,
                            color = colorScheme.onSurfaceVariant, maxLines = 1,
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
            Column(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Immagine proprietà: $price",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
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
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = property.imageRes),
                    contentDescription = "Immagine proprietà",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = colorScheme.primary,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = property.price,
                        style = typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Contenuto della card
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (property.surface.isNotEmpty()) {
                        PropertyFeature(
                            icon = Icons.Default.SquareFoot,
                            text = property.surface,
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }

                    if (property.bathrooms > 0) {
                        PropertyFeature(
                            icon = Icons.Default.Bathtub,
                            text = "${property.bathrooms}",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }

                    if (property.bedrooms > 0) {
                        PropertyFeature(
                            icon = Icons.Default.Home,
                            text = "${property.bedrooms}",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }
                }

                // Pulsante "Visualizza dettagli"
                AppPrimaryButton(
                    text = "Visualizza dettagli",
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(16.dp)
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
                .padding(16.dp)
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
                    bedrooms = 2
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