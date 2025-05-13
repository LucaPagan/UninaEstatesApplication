package com.dieti.dietiestates25.ui.screen
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.More // Icona usata per BADGE
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.* // Importa tutto da Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.navigation.Screen

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
            Notification(2, "Venditore", "C'è una proposta per te", NotificationIconType.PHONE, false), // Modificato per test
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

@OptIn(ExperimentalMaterial3Api::class) // Necessario per TopAppBar
@Composable
fun NotificationScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: NotificationsViewModel = viewModel()
) {
    DietiEstatesTheme {


        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        val currentTab by viewModel.currentTab.collectAsState()
        val notifications = viewModel.getFilteredNotifications()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Notifiche")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) { // Azione per tornare indietro
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Apri menu opzioni */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TealVibrant,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            // Usa il Composable BottomNavigation definito sotto
            AppBottomNavigation(navController = navController, idUtente = idUtente)
        }
    ) { paddingValues ->
        // Applica paddingValues al contenitore principale per evitare sovrapposizioni
        Column(
            modifier = Modifier
                .padding(paddingValues) // Applicare qui i padding
                .fillMaxSize()
                .background(Color.White) // Sfondo bianco per l'area contenuto
        ) {
            // Tab selectors
            NotificationTabs(
                currentTab = currentTab,
                onTabSelected = { viewModel.setCurrentTab(it) }
            )


        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Notifiche")
                    },
                    actions = {
                        IconButton(onClick = { /* Apri menu opzioni */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary, // Usa il colore personalizzato
                        titleContentColor = colorScheme.onPrimary,
                        navigationIconContentColor = colorScheme.onPrimary,
                        actionIconContentColor = colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                // Usa il Composable BottomNavigation definito sotto
                AppBottomNavigation(navController = navController, idUtente = idUtente)
            }
        ) { paddingValues ->
            // Applica paddingValues al contenitore principale per evitare sovrapposizioni
            Column(
                modifier = Modifier
                    .padding(paddingValues) // Applicare qui i padding
                    .fillMaxSize()
                    .background(colorScheme.background) // Sfondo bianco per l'area contenuto
            ) {
                // Tab selectors
                NotificationTabs(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.setCurrentTab(it) },
                    colorScheme = colorScheme,
                    typography = typography,
                    navController = navController
                )

                // Notification list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    ) // Padding per la lista
                ) {
                    items(
                        notifications,
                        key = { it.id }) { notification -> // Aggiunta key per performance
                        NotificationItem(
                            notification = notification,
                            onToggleFavorite = { viewModel.toggleFavorite(notification.id) },
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Spazio tra item
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationTabs(
    currentTab: NotificationsViewModel.NotificationTab,
    colorScheme: ColorScheme,
    typography: Typography,
    navController: NavController,
    onTabSelected: (NotificationsViewModel.NotificationTab) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navController.navigate(Screen.NotificationDetailScreen.route) })
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp) // Padding esterno per i tabs
            .clip(RoundedCornerShape(25.dp))
            .background(colorScheme.primary)
            .padding(4.dp), // Padding interno per i bottoni
        horizontalArrangement = Arrangement.SpaceEvenly // Distribuzione uniforme
    ) {
        TabButton(
            text = "Recenti",
            isSelected = currentTab == NotificationsViewModel.NotificationTab.RECENT,
            onClick = { onTabSelected(NotificationsViewModel.NotificationTab.RECENT) },
            modifier = Modifier.weight(1f),
            colorScheme = colorScheme,
            typography = typography
        )

        TabButton(
            text = "Vecchie",
            isSelected = currentTab == NotificationsViewModel.NotificationTab.OLD,
            onClick = { onTabSelected(NotificationsViewModel.NotificationTab.OLD) },
            modifier = Modifier.weight(1f),
            colorScheme = colorScheme,
            typography = typography
        )

        TabButton(
            text = "Salvate",
            isSelected = currentTab == NotificationsViewModel.NotificationTab.SAVED,
            onClick = { onTabSelected(NotificationsViewModel.NotificationTab.SAVED) },
            modifier = Modifier.weight(1f),
            colorScheme = colorScheme,
            typography = typography
        )
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
) {
    Box(
        modifier = modifier
            .padding(horizontal = 4.dp) // Spazio tra i bottoni
            .clip(RoundedCornerShape(25.dp))
            .background(if (isSelected) colorScheme.secondary else colorScheme.surfaceDim)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = if (isSelected) typography.labelLarge.copy(fontWeight = FontWeight.Bold) else typography.labelLarge,
            color = if (isSelected) colorScheme.onSecondary else colorScheme.onPrimary,
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    colorScheme: ColorScheme,
    typography: Typography,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.primary // Sfondo della card
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(64.dp) // Leggermente ridotto per bilanciare
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.secondary)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.iconType) {
                        NotificationIconType.PHONE -> Icons.Default.Phone
                        NotificationIconType.PERSON -> Icons.Default.Person
                        NotificationIconType.BADGE -> Icons.Default.More // Mantenuta icona More
                    },
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(32.dp),
                    tint = colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp)) // Spazio tra icona e testo

            // Content Column
            Column(
                modifier = Modifier
                    .weight(1f) // Occupa lo spazio rimanente
            ) {
                Text(
                    text = notification.senderType,
                    style = typography.titleMedium, // Leggermente più grande
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimary
                )

                Spacer(modifier = Modifier.height(4.dp)) // Spazio tra i testi

                Text(
                    text = notification.message,
                    style = typography.bodyMedium,
                    color = colorScheme.onPrimary.copy(alpha = 0.8f) // Bianco semi-trasparente per il messaggio
                )
            }

            // Favorite icon Button
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (notification.isFavorite)
                        Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Toggle Favorite",
                    tint = if (notification.isFavorite) colorScheme.surfaceTint else colorScheme.onPrimary // Giallo oro e Bianco
                )
            }
        }
    }
}

// --- Preview ---
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 360, heightDp = 720) // Dimensioni tipiche telefono
@Composable
fun NotificationScreenPreview() {
    val navController = rememberNavController() // Usa rememberNavController per la preview
    val previewViewModel = NotificationsViewModel() // Crea istanza diretta del ViewModel

    // Opzionale: Avvolgi con il tuo tema Material 3 se necessario
    // YourAppTheme { // Sostituisci YourAppTheme con il nome del tuo tema
    NotificationScreen(
        navController = navController,
        idUtente = "previewUser123", // Fornisci un id utente di esempio
        viewModel = previewViewModel  // Passa il ViewModel creato per la preview
    )
    // }
}