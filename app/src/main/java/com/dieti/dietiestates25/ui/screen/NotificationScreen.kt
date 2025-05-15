package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppBottomNavigation // Assicurati che questo percorso sia corretto
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow // Importa StateFlow
import kotlinx.coroutines.flow.combine // Importa combine
import kotlinx.coroutines.flow.SharingStarted // Importa SharingStarted
import kotlinx.coroutines.flow.stateIn // Importa stateIn
import androidx.lifecycle.viewModelScope // Importa viewModelScope


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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color // Aggiunta per il colore della stella
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.navigation.Screen // Assicurati che questo percorso sia corretto
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

// Data class to represent a notification
data class Notification(
    val id: Int,
    val senderType: String,
    val message: String,
    val iconType: NotificationIconType,
    val date: LocalDate, // Aggiunto campo data
    var isFavorite: Boolean = false // 'var' è corretto qui sebbene venga gestito con .copy()
)

// Icon types for notifications
enum class NotificationIconType {
    PHONE, PERSON, BADGE
}

// ViewModel to manage notifications
class NotificationsViewModel : ViewModel() {
    // Lista master interna delle notifiche
    internal val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    // Esposizione pubblica come StateFlow per l'osservazione (se necessario altrove)
    val allNotifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _currentTab = MutableStateFlow(NotificationTab.RECENT)
    val currentTab: StateFlow<NotificationTab> = _currentTab.asStateFlow()

    // Lista interna dei preferiti. Aggiornata da updateFavorites()
    private val _favoriteNotifications = MutableStateFlow<List<Notification>>(emptyList())
    // Esposizione pubblica come StateFlow (se necessario altrove)
    val favoriteNotificationsList: StateFlow<List<Notification>> = _favoriteNotifications.asStateFlow()


    enum class NotificationTab {
        RECENT, OLD, SAVED
    }

    // StateFlow per le notifiche filtrate, basato sulla tab corrente e le liste
    val filteredNotifications: StateFlow<List<Notification>> =
        combine(_currentTab, _notifications, _favoriteNotifications) { tab, allNots, favNots ->
            val today = LocalDate.now()
            val fiveDaysAgo = today.minusDays(5)
            when (tab) {
                NotificationTab.RECENT -> allNots
                    .filter { !it.date.isBefore(fiveDaysAgo) } // Include oggi e fino a 5 giorni fa
                    .sortedByDescending { it.date }
                NotificationTab.OLD -> allNots
                    .sortedByDescending { it.date } // Mostra tutte, ordinate
                NotificationTab.SAVED -> favNots // favNots è già la lista dei preferiti, ordinata in updateFavorites
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        val today = LocalDate.now()
        _notifications.value = listOf(
            Notification(1, "Venditore", "C'è una proposta per te", NotificationIconType.PHONE, today.minusDays(Random.nextInt(0, 3).toLong()), true),
            Notification(2, "Compratore", "Richiesta informazioni", NotificationIconType.PERSON, today.minusDays(Random.nextInt(1, 4).toLong()), false),
            Notification(3, "Agenzia", "Nuovo immobile disponibile", NotificationIconType.BADGE, today.minusDays(Random.nextInt(0, 2).toLong()), true),
            Notification(4, "Broker", "Visita confermata per domani", NotificationIconType.PERSON, today.minusDays(Random.nextInt(4, 7).toLong()), false), // Più vecchio di 5 giorni
            Notification(5, "Sistema", "Aggiornamento policy privacy", NotificationIconType.BADGE, today.minusDays(Random.nextInt(10, 20).toLong()), false), // Molto vecchio
            Notification(6, "Venditore", "Offerta ricevuta per Via Roma", NotificationIconType.PHONE, today, false) // Oggi
        )
        updateFavorites() // Inizializza la lista dei preferiti
    }

    fun setCurrentTab(tab: NotificationTab) {
        _currentTab.value = tab
    }

    fun toggleFavorite(notificationId: Int) {
        // Aggiorna la lista master delle notifiche
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        // Aggiorna la lista dei preferiti basata sulla lista master modificata
        updateFavorites()
    }

    // Aggiorna la MutableStateFlow dei preferiti
    internal fun updateFavorites() {
        _favoriteNotifications.value = _notifications.value.filter { it.isFavorite }.sortedByDescending { it.date }
    }

    // getFilteredNotifications() non è più necessaria, la sua logica è in filteredNotifications StateFlow
}

@OptIn(ExperimentalMaterial3Api::class)
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
        // Osserva il nuovo StateFlow filteredNotifications
        val notifications by viewModel.filteredNotifications.collectAsState()
        val notifications = viewModel.getFilteredNotifications()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Notifiche") },
                    actions = {
                        IconButton(onClick = { /* Apri menu opzioni */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary,
                        titleContentColor = colorScheme.onPrimary,
                        actionIconContentColor = colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                AppBottomNavigation(navController = navController, idUtente = idUtente)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(colorScheme.background)
            ) {
                NotificationTabs(
                    currentTab = currentTab, // Passa il valore di currentTab, non lo StateFlow
                    onTabSelected = { viewModel.setCurrentTab(it) },
                    colorScheme = colorScheme,
                    typography = typography
                )

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nessuna notifica da mostrare.",
                            style = typography.bodyLarge,
                            color = colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(notifications, key = { it.id }) { notification ->
                            NotificationItem(
                                notification = notification,
                                onToggleFavorite = { viewModel.toggleFavorite(notification.id) },
                                colorScheme = colorScheme,
                                typography = typography,
                                navController = navController
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationTabs(
    currentTab: NotificationsViewModel.NotificationTab, // Riceve il valore, non lo StateFlow
    colorScheme: ColorScheme,
    typography: Typography,
    onTabSelected: (NotificationsViewModel.NotificationTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(colorScheme.primaryContainer)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
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
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(25.dp))
            // Colori come da tua specifica precedente
            .background(if (isSelected) colorScheme.primary else colorScheme.surfaceDim)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            // Colori come da tua specifica precedente
            color = if (isSelected) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f),
        )
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    colorScheme: ColorScheme,
    typography: Typography,
    navController: NavController,
    onToggleFavorite: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                navController.navigate(Screen.NotificationDetailScreen.route)
            }),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // Colore sfondo Card come da tua specifica
            containerColor = colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    // Colore sfondo Icon Box come da tua specifica
                    .background(colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.iconType) {
                        NotificationIconType.PHONE -> Icons.Default.Phone
                        NotificationIconType.PERSON -> Icons.Default.Person
                        NotificationIconType.BADGE -> Icons.Default.More
                    },
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(28.dp),
                    // Tint icona come da tua specifica
                    tint = colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.senderType,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    // Colore testo come da tua specifica
                    color = colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = typography.bodyMedium,
                    // Colore testo come da tua specifica
                    color = colorScheme.onPrimary.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = notification.date.format(dateFormatter),
                    style = typography.labelSmall,
                    // Colore testo data come da tua specifica
                    color = colorScheme.onPrimary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (notification.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Toggle Favorite",
                        // Tint icona stella come da tua specifica
                        tint = if (notification.isFavorite) Color(0xFFFFD700) /* Giallo Oro */ else colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun NotificationScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = remember {
        val vm = NotificationsViewModel()
        // Per testare la preview con dati specifici, puoi modificare vm._notifications.value
        // e chiamare vm.updateFavorites() qui, se necessario.
        // Esempio per la tab "Salvate" con una notifica preferita:
        /*
        val today = LocalDate.now()
        vm._notifications.value = listOf(
            Notification(1, "Preview Preferita", "Questa è salvata.", NotificationIconType.PHONE, today, true),
            Notification(2, "Preview Normale", "Questa no.", NotificationIconType.PERSON, today.minusDays(1), false)
        )
        vm.updateFavorites()
        // Potresti anche voler impostare la tab iniziale per la preview:
        // vm.setCurrentTab(NotificationsViewModel.NotificationTab.SAVED)
        */
        vm
    }

    DietiEstatesTheme {
        NotificationScreen(
            navController = navController,
            idUtente = "previewUser123",
            viewModel = previewViewModel
        )
    }
}