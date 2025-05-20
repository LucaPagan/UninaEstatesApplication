package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
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
import com.dieti.dietiestates25.ui.screen.Notification // Import from your screen package
import com.dieti.dietiestates25.ui.screen.NotificationIconType // Import from your screen package
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.dieti.dietiestates25.ui.screen.Appointment // Assicurati che il percorso sia corretto
import com.dieti.dietiestates25.ui.screen.AppointmentIconType // Assicurati che il percorso sia corretto
import androidx.compose.material.icons.filled.Event // Esempio di icona per appuntamenti
import androidx.compose.material.icons.filled.BusinessCenter // Esempio
import androidx.compose.material.icons.filled.Build // Esempio
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun NotificationCard(
    notification: Notification,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme = MaterialTheme.colorScheme, // Default to MaterialTheme
    typography: Typography = MaterialTheme.typography    // Default to MaterialTheme
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), // Consistent corner radius
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface // Neutral card background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Subtle elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top // Align content to the top
        ) {
            // Extracted private composables are now part of this file or passed as parameters
            NotificationCardIcon( // Renamed for clarity within AppCards
                iconType = notification.iconType,
                colorScheme = colorScheme
            )

            Spacer(modifier = Modifier.width(16.dp))

            NotificationCardContent( // Renamed
                modifier = Modifier.weight(1f),
                senderType = notification.senderType,
                message = notification.message,
                colorScheme = colorScheme,
                typography = typography
            )

            Spacer(modifier = Modifier.width(12.dp))

            NotificationCardActions( // Renamed
                date = notification.date.format(dateFormatter),
                isFavorite = notification.isFavorite,
                onToggleFavorite = onToggleFavorite,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

// Helper composables for NotificationCard (previously private in NotificationScreen.kt)
// These are now internal to the components package or could be private to AppCards.kt

@Composable
private fun NotificationCardIcon(
    iconType: NotificationIconType,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
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
            modifier = Modifier.size(24.dp),
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
    typography: Typography
) {
    Column(modifier = modifier) {
        Text(
            text = senderType,
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
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
    typography: Typography
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.height(IntrinsicSize.Min) // Adapt height to content
    ) {
        Text(
            text = date,
            style = typography.labelSmall,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        // Spacer to push IconButton down if date text is shorter,
        // or use Arrangement.Bottom if date should always be top and icon bottom of a fixed height.
        // Given IntrinsicSize.Min, SpaceBetween should work fine.
        Spacer(
            modifier = Modifier.weight(
                1f,
                fill = false
            )
        ) // Only take space if needed, don't force expansion
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp) // Standard touch target
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Aggiungi ai preferiti",
                tint = if (isFavorite) Color(0xFFFFC107) /* Amber */ else colorScheme.onSurfaceVariant.copy(
                    alpha = 0.7f
                ),
                modifier = Modifier.size(22.dp) // Icon visual size
            )
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    typography: Typography
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // Centra verticalmente tutto il contenuto della Row
        ) {
            AppointmentCardIcon( // Icona per l'appuntamento
                iconType = appointment.iconType,
                colorScheme = colorScheme
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Contenuto principale dell'appuntamento
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
                    Spacer(modifier = Modifier.height(2.dp)) // Spazio ridotto
                    Text(
                        text = it,
                        style = typography.bodySmall, // Più piccolo per la descrizione
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Data e fascia oraria dell'appuntamento
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = appointment.date.format(dateFormatter),
                    style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium), // Leggermente più grande della labelSmall
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp)) // Spazio ridotto
                Text(
                    text = appointment.timeSlot,
                    style = typography.labelLarge.copy(fontWeight = FontWeight.Bold), // Fascia oraria in grassetto
                    color = colorScheme.primary // Evidenzia la fascia oraria
                )
            }
        }
    }
}

@Composable
private fun AppointmentCardIcon(
    iconType: AppointmentIconType,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.primaryContainer), // Colore diverso per appuntamenti?
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (iconType) { // Scegli icone appropriate
                AppointmentIconType.VISIT -> Icons.Filled.Event // Esempio
                AppointmentIconType.MEETING -> Icons.Filled.BusinessCenter // Esempio
                AppointmentIconType.GENERIC -> Icons.Filled.Build // Esempio
            },
            contentDescription = "Appointment Icon",
            modifier = Modifier.size(24.dp),
            tint = colorScheme.onPrimaryContainer
        )
    }
}