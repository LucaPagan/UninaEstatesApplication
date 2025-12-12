package com.dieti.dietiestates25.ui.features.notification

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.data.model.Appointment
import com.dieti.dietiestates25.data.model.Notification
import com.dieti.dietiestates25.ui.components.AppAppointmentDisplay
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppEmptyDisplayView
import com.dieti.dietiestates25.ui.components.AppNotificationDisplay
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.SessionManager
import java.util.Locale

@Composable
fun NotificationScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: NotificationViewModel = viewModel(), // Corretto nome classe VM
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    // Carica i dati all'avvio della schermata
    LaunchedEffect(Unit) {
        val actualUserId = if (idUtente == "sconosciuto" || idUtente.isBlank()) {
            SessionManager.getUserId(context) ?: ""
        } else {
            idUtente
        }

        if (actualUserId.isNotEmpty()) {
            viewModel.loadData(context)
        }
    }

    val currentTab by viewModel.currentTab.collectAsState()
    val notifications by viewModel.filteredNotifications.collectAsState()
    val isShowingAppointments by viewModel.isShowingAppointments.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Configurazione dell'action button dinamica
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
                onActionClick = viewModel::toggleAppointmentsView,
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
        Box (
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
                        onAppointmentClick = { appointment ->
                            navController.navigate(Screen.AppointmentDetailScreen.route)
                        },
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                } else {
                    NotificationScreenContent(
                        currentTab = currentTab,
                        notifications = notifications,
                        onTabSelected = viewModel::setCurrentTab,
                        onToggleFavorite = viewModel::toggleFavorite,
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
    currentTab: NotificationViewModel.NotificationTab,
    notifications: List<Notification>,
    onTabSelected: (NotificationViewModel.NotificationTab) -> Unit,
    onToggleFavorite: (Int) -> Unit,
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
            AppEmptyDisplayView(
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
            AppEmptyDisplayView(
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
                    AppAppointmentDisplay(
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
            vertical = dimensions.paddingSmall
        ),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
    ) {
        items(notifications, key = { it.id }) { notification ->
            AppNotificationDisplay(
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

@Composable
fun NotificationTabs(
    currentTab: NotificationViewModel.NotificationTab,
    onTabSelected: (NotificationViewModel.NotificationTab) -> Unit,
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
        val tabs = NotificationViewModel.NotificationTab.entries.toTypedArray()
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

@Preview(showBackground = true, name = "Notification Screen Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Notification Screen Dark")
@Composable
fun NotificationScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = remember {
        NotificationViewModel().apply {}
    }
    NotificationScreen(
        navController = navController,
        idUtente = "previewUser123",
        viewModel = previewViewModel
    )
}