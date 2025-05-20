package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.components.NotificationCard
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.components.AppointmentCard
import com.dieti.dietiestates25.ui.components.CircularIconActionButton

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

import java.time.LocalDate
import java.util.Locale

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notifications

// Data classes e Enums (invariate, ma assicurati siano accessibili)
data class Notification(
    val id: Int,
    val senderType: String,
    val message: String,
    val iconType: NotificationIconType,
    val date: LocalDate,
    var isFavorite: Boolean = false,
)

enum class NotificationIconType {
    PHONE, PERSON, BADGE
}

data class Appointment(
    val id: Int,
    val title: String,
    val description: String?,
    val iconType: AppointmentIconType,
    val date: LocalDate,
    val timeSlot: String,
)

enum class AppointmentIconType {
    VISIT, MEETING, GENERIC
}

val TimeSlots = listOf("9-12", "12-14", "14-17", "17-20")

class NotificationsViewModel : ViewModel() {
    enum class NotificationTab {
        RECENTI, VECCHIE, SALVATE
    }

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val allNotifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _currentTab = MutableStateFlow(NotificationTab.RECENTI)
    val currentTab: StateFlow<NotificationTab> = _currentTab.asStateFlow()

    private val _favoriteNotifications = MutableStateFlow<List<Notification>>(emptyList())

    val filteredNotifications: StateFlow<List<Notification>> =
        combine(_currentTab, _notifications, _favoriteNotifications) { tab, allNots, favNots ->
            val today = LocalDate.now()
            val fiveDaysAgo = today.minusDays(5)
            when (tab) {
                NotificationTab.RECENTI -> allNots
                    .filter { !it.date.isBefore(fiveDaysAgo) }
                    .sortedByDescending { it.date }
                NotificationTab.VECCHIE -> allNots
                    .sortedByDescending { it.date }
                NotificationTab.SALVATE -> favNots
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _isShowingAppointments = MutableStateFlow(false)
    val isShowingAppointments: StateFlow<Boolean> = _isShowingAppointments.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    init {
        loadInitialNotifications()
        loadInitialAppointments()
    }

    private fun loadInitialNotifications() {
        val today = LocalDate.now()
        val initialList = listOf(
            Notification(1, "Venditore", "C'è una proposta per te, affrettati a rispondere!", NotificationIconType.PHONE, today.minusDays(Random.nextInt(0, 3).toLong()), true),
            Notification(2, "Compratore", "Richiesta informazioni per l'immobile in Via Toledo.", NotificationIconType.PERSON, today.minusDays(Random.nextInt(1, 4).toLong()), false),
            Notification(3, "Agenzia", "Nuovo immobile disponibile nella tua zona di ricerca.", NotificationIconType.BADGE, today.minusDays(Random.nextInt(0, 2).toLong()), true),
            Notification(4, "Broker", "Visita confermata per domani alle 10:30.", NotificationIconType.PERSON, today.minusDays(Random.nextInt(4, 7).toLong()), false),
            Notification(5, "Sistema", "Aggiornamento importante della policy sulla privacy.", NotificationIconType.BADGE, today.minusDays(Random.nextInt(10, 20).toLong()), false),
            Notification(6, "Venditore", "Offerta ricevuta per l'appartamento in Via Roma.", NotificationIconType.PHONE, today, false),
            Notification(7, "Compratore", "Vorrei fissare un appuntamento.", NotificationIconType.PERSON, today.minusDays(6), false),
            Notification(8, "Agenzia", "Promozione estiva: sconti sulle commissioni!", NotificationIconType.BADGE, today.minusDays(10), false)
        )
        _notifications.value = initialList.sortedByDescending { it.date }
        updateFavorites()
    }

    fun setCurrentTab(tab: NotificationTab) {
        _currentTab.value = tab
    }

    fun toggleFavorite(notificationId: Int) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isFavorite = !it.isFavorite) else it
        }.sortedByDescending { it.date }
        updateFavorites()
    }

    private fun updateFavorites() {
        _favoriteNotifications.value = _notifications.value
            .filter { it.isFavorite }
            .sortedByDescending { it.date }
    }

    fun toggleAppointmentsView() {
        _isShowingAppointments.value = !_isShowingAppointments.value
    }

    private fun loadInitialAppointments() {
        val today = LocalDate.now()
        _appointments.value = listOf(
            Appointment(1, "Visita Via Toledo", "Cliente: Paolo Bianchi", AppointmentIconType.VISIT, today.plusDays(1), TimeSlots[0]),
            Appointment(2, "Incontro con Agenzia", "Discussione nuove proposte", AppointmentIconType.MEETING, today.plusDays(2), TimeSlots[2]),
            Appointment(3, "Sopralluogo tecnico", "Viale Kennedy, 100", AppointmentIconType.GENERIC, today.plusDays(1), TimeSlots[1]),
            Appointment(4, "Chiamata con Notaio", "Definizione contratto", AppointmentIconType.MEETING, today.plusDays(3), TimeSlots[3])
        ).sortedBy { it.date }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: NotificationsViewModel = viewModel(),
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val dimensions = Dimensions // Istanza locale per accesso breve

        val currentTab by viewModel.currentTab.collectAsState()
        val notifications by viewModel.filteredNotifications.collectAsState()
        val isShowingAppointments by viewModel.isShowingAppointments.collectAsState()
        val appointments by viewModel.appointments.collectAsState()

        Scaffold(
            topBar = {
                NotificationScreenHeader(
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions, // Passa dimensions
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
                        },
                        colorScheme = colorScheme,
                        typography = typography,
                        dimensions = dimensions // Passa dimensions
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
                        colorScheme = colorScheme,
                        typography = typography,
                        dimensions = dimensions // Passa dimensions
                    )
                }
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
                top = dimensions.paddingLarge, // SOSTITUITO 25.dp con paddingLarge (24.dp) per coerenza
                bottom = dimensions.paddingLarge,
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
                    size = 60.dp, // Valore specifico, non in Dimensions.iconSize*
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
                // buttonSize = 40.dp (default), iconSize = 24.dp (dimensions.iconSizeMedium, default)
                iconSize = dimensions.iconSizeMedium // Esplicito per chiarezza
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
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NotificationTabs(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions // Passa dimensions
        )
        if (notifications.isEmpty()) {
            EmptyNotificationsView(
                modifier = Modifier.weight(1f),
                colorScheme = colorScheme,
                typography = typography,
                message = "Nessuna notifica da mostrare.",
                dimensions = dimensions // Passa dimensions
            )
        } else {
            NotificationsList(
                modifier = Modifier.weight(1f),
                notifications = notifications,
                onToggleFavorite = onToggleFavorite,
                onNotificationClick = onNotificationClick,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions // Passa dimensions
            )
        }
    }
}

@Composable
private fun AppointmentsView(
    appointments: List<Appointment>,
    onAppointmentClick: (Appointment) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensions.paddingMedium) // SOSTITUITO 16.dp
    ) {
        if (appointments.isEmpty()) {
            EmptyNotificationsView(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                colorScheme = colorScheme,
                typography = typography,
                message = "Nessun appuntamento programmato.",
                dimensions = dimensions // Passa dimensions
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = dimensions.paddingMedium, // SOSTITUITO 16.dp
                    vertical = 12.dp // 12.dp non in Dimensions, lasciato invariato
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp) // 12.dp non in Dimensions, lasciato invariato
            ) {
                items(appointments, key = { it.id }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onClick = { onAppointmentClick(appointment) },
                        colorScheme = colorScheme,
                        typography = typography
                        // AppointmentCard userà i default per dimensioni o le sue props specifiche
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationsView(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    typography: Typography,
    message: String,
    dimensions: Dimensions // Aggiunto
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium), // SOSTITUITO 16.dp
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
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = dimensions.paddingMedium, // SOSTITUITO 16.dp
            vertical = 12.dp // 12.dp non in Dimensions, lasciato invariato
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp) // 12.dp non in Dimensions, lasciato invariato
    ) {
        items(notifications, key = { it.id }) { notification ->
            NotificationCard(
                notification = notification,
                onToggleFavorite = { onToggleFavorite(notification.id) },
                onClick = { onNotificationClick(notification) },
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
    dimensions: Dimensions // Aggiunto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensions.paddingMedium, // SOSTITUITO 16.dp
                vertical = dimensions.paddingMedium   // SOSTITUITO 16.dp
            )
            .height(52.dp) // 52.dp non in Dimensions (buttonHeight è 56.dp), lasciato invariato
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge)) // SOSTITUITO 26.dp con 24.dp
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingExtraSmall), // SOSTITUITO 4.dp
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
                dimensions = dimensions // Passa dimensions
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
    dimensions: Dimensions // Aggiunto
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 2.dp) // 2.dp non in Dimensions, lasciato invariato
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge)) // SOSTITUITO 22.dp con 24.dp
            .background(if (isSelected) colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp), // 12.dp non in Dimensions, lasciato invariato
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