package com.dieti.dietiestates25.ui.features.notification

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions
import kotlinx.coroutines.delay
import java.util.Locale

// --- DATI MOCK LOCALI (Sostituiscono Model e ViewModel) ---
enum class NotificationTab {
    TUTTE, PREFERITI, NON_LETTE
}

data class NotificationMock(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val isFavorite: Boolean,
    val isRead: Boolean
)

data class AppointmentMock(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val location: String
)

@Composable
fun NotificationScreen(
    navController: NavController,
    idUtente: String = "sconosciuto"
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    // --- GESTIONE STATO LOCALE ---
    var isLoading by remember { mutableStateOf(true) }
    var isShowingAppointments by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(NotificationTab.TUTTE) }

    // Liste dati
    var notifications by remember { mutableStateOf<List<NotificationMock>>(emptyList()) }
    var appointments by remember { mutableStateOf<List<AppointmentMock>>(emptyList()) }

    // --- CARICAMENTO DATI SIMULATO ---
    LaunchedEffect(Unit) {
        isLoading = true
        delay(1500) // Simula caricamento

        notifications = listOf(
            NotificationMock("1", "Nuova proposta", "Hai ricevuto una proposta per Via Roma", "20/05/2024", false, false),
            NotificationMock("2", "Promemoria Appuntamento", "Visita confermata per domani", "19/05/2024", true, true),
            NotificationMock("3", "Aggiornamento Prezzo", "Il prezzo dell'immobile in lista preferiti è sceso", "18/05/2024", false, true),
            NotificationMock("4", "Benvenuto", "Grazie per esserti registrato a DietiEstates!", "15/05/2024", false, true)
        )

        appointments = listOf(
            AppointmentMock("1", "Visita Trilocale", "22/05/2024", "10:00", "Via Toledo 12"),
            AppointmentMock("2", "Incontro Agente", "24/05/2024", "16:30", "Sede Centrale")
        )

        isLoading = false
    }

    // --- LOGICA DI FILTRO ---
    val filteredNotifications by remember {
        derivedStateOf {
            when (currentTab) {
                NotificationTab.TUTTE -> notifications
                NotificationTab.PREFERITI -> notifications.filter { it.isFavorite }
                NotificationTab.NON_LETTE -> notifications.filter { !it.isRead }
            }
        }
    }

    // --- AZIONI ---
    fun toggleFavorite(id: String) {
        notifications = notifications.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    // Configurazione UI dinamica
    val actionIcon = if (isShowingAppointments) Icons.Filled.Notifications else Icons.Filled.CalendarToday
    val actionContentDescription = if (isShowingAppointments) "Mostra Notifiche" else "Mostra Appuntamenti"
    val screenTitle = if (isShowingAppointments) "Appuntamenti" else "Notifiche"

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = screenTitle,
                actionIcon = actionIcon,
                actionContentDescription = actionContentDescription,
                onActionClick = { isShowingAppointments = !isShowingAppointments },
                actionBackgroundColor = colorScheme.primaryContainer,
                actionIconTint = colorScheme.onPrimaryContainer,
                showAppIcon = true,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController, idUtente = idUtente)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                if (isShowingAppointments) {
                    AppointmentsScreenContent(
                        appointments = appointments,
                        onAppointmentClick = {
                            // Navigazione finta o dettaglio
                            Toast.makeText(context, "Dettaglio Appuntamento ${it.title}", Toast.LENGTH_SHORT).show()
                        },
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                } else {
                    NotificationScreenContent(
                        currentTab = currentTab,
                        notifications = filteredNotifications,
                        onTabSelected = { currentTab = it },
                        onToggleFavorite = { id -> toggleFavorite(id) },
                        navController = navController,
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationScreenContent(
    currentTab: NotificationTab,
    notifications: List<NotificationMock>,
    onTabSelected: (NotificationTab) -> Unit,
    onToggleFavorite: (String) -> Unit,
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NotificationTabs(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )
        if (notifications.isEmpty()) {
            EmptyDisplayView(
                modifier = Modifier.weight(1f),
                message = "Nessuna notifica da mostrare.",
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography
            )
        } else {
            NotificationsList(
                modifier = Modifier.weight(1f),
                notifications = notifications,
                onToggleFavorite = onToggleFavorite,
                onNotificationClick = { notification ->
                    navController.navigate(Screen.NotificationDetailScreen.createRoute(notification.id))
                },
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
private fun AppointmentsScreenContent(
    appointments: List<AppointmentMock>,
    onAppointmentClick: (AppointmentMock) -> Unit,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensions.paddingMedium)
    ) {
        if (appointments.isEmpty()) {
            EmptyDisplayView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                message = "Nessun appuntamento programmato.",
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = dimensions.paddingMedium,
                    vertical = dimensions.paddingSmall
                ),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
            ) {
                items(appointments, key = { it.id }) { appointment ->
                    AppointmentItem(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) },
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationsList(
    modifier: Modifier = Modifier,
    notifications: List<NotificationMock>,
    onToggleFavorite: (String) -> Unit,
    onNotificationClick: (NotificationMock) -> Unit,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = dimensions.paddingMedium,
            vertical = dimensions.paddingSmall
        ),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
    ) {
        items(notifications, key = { it.id }) { notification ->
            NotificationItem(
                notification = notification,
                onToggleFavorite = onToggleFavorite,
                onClick = { onNotificationClick(notification) },
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

// --- COMPONENTI UI LOCALI (Sostituiscono AppNotificationDisplay/AppAppointmentDisplay) ---

@Composable
fun NotificationItem(
    notification: NotificationMock,
    onToggleFavorite: (String) -> Unit,
    onClick: () -> Unit,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(if (notification.isRead) colorScheme.surfaceVariant else colorScheme.primaryContainer.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
            .padding(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onSurface
            )
            Text(
                text = notification.description,
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))
            Text(
                text = notification.date,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        IconButton(onClick = { onToggleFavorite(notification.id) }) {
            Icon(
                imageVector = if (notification.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Preferito",
                tint = if (notification.isFavorite) colorScheme.primary else colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AppointmentItem(
    appointment: AppointmentMock,
    onClick: () -> Unit,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(colorScheme.secondaryContainer.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = appointment.title,
                style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colorScheme.onSecondaryContainer
            )
            Text(
                text = "Luogo: ${appointment.location}",
                style = typography.bodyMedium,
                color = colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))
            Text(
                text = "${appointment.date} ore ${appointment.time}",
                style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.primary
            )
        }
        Icon(
            imageVector = Icons.Filled.CalendarToday,
            contentDescription = null,
            tint = colorScheme.primary
        )
    }
}

@Composable
fun NotificationTabs(
    currentTab: NotificationTab,
    onTabSelected: (NotificationTab) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.paddingMedium
            )
            .height(dimensions.buttonHeight)
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingExtraSmall),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = NotificationTab.entries.toTypedArray()
        tabs.forEach { tab ->
            TabButton(
                text = tab.name.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    .replace("_", " "),
                isSelected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f),
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = dimensions.tabButtonPadding)
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
            .background(if (isSelected) colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = dimensions.paddingSmall),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun EmptyDisplayView(
    modifier: Modifier = Modifier,
    message: String,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true, name = "Notification Screen Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Notification Screen Dark")
@Composable
fun NotificationScreenPreview() {
    val navController = rememberNavController()
    NotificationScreen(
        navController = navController,
        idUtente = "previewUser123"
    )
}