package com.dieti.dietiestates25.ui.components


// Importa i modelli dal package corretto (ui.model)
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
import androidx.compose.material.icons.automirrored.filled.More // Per BADGE notifica
import androidx.compose.material.icons.filled.Person // Per PERSON notifica
import androidx.compose.material.icons.filled.Phone // Per PHONE notifica
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
import androidx.compose.ui.unit.dp // Mantieni per valori hardcoded non in Dimensions
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.dieti.dietiestates25.ui.theme.Dimensions


@Composable
fun NotificationCard(
    notification: Notification,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
    dimensions: Dimensions = Dimensions // Aggiunto per usare i valori di Dimensions
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), // 16.dp non è dimensions.cornerRadiusMedium (12dp) o Large (24dp). Lasciato.
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall) // SOSTITUITO 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium), // SOSTITUITO 16.dp
            verticalAlignment = Alignment.Top
        ) {
            NotificationCardIcon(
                iconType = notification.iconType,
                colorScheme = colorScheme,
                dimensions = dimensions // Passa dimensions
            )

            Spacer(modifier = Modifier.width(dimensions.spacingMedium)) // SOSTITUITO 16.dp

            NotificationCardContent(
                modifier = Modifier.weight(1f),
                senderType = notification.senderType,
                message = notification.message,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions // Passa dimensions
            )

            Spacer(modifier = Modifier.width(12.dp)) // 12.dp non in Dimensions.spacing*, lasciato.

            NotificationCardActions(
                date = notification.date.format(dateFormatter),
                isFavorite = notification.isFavorite,
                onToggleFavorite = onToggleFavorite,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions // Passa dimensions
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
            .size(dimensions.iconSizeExtraLarge) // SOSTITUITO 48.dp
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)) // SOSTITUITO 12.dp
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
            modifier = Modifier.size(dimensions.iconSizeMedium), // SOSTITUITO 24.dp
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
        Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall)) // SOSTITUITO 4.dp
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
            modifier = Modifier.size(dimensions.iconSizeLarge) // SOSTITUITO 32.dp con 36.dp (valore più vicino in Dimensions)
            // oppure dimensions.paddingExtraLarge (32.dp) se si preferisce un match esatto di valore
            // Ho scelto iconSizeLarge perché semanticamente più corretto per un'icona.
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Aggiungi ai preferiti",
                tint = if (isFavorite) Color(0xFFFFC107) else colorScheme.onSurfaceVariant.copy(
                    alpha = 0.7f
                ),
                modifier = Modifier.size(22.dp) // 22.dp non in Dimensions.iconSize*, lasciato. (iconSizeMedium è 24.dp)
            )
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme, // Default aggiunto
    typography: Typography = MaterialTheme.typography,   // Default aggiunto
    dimensions: Dimensions = Dimensions
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), // Lasciato 16.dp
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall) // SOSTITUITO 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium), // SOSTITUITO 16.dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppointmentCardIcon(
                iconType = appointment.iconType,
                colorScheme = colorScheme,
                dimensions = dimensions // Passa dimensions
            )

            Spacer(modifier = Modifier.width(dimensions.spacingMedium)) // SOSTITUITO 16.dp

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
                    Spacer(modifier = Modifier.height(2.dp)) // 2.dp non in Dimensions, lasciato.
                    Text(
                        text = it,
                        style = typography.bodySmall,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp)) // 12.dp non in Dimensions, lasciato.

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = appointment.date.format(dateFormatter),
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp)) // 2.dp non in Dimensions, lasciato.
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
            .size(dimensions.iconSizeExtraLarge) // SOSTITUITO 48.dp
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)) // SOSTITUITO 12.dp
            .background(colorScheme.primaryContainer), // Cambiato da secondaryContainer per coerenza se è un'azione primaria
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (iconType) {
                AppointmentIconType.VISIT -> Icons.Filled.Event
                AppointmentIconType.MEETING -> Icons.Filled.BusinessCenter
                AppointmentIconType.GENERIC -> Icons.Filled.Build
            },
            contentDescription = "Appointment Icon",
            modifier = Modifier.size(dimensions.iconSizeMedium), // SOSTITUITO 24.dp
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
                    // Riga 1: Prezzo e Pulsante Azione
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

                    // Riga 2: Indirizzo (se presente)
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

                    // Riga 3: Altri dettagli (tipo, mq, bagni)
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