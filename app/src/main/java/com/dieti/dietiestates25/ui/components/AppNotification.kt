package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More // Per BADGE notifica
import androidx.compose.material.icons.filled.BusinessCenter // Per MEETING appuntamento
import androidx.compose.material.icons.filled.Build // Per GENERIC appuntamento
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event // Per VISIT appuntamento
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.dieti.dietiestates25.ui.model.Appointment // Importa dal package model
import com.dieti.dietiestates25.ui.model.AppointmentIconType // Importa dal package model
import com.dieti.dietiestates25.ui.model.Notification // Importa dal package model
import com.dieti.dietiestates25.ui.model.NotificationIconType // Importa dal package model
import com.dieti.dietiestates25.ui.theme.Dimensions // Assumendo che Dimensions sia accessibile
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Componente generalizzato per visualizzare un item in una lista,
 * adatto per notifiche e appuntamenti.
 */
@Composable
fun AppInfoItemCard( // Nome più generico
    onClick: () -> Unit,
    icon: ImageVector,
    iconBackgroundColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String?, // Sottotitolo opzionale
    trailingTopText: String,
    trailingBottomContent: @Composable ColumnScope.() -> Unit, // Contenuto personalizzabile in basso a destra
    modifier: Modifier = Modifier,
    dimensions: Dimensions = Dimensions, // Usa valori di default da Dimensions
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium), // Usa dimensions
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall) // Usa dimensions
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingMedium), // Usa dimensions
            verticalAlignment = Alignment.Top // Per allineare bene il contenuto a destra
        ) {
            // Icona
            Box(
                modifier = Modifier
                    .size(dimensions.iconSizeExtraLarge) // Usa dimensions (48.dp)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)) // Usa dimensions
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    modifier = Modifier.size(dimensions.iconSizeMedium), // Usa dimensions (24.dp)
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(dimensions.spacingMedium)) // Usa dimensions

            // Contenuto Testuale Principale
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onSurface,
                    maxLines = 1, // Titolo su una riga
                    overflow = TextOverflow.Ellipsis
                )
                subtitle?.let {
                    Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall)) // Usa dimensions
                    Text(
                        text = it,
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 2, // Sottotitolo/messaggio su max 2 righe
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(dimensions.spacingSmall)) // Usa dimensions

            // Contenuto a Destra (Trailing)
            Column(
                horizontalAlignment = Alignment.End,
                // Usa IntrinsicSize.Min per adattare l'altezza o definisci un'altezza fissa minima
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    text = trailingTopText, // Es. Data
                    style = typography.labelSmall,
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                // Contenuto inferiore (es. stella o fascia oraria)
                // Spacer per spingere il contenuto in basso se la data è corta e il contenuto ha altezza variabile
                Spacer(modifier = Modifier.weight(1f, fill = false))
                trailingBottomContent()
            }
        }
    }
}

// Specializzazione di AppInfoItemCard per le Notifiche
@Composable
fun AppNotificationDisplay(
    notification: Notification,
    onToggleFavorite: (Int) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dimensions: Dimensions = Dimensions,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }
    val notificationIcon = when (notification.iconType) {
        NotificationIconType.PHONE -> Icons.Filled.Phone
        NotificationIconType.PERSON -> Icons.Filled.Person
        NotificationIconType.BADGE -> Icons.AutoMirrored.Filled.More
    }

    AppInfoItemCard(
        onClick = onClick,
        icon = notificationIcon,
        iconBackgroundColor = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer,
        title = notification.senderType,
        subtitle = notification.message,
        trailingTopText = notification.date.format(dateFormatter),
        trailingBottomContent = {
            CircularIconActionButton(
                onClick = { onToggleFavorite(notification.id) },
                iconVector = if (notification.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = if (notification.isFavorite) "Rimuovi dai preferiti" else "Aggiungi ai preferiti",
                backgroundColor = colorScheme.surfaceDim,
                iconTint = if (notification.isFavorite) colorScheme.surfaceTint else colorScheme.onPrimaryContainer,
                iconModifier = Modifier.size(dimensions.iconSizeMedium)
            )
        },
        modifier = modifier,
        dimensions = dimensions,
        colorScheme = colorScheme,
        typography = typography
    )
}

// Specializzazione di AppInfoItemCard per gli Appuntamenti
@Composable
fun AppAppointmentDisplay(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dimensions: Dimensions = Dimensions,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }
    val appointmentIconImageVector = when (appointment.iconType) {
        AppointmentIconType.VISIT -> Icons.Filled.Event
        AppointmentIconType.MEETING -> Icons.Filled.BusinessCenter
        AppointmentIconType.GENERIC -> Icons.Filled.Build
        // Add an else branch to handle any other cases
        else -> Icons.Filled.Error // Or some other default/fallback icon
    }

    AppInfoItemCard(
        onClick = onClick,
        icon = appointmentIconImageVector,
        iconBackgroundColor = colorScheme.primaryContainer, // Colore diverso per appuntamenti
        iconTint = colorScheme.onPrimaryContainer,
        title = appointment.title,
        subtitle = appointment.description,
        trailingTopText = appointment.date?.format(dateFormatter) ?: "",
        trailingBottomContent = {
            Text(
                text = appointment.timeSlot,
                style = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.primary, // Evidenzia la fascia oraria
                modifier = Modifier.padding(top = dimensions.spacingExtraSmall) // Aggiungi un po' di padding se necessario
            )
        },
        modifier = modifier,
        dimensions = dimensions,
        colorScheme = colorScheme,
        typography = typography
    )
}