package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.navigation.Screen // Assicurati che questo percorso sia corretto
import com.dieti.dietiestates25.ui.theme.Dimensions

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale // Aggiunto per il formatter della data

import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

import android.annotation.SuppressLint

import androidx.lifecycle.viewModelScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.More
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppIconDisplay

// Data class to represent a notification
data class Notification(
    val id: Int,
    val senderType: String,
    val message: String,
    val iconType: NotificationIconType,
    val date: LocalDate,
    var isFavorite: Boolean = false
)

// Icon types for notifications
enum class NotificationIconType {
    PHONE, PERSON, BADGE
}

// ViewModel to manage notifications
class NotificationsViewModel : ViewModel() {

    enum class NotificationTab {
        RECENTI, VECCHIE, SALVATE
    }

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val allNotifications: StateFlow<List<Notification>> = _notifications.asStateFlow() // Esposizione se serve altrove

    private val _currentTab = MutableStateFlow(NotificationTab.RECENTI)
    val currentTab: StateFlow<NotificationTab> = _currentTab.asStateFlow()

    private val _favoriteNotifications = MutableStateFlow<List<Notification>>(emptyList())
    // val favoriteNotificationsList: StateFlow<List<Notification>> = _favoriteNotifications.asStateFlow() // Potrebbe non essere necessaria l'esposizione diretta

    // StateFlow per le notifiche filtrate, basato sulla tab corrente e le liste
    val filteredNotifications: StateFlow<List<Notification>> =
        combine(_currentTab, _notifications, _favoriteNotifications) { tab, allNots, favNots ->
            val today = LocalDate.now()
            val fiveDaysAgo = today.minusDays(5) // Notifiche recenti fino a 5 giorni fa

            when (tab) {
                NotificationTab.RECENTI -> allNots
                    .filter { !it.date.isBefore(fiveDaysAgo) } // Include oggi e fino a 5 giorni fa
                    .sortedByDescending { it.date }
                NotificationTab.VECCHIE -> allNots // Mostra tutte, già ordinate in _notifications se necessario o ordinate qui
                    .sortedByDescending { it.date } // Ordinamento per data decrescente
                NotificationTab.SALVATE -> favNots // favNots è già la lista dei preferiti, ordinata in updateFavorites
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    init {
        loadInitialNotifications()
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
            Notification(7, "Compratore", "Vorrei fissare un appuntamento.", NotificationIconType.PERSON, today.minusDays(6), false), // Più vecchio di 5 giorni
            Notification(8, "Agenzia", "Promozione estiva: sconti sulle commissioni!", NotificationIconType.BADGE, today.minusDays(10), false) // Vecchio
        )
        _notifications.value = initialList.sortedByDescending { it.date } // Ordina subito la lista principale
        updateFavorites()
    }

    fun setCurrentTab(tab: NotificationTab) {
        _currentTab.value = tab
    }

    fun toggleFavorite(notificationId: Int) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isFavorite = !it.isFavorite) else it
        }.sortedByDescending { it.date } // Mantieni l'ordinamento
        updateFavorites()
    }

    private fun updateFavorites() {
        _favoriteNotifications.value = _notifications.value
            .filter { it.isFavorite }
            .sortedByDescending { it.date } // Assicurati che anche i preferiti siano ordinati
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    idUtente: String = "sconosciuto", // Parametro idUtente mantenuto
    viewModel: NotificationsViewModel = viewModel()
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography // Non serve se passiamo colorScheme e typography ai figli

        val currentTab by viewModel.currentTab.collectAsState()
        val notifications by viewModel.filteredNotifications.collectAsState()

        Scaffold(
            topBar = {
                NotificationScreenTopAppBar(
                    colorScheme = colorScheme,
                    onMenuClick = { /* Logica per il menu */ },
                    dimensions = Dimensions
                )
            },
            bottomBar = {
                // Passa l'idUtente se AppBottomNavigation lo richiede
                AppBottomNavigation(navController = navController, idUtente = idUtente)
            }
        ) { paddingValues ->
            NotificationScreenContent(
                modifier = Modifier.padding(paddingValues),
                currentTab = currentTab,
                notifications = notifications,
                onTabSelected = viewModel::setCurrentTab, // Usa riferimento a funzione
                onToggleFavorite = viewModel::toggleFavorite, // Usa riferimento a funzione
                onNotificationClick = {
                    // Naviga al dettaglio della notifica, passando l'ID se necessario
                    navController.navigate(Screen.NotificationDetailScreen.route) // Potresti voler passare notification.id
                },
                colorScheme = colorScheme,
                typography = typography // Passa typography qui
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationScreenTopAppBar(
    colorScheme: ColorScheme,
    onMenuClick: () -> Unit,
    dimensions: Dimensions
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.primary)
                    .clip(RoundedCornerShape(bottomStart = dimensions.cornerRadiusLarge, bottomEnd = dimensions.cornerRadiusLarge))
                    .padding(horizontal = dimensions.paddingLarge),
                contentAlignment = Alignment.BottomStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppIconDisplay(
                        size = 60.dp,
                        shapeRadius = dimensions.cornerRadiusMedium
                    )
                    Text(text = "Notifiche")
                }
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu Opzioni"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary,
            titleContentColor = colorScheme.onPrimary,
            actionIconContentColor = colorScheme.onPrimary // Colore icona azioni
        )
    )
}

@Composable
private fun NotificationScreenContent(
    modifier: Modifier = Modifier,
    currentTab: NotificationsViewModel.NotificationTab,
    notifications: List<Notification>,
    onTabSelected: (NotificationsViewModel.NotificationTab) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onNotificationClick: (Notification) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        NotificationTabs(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            colorScheme = colorScheme,
            typography = typography
        )

        if (notifications.isEmpty()) {
            EmptyNotificationsView(
                modifier = Modifier.weight(1f),
                colorScheme = colorScheme,
                typography = typography
            )
        } else {
            NotificationsList(
                modifier = Modifier.weight(1f),
                notifications = notifications,
                onToggleFavorite = onToggleFavorite,
                onNotificationClick = onNotificationClick,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
private fun EmptyNotificationsView(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp), // Aggiunto padding per non farla toccare i bordi
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nessuna notifica da mostrare.",
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
    typography: Typography
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp), // Aumentato padding verticale
        verticalArrangement = Arrangement.spacedBy(12.dp) // Spazio tra gli item
    ) {
        items(notifications, key = { it.id }) { notification ->
            NotificationItem(
                notification = notification,
                onToggleFavorite = { onToggleFavorite(notification.id) },
                onClick = { onNotificationClick(notification) },
                colorScheme = colorScheme,
                typography = typography
            )
            // Spacer rimosso, gestito da verticalArrangement in LazyColumn
        }
    }
}


@Composable
fun NotificationTabs( // Mantenuta pubblica se usata altrove, altrimenti private
    currentTab: NotificationsViewModel.NotificationTab,
    onTabSelected: (NotificationsViewModel.NotificationTab) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp) // Padding consistente
            .height(52.dp) // Altezza fissa per i tab
            .clip(RoundedCornerShape(26.dp)) // Leggermente più arrotondato
            .background(colorScheme.surfaceVariant) // Colore di sfondo per il contenitore dei tab
            .padding(4.dp), // Padding interno per i bottoni
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = NotificationsViewModel.NotificationTab.entries.toTypedArray()
        tabs.forEach { tab ->
            TabButton(
                text = tab.name.lowercase(Locale.getDefault())
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }, // Es. Recent, Old, Saved
                isSelected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f),
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
fun TabButton( // Mantenuta pubblica se usata altrove, altrimenti private
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Box(
        modifier = modifier
            .fillMaxHeight() // Occupa tutta l'altezza del contenitore Row
            .padding(horizontal = 2.dp) // Spazio minimo tra i bottoni
            .clip(RoundedCornerShape(22.dp)) // Angoli arrotondati per il bottone
            .background(if (isSelected) colorScheme.primary else Color.Transparent) // Sfondo trasparente se non selezionato
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp), // Padding orizzontale interno
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium // Medium per non selezionato
            ),
            color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant.copy(alpha = 0.8f) // Colore per testo non selezionato
        )
    }
}

@Composable
fun NotificationItem( // Mantenuta pubblica se usata altrove, altrimenti private
    notification: Notification,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface // Sfondo più neutro per la card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Leggera elevazione
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding aumentato
            verticalAlignment = Alignment.Top // Allinea in alto per la data e stella
        ) {
            NotificationIcon(
                iconType = notification.iconType,
                colorScheme = colorScheme
            )

            Spacer(modifier = Modifier.width(16.dp))

            NotificationContent(
                modifier = Modifier.weight(1f),
                senderType = notification.senderType,
                message = notification.message,
                colorScheme = colorScheme,
                typography = typography
            )

            Spacer(modifier = Modifier.width(12.dp)) // Aumentato spazio prima della colonna data/stella

            NotificationActions(
                date = notification.date.format(dateFormatter),
                isFavorite = notification.isFavorite,
                onToggleFavorite = onToggleFavorite,
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@Composable
private fun NotificationIcon(
    iconType: NotificationIconType,
    colorScheme: ColorScheme
) {
    Box(
        modifier = Modifier
            .size(48.dp) // Dimensione icon box ridotta
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.primaryContainer), // Usa primaryContainer
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (iconType) {
                NotificationIconType.PHONE -> Icons.Default.Phone
                NotificationIconType.PERSON -> Icons.Default.Person
                NotificationIconType.BADGE -> Icons.AutoMirrored.Filled.More // Usando Filled.More
            },
            contentDescription = "Notification Icon",
            modifier = Modifier.size(24.dp), // Dimensione icona interna
            tint = colorScheme.onPrimaryContainer // Usa onPrimaryContainer
        )
    }
}

@Composable
private fun NotificationContent(
    modifier: Modifier = Modifier,
    senderType: String,
    message: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(modifier = modifier) {
        Text(
            text = senderType,
            style = typography.titleMedium, // Coerente con il design system
            fontWeight = FontWeight.SemiBold, // Un po' meno forte di Bold
            color = colorScheme.onSurface // Colore sul colore di superficie
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant, // Colore variante per testo secondario
            maxLines = 2 // Non più di due righe
            // overflow = TextOverflow.Ellipsis // Aggiungere se si vuole l'ellissi
        )
    }
}

@Composable
private fun NotificationActions(
    date: String,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        horizontalAlignment = Alignment.End, // Allinea a destra
        verticalArrangement = Arrangement.SpaceBetween, // Spazio tra data e stella
        modifier = Modifier.height(IntrinsicSize.Min) // Per far sì che la colonna si adatti all'altezza del testo più alto
    ) {
        Text(
            text = date,
            style = typography.labelSmall,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f) // Più leggibile
        )
        Spacer(modifier = Modifier.weight(1f)) // Spinge l'icona in basso se necessario (con altezza fissa)
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp) // Dimensione standard per IconButton touch target
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Aggiungi ai preferiti",
                tint = if (isFavorite) Color(0xFFFFC107) /* Amber 500 */ else colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp) // Dimensione effettiva dell'icona stella
            )
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable") // Necessario per la preview con ViewModel
@Preview(showBackground = true, name = "Notification Screen Light")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Notification Screen Dark")
@Composable
fun NotificationScreenPreview() {
    val navController = rememberNavController()
    // Inizializza il ViewModel direttamente per la preview
    val previewViewModel = remember {
        NotificationsViewModel().apply {
            // Puoi personalizzare i dati o la tab selezionata qui per testare scenari specifici
            // Esempio:
            // setCurrentTab(NotificationsViewModel.NotificationTab.SAVED)
            // toggleFavorite(1) // Per avere una notifica salvata
        }
    }

    DietiEstatesTheme {
        NotificationScreen(
            navController = navController,
            idUtente = "previewUser123",
            viewModel = previewViewModel
        )
    }
}