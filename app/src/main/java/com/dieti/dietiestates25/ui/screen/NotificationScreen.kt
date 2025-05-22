package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.components.CircularIconActionButton

// Importa i modelli e il ViewModel dal nuovo package model
import com.dieti.dietiestates25.ui.model.Notification
import com.dieti.dietiestates25.ui.model.Appointment
import com.dieti.dietiestates25.ui.model.NotificationsViewModel

import com.dieti.dietiestates25.ui.components.AppNotificationDisplay
import com.dieti.dietiestates25.ui.components.AppAppointmentDisplay

import java.util.Locale // Già presente

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Già presente in TabButton
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color // Già presente in TabButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Già presente
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: NotificationsViewModel = viewModel(), // viewModel ora è importato da model
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val currentTab by viewModel.currentTab.collectAsState()
    val notifications by viewModel.filteredNotifications.collectAsState()
    val isShowingAppointments by viewModel.isShowingAppointments.collectAsState()
    val appointments by viewModel.appointments.collectAsState()

    Scaffold(
        topBar = {
            NotificationScreenHeader(
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions,
                isShowingAppointments = isShowingAppointments,
                onCalendarIconClick = viewModel::toggleAppointmentsView
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController, idUtente = idUtente)
        }
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isShowingAppointments) {
                AppointmentsView(
                    appointments = appointments,
                    onAppointmentClick = { appointment ->
                        println("Clicked appointment: ${appointment.title}")
                        // Es. navController.navigate(Screen.AppointmentDetailScreen.withId(appointment.id))
                    },
                    dimensions = dimensions, // Passa dimensions
                    colorScheme = colorScheme, // Passa se AppAppointmentDisplay non usa il default
                    typography = typography  // Passa se AppAppointmentDisplay non usa il default
                )
            } else {
                NotificationScreenContent(
                    currentTab = currentTab,
                    notifications = notifications,
                    onTabSelected = viewModel::setCurrentTab,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onNotificationClick = { notification ->
                        navController.navigate(Screen.NotificationDetailScreen.route)
                    },
                    dimensions = dimensions, // Passa dimensions
                    colorScheme = colorScheme,
                    typography = typography
                )
            }
        }
    }
}


@Composable
private fun NotificationScreenHeader(
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    isShowingAppointments: Boolean,
    onCalendarIconClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(colorScheme.primary)
            .clip(RoundedCornerShape(bottomStart = dimensions.cornerRadiusLarge, bottomEnd = dimensions.cornerRadiusLarge))
            .padding(horizontal = dimensions.paddingLarge)
            .padding(
                top = dimensions.paddingLarge, // Usando dimensions
                bottom = dimensions.paddingLarge
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AppIconDisplay(
                    size = 60.dp, // Valore specifico per questo design
                    shapeRadius = dimensions.cornerRadiusMedium
                )
                Spacer(modifier = Modifier.width(dimensions.spacingMedium))
                Text(
                    text = if (isShowingAppointments) "Appuntamenti" else "Notifiche",
                    color = colorScheme.onPrimary,
                    style = typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            val iconVector = if (isShowingAppointments) Icons.Filled.Notifications else Icons.Filled.CalendarToday
            val contentDesc = if (isShowingAppointments) "Mostra Notifiche" else "Mostra Appuntamenti"
            CircularIconActionButton(
                onClick = onCalendarIconClick,
                iconVector = iconVector,
                contentDescription = contentDesc,
                backgroundColor = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer,
                iconSize = dimensions.iconSizeMedium
            )
        }
    }
}

@Composable
private fun NotificationScreenContent(
    currentTab: NotificationsViewModel.NotificationTab,
    notifications: List<Notification>,
    onTabSelected: (NotificationsViewModel.NotificationTab) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onNotificationClick: (Notification) -> Unit,
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
            EmptyDisplayView( // Rinominato per generalità
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
                onNotificationClick = onNotificationClick,
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
private fun AppointmentsView(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
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
            EmptyDisplayView( // Riutilizza EmptyDisplayView
                modifier = Modifier.weight(1f).fillMaxWidth(),
                message = "Nessun appuntamento programmato.",
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = dimensions.paddingMedium,
                    vertical = 12.dp // Lasciato 12.dp (non in Dimensions)
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Lasciato 12.dp (non in Dimensions)
            ) {
                items(appointments, key = { it.id }) { appointment ->
                    AppAppointmentDisplay( // Usa il nuovo componente da AppNotification.kt
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
private fun EmptyDisplayView( // Rinominato da EmptyNotificationsView
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

@Composable
private fun NotificationsList(
    modifier: Modifier = Modifier,
    notifications: List<Notification>,
    onToggleFavorite: (Int) -> Unit,
    onNotificationClick: (Notification) -> Unit,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = dimensions.paddingMedium,
            vertical = 12.dp // Lasciato 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Lasciato 12.dp
    ) {
        items(notifications, key = { it.id }) { notification ->
            AppNotificationDisplay( // Usa il nuovo componente da AppNotification.kt
                notification = notification,
                onToggleFavorite = onToggleFavorite, // Già accetta (Int)
                onClick = { onNotificationClick(notification) },
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
fun NotificationTabs(
    currentTab: NotificationsViewModel.NotificationTab,
    onTabSelected: (NotificationsViewModel.NotificationTab) -> Unit,
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
            .height(52.dp) // Lasciato 52.dp
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingExtraSmall),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = NotificationsViewModel.NotificationTab.entries.toTypedArray()
        tabs.forEach { tab ->
            TabButton(
                text = tab.name.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
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
            .padding(horizontal = 2.dp) // Lasciato 2.dp
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge)) // Usa dimensions
            .background(if (isSelected) colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp), // Lasciato 12.dp
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

@Preview(showBackground = true, name = "Notification Screen Light")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Notification Screen Dark")
@Composable
fun NotificationScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = remember {
        NotificationsViewModel().apply {}
    }
    DietiEstatesTheme {
        NotificationScreen(
            navController = navController,
            idUtente = "previewUser123",
            viewModel = previewViewModel
        )
    }
}