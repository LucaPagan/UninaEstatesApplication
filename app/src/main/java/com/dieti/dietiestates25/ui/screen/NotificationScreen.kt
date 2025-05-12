package com.dieti.dietiestates25.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.More
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.theme.TealVibrant
import com.dieti.dietiestates25.ui.theme.TealLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data class to represent a notification
data class Notification(
    val id: Int,
    val senderType: String,
    val message: String,
    val iconType: NotificationIconType,
    var isFavorite: Boolean = false
)

// Icon types for notifications
enum class NotificationIconType {
    PHONE, PERSON, BADGE
}

// ViewModel to manage notifications
class NotificationsViewModel : ViewModel() {
    // Main list of notifications
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications = _notifications.asStateFlow()

    // Currently selected tab
    private val _currentTab = MutableStateFlow(NotificationTab.RECENT)
    val currentTab = _currentTab.asStateFlow()

    // Favorites list (will be shared between screens)
    private val _favoriteNotifications = MutableStateFlow<List<Notification>>(emptyList())
    val favoriteNotifications = _favoriteNotifications.asStateFlow()

    // Enum for notification tabs
    enum class NotificationTab {
        RECENT, OLD, SAVED
    }

    init {
        // Initialize with sample data
        _notifications.value = listOf(
            Notification(1, "Venditore", "C'è una proposta per te", NotificationIconType.PHONE, true),
            Notification(2, "Venditore", "C'è una proposta per te", NotificationIconType.PHONE, true),
            Notification(3, "Agenzia", "C'è una proposta per te", NotificationIconType.PERSON, true),
            Notification(4, "Broker", "Visita confermata", NotificationIconType.BADGE, false)
        )
        updateFavorites()
    }

    // Change the current tab
    fun setCurrentTab(tab: NotificationTab) {
        _currentTab.value = tab
    }

    // Toggle favorite status
    fun toggleFavorite(notificationId: Int) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        updateFavorites()
    }

    // Update the favorites list based on the main list
    private fun updateFavorites() {
        _favoriteNotifications.value = _notifications.value.filter { it.isFavorite }
    }

    // Get notifications filtered by current tab
    fun getFilteredNotifications(): List<Notification> {
        return when (_currentTab.value) {
            NotificationTab.RECENT -> _notifications.value
            NotificationTab.OLD -> emptyList() // Placeholder, implement according to your needs
            NotificationTab.SAVED -> _favoriteNotifications.value
        }
    }
}

@Composable
fun NotificationScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: NotificationsViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val notifications = viewModel.getFilteredNotifications()

    Scaffold(
        bottomBar = {
            BottomNavigation(navController, idUtente)
        }
    ) { paddingValues ->
        // Top App Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { /* Go back */ },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Notifiche",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            IconButton(
                onClick = { /* Open menu */ },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        }

        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
        ) {
            Column {
                // Tab selectors
                NotificationTabs(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.setCurrentTab(it) }
                )

                // Notification list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onToggleFavorite = { viewModel.toggleFavorite(notification.id) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationTabs(
    currentTab: NotificationsViewModel.NotificationTab,
    onTabSelected: (NotificationsViewModel.NotificationTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(TealVibrant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TabButton(
            text = "Recenti",
            isSelected = currentTab == NotificationsViewModel.NotificationTab.RECENT,
            onClick = { onTabSelected(NotificationsViewModel.NotificationTab.RECENT) },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = "Vecchie",
            isSelected = currentTab == NotificationsViewModel.NotificationTab.OLD,
            onClick = { onTabSelected(NotificationsViewModel.NotificationTab.OLD) },
            modifier = Modifier.weight(1f)
        )

        TabButton(
            text = "Salvate",
            isSelected = currentTab == NotificationsViewModel.NotificationTab.SAVED,
            onClick = { onTabSelected(NotificationsViewModel.NotificationTab.SAVED) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(if (isSelected) TealLight else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) Color.Black else Color.White
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = TealVibrant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.iconType) {
                        NotificationIconType.PHONE -> Icons.Default.Phone
                        NotificationIconType.PERSON -> Icons.Default.Person
                        NotificationIconType.BADGE -> Icons.Default.More
                    },
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = notification.senderType,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            // Favorite icon
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (notification.isFavorite)
                        Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Toggle Favorite",
                    tint = if (notification.isFavorite) Color(0xFFFFB74D) else Color.White
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController, idUtente: String) {
    // Using Material 3 NavigationBar and NavigationBarItem
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary // Primary color for bottom bar
    ) {
        // Use weight to distribute items evenly
        AddItem(
            icon = Icons.Default.Home,
            label = "Esplora",
            selected = true, // Indicate this is the current screen
            onClick = {
                // Already on Home, can add logic to scroll to top if needed
            },
            colorScheme = MaterialTheme.colorScheme // Pass color scheme
        )

        AddItem(
            icon = Icons.Default.Notifications,
            label = "Notifiche",
            selected = false,
            onClick = {
                // TODO: Navigate to notifications screen
                // navController.navigate(Screen.NotificationsScreen.route)
            },
            colorScheme = MaterialTheme.colorScheme // Pass color scheme
        )

        AddItem(
            icon = Icons.Default.Person,
            label = "Profilo",
            selected = false,
            onClick = {
                // TODO: Navigate to profile screen
                // navController.navigate(Screen.ProfileScreen.route)
            },
            colorScheme = MaterialTheme.colorScheme // Pass color scheme
        )
    }
}

@Composable
fun RowScope.AddItem( // Extension function to use weight
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme // Receive color scheme
) {
    NavigationBarItem(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
            )
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = colorScheme.onPrimary, // Icon color when selected
            selectedTextColor = colorScheme.onPrimary, // Text color when selected
            unselectedIconColor = colorScheme.onPrimary.copy(alpha = 0.6f), // Icon color when unselected
            unselectedTextColor = colorScheme.onPrimary.copy(alpha = 0.6f), // Text color when unselected
            indicatorColor = colorScheme.primaryContainer // Indicator color (optional, can be set to transparent)
        ),
        modifier = Modifier.weight(1f) // Distribute space evenly
    )
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    val navController = rememberNavController()

    NotificationScreen(
        navController = TODO(),
        idUtente = TODO(),
        viewModel = TODO()
    )

}