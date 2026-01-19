package com.dieti.dietiestates25.ui.features.manager

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import kotlinx.coroutines.delay
import java.util.Locale

// --- DATI MOCK LOCALI (Sostituiscono Model e ViewModel) ---
data class Offer(
    val id: String,
    val buyerName: String,
    val price: String,
    val propertyAddress: String,
    val status: String,
    val date: String
)

data class Appointment(
    val id: String,
    val clientName: String,
    val propertyAddress: String,
    val date: String,
    val timeSlot: String,
    val status: String,
    val notes: String
)

data class Report(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val summary: String
)

enum class ManagerTab {
    OFFERS, APPOINTMENTS, REPORTS
}

enum class StatoOfferta {
    IN_ATTESA, ACCETTATA, RIFIUTATA
}

@Composable
fun ManagerScreen(
    navController: NavController,
    idUtente: String = "sconosciuto"
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    // --- GESTIONE STATO LOCALE ---
    var currentTab by remember { mutableStateOf(ManagerTab.OFFERS) }
    var selectedItem by remember { mutableStateOf<Any?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Stati per i dati
    var offers by remember { mutableStateOf<List<Offer>>(emptyList()) }
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }
    var reports by remember { mutableStateOf<List<Report>>(emptyList()) }

    // --- CARICAMENTO DATI SIMULATO ---
    LaunchedEffect(Unit) {
        delay(1000) // Simula caricamento

        offers = listOf(
            Offer("1", "Mario Rossi", "350.000", "Via Roma 1, Napoli", "In Attesa", "2024-01-15"),
            Offer("2", "Luigi Verdi", "420.000", "Via Petrarca 10, Napoli", "Accettata", "2024-01-10"),
            Offer("3", "Giulia Bianchi", "180.000", "Corso Umberto 50, Napoli", "Rifiutata", "2024-01-05")
        )

        appointments = listOf(
            Appointment("1", "Anna Neri", "Via Roma 1", "2024-02-20", "10:00 - 11:00", "Confermato", "Interessata al garage"),
            Appointment("2", "Paolo Gialli", "Via Petrarca 10", "2024-02-21", "15:00 - 16:00", "In Attesa", "Prima visita")
        )

        reports = listOf(
            Report("1", "Analisi Mercato Q1", "Andamento prezzi zona Vomero", "2024-01-01", "Prezzi in salita del 5%."),
            Report("2", "Performance Agenti", "Vendite concluse nel 2023", "2023-12-31", "Ottimi risultati nel settore residenziale.")
        )

        isLoading = false
    }

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    Scaffold(
        topBar = {
            if (selectedItem != null) {
                AppTopBar(
                    title = "Dettagli",
                    actionIcon = Icons.Filled.ArrowBack,
                    actionContentDescription = "Torna indietro",
                    onActionClick = { selectedItem = null },
                    actionBackgroundColor = colorScheme.primaryContainer,
                    actionIconTint = colorScheme.onPrimaryContainer,
                    showAppIcon = true,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            } else {
                AppTopBar(
                    title = "Manager",
                    actionIcon = Icons.Filled.Shield,
                    actionContentDescription = "Mostra opzioni manager",
                    onActionClick = { /* Gestisci click */ },
                    actionBackgroundColor = colorScheme.primaryContainer,
                    actionIconTint = colorScheme.onPrimaryContainer,
                    showAppIcon = true,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }
        },
        bottomBar = {
            if (selectedItem == null) {
                AppBottomNavigation(navController = navController, idUtente = idUtente)
            }
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
                    CircularProgressIndicator(color = colorScheme.onPrimary)
                }
            } else {
                if (selectedItem != null) {
                    ItemDetailScreen(
                        item = selectedItem,
                        colorScheme = colorScheme,
                        typography = typography,
                        dimensions = dimensions
                    )
                } else {
                    ManagerScreenContent(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it },
                        colorScheme = colorScheme,
                        typography = typography,
                        dimensions = dimensions,
                        offers = offers,
                        appointments = appointments,
                        reports = reports,
                        onItemClick = { item -> selectedItem = item }
                    )
                }
            }
        }
    }
}

@Composable
private fun ManagerScreenContent(
    currentTab: ManagerTab,
    onTabSelected: (ManagerTab) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    offers: List<Offer>,
    appointments: List<Appointment>,
    reports: List<Report>,
    onItemClick: (Any) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ManagerTabs(
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )

        when (currentTab) {
            ManagerTab.OFFERS -> {
                if (offers.isEmpty()) {
                    EmptyDisplayView(
                        modifier = Modifier.weight(1f),
                        message = "Nessuna offerta da mostrare.",
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                } else {
                    OffersList(
                        modifier = Modifier.weight(1f),
                        data = offers,
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography,
                        onItemClick = onItemClick
                    )
                }
            }
            ManagerTab.APPOINTMENTS -> {
                if (appointments.isEmpty()) {
                    EmptyDisplayView(
                        modifier = Modifier.weight(1f),
                        message = "Nessun appuntamento da mostrare.",
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                } else {
                    AppointmentsList(
                        modifier = Modifier.weight(1f),
                        data = appointments,
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography,
                        onItemClick = onItemClick
                    )
                }
            }
            ManagerTab.REPORTS -> {
                if (reports.isEmpty()) {
                    EmptyDisplayView(
                        modifier = Modifier.weight(1f),
                        message = "Nessun report da mostrare.",
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                } else {
                    ReportsList(
                        modifier = Modifier.weight(1f),
                        data = reports,
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography,
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}

@Composable
private fun OffersList(
    modifier: Modifier,
    data: List<Offer>,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography,
    onItemClick: (Offer) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = dimensions.paddingMedium,
            vertical = dimensions.paddingSmall
        ),
        verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
    ) {
        items(data) { offer ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(colorScheme.surfaceVariant)
                    .clickable { onItemClick(offer) }
                    .padding(dimensions.paddingMedium)
            ) {
                Column {
                    Text("${offer.buyerName} offre €${offer.price}", style = typography.titleMedium)
                    Text("Indirizzo: ${offer.propertyAddress}", style = typography.bodyMedium)
                    Spacer(Modifier.height(dimensions.spacingSmall))
                    StatusBadge(statoOfferta = stringToStatoOfferta(offer.status))
                }
            }
        }
    }
}

@Composable
private fun AppointmentsList(
    modifier: Modifier,
    data: List<Appointment>,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography,
    onItemClick: (Appointment) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
    ) {
        items(data) { appointment ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(colorScheme.surfaceVariant)
                    .clickable { onItemClick(appointment) }
                    .padding(dimensions.paddingMedium)
            ) {
                Column {
                    Text("Cliente: ${appointment.clientName}", style = typography.titleMedium)
                    Text("Immobile: ${appointment.propertyAddress}", style = typography.bodyMedium)
                    Text("Data: ${appointment.date} ore ${appointment.status}", style = typography.bodyMedium)
                    Text("Note: ${appointment.notes}", style = typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun ReportsList(
    modifier: Modifier,
    data: List<Report>,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography,
    onItemClick: (Report) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
    ) {
        items(data) { report ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(colorScheme.surfaceVariant)
                    .clickable { onItemClick(report) }
                    .padding(dimensions.paddingMedium)
            ) {
                Column {
                    Text(report.title, style = typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(report.description, style = typography.bodyMedium)
                    Text("Data: ${report.date}", style = typography.bodySmall)
                    Text("Sintesi: ${report.summary}", style = typography.bodySmall, color = colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ManagerTabs(
    currentTab: ManagerTab,
    onTabSelected: (ManagerTab) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingMedium)
            .height(dimensions.buttonHeight)
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingExtraSmall),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = ManagerTab.entries.toTypedArray()
        tabs.forEach { tab ->
            ManagerTabButton(
                text = when (tab) {
                    ManagerTab.OFFERS -> "Offerte"
                    ManagerTab.APPOINTMENTS -> "Appuntamenti"
                    ManagerTab.REPORTS -> "Report"
                },
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
private fun ManagerTabButton(
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
            style = typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun EmptyDisplayView(
    modifier: Modifier = Modifier,
    message: String,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(dimensions.paddingMedium),
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
private fun StatusBadge(
    statoOfferta: StatoOfferta,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val (backgroundColor, text, textColor) = when (statoOfferta) {
        StatoOfferta.ACCETTATA -> Triple(colorScheme.primary, "Accettata", colorScheme.onPrimary)
        StatoOfferta.IN_ATTESA -> Triple(colorScheme.scrim, "In Attesa", colorScheme.onPrimary)
        StatoOfferta.RIFIUTATA -> Triple(colorScheme.error, "Rifiutata", colorScheme.onError)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = dimensions.statusBadgePadding, vertical = dimensions.paddingExtraSmall),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor
        )
    }
}

fun stringToStatoOfferta(status: String): StatoOfferta {
    return when (status.lowercase(Locale.ROOT)) {
        "in attesa" -> StatoOfferta.IN_ATTESA
        "accettata" -> StatoOfferta.ACCETTATA
        "rifiutata" -> StatoOfferta.RIFIUTATA
        else -> StatoOfferta.IN_ATTESA
    }
}

@Composable
fun ItemDetailScreen(
    item: Any?,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensions.paddingLarge)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                .background(colorScheme.surfaceVariant)
                .padding(dimensions.paddingMedium)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (item) {
                    is Offer -> {
                        Text("Dettagli Offerta", style = typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                        Text("Acquirente: ${item.buyerName}", style = typography.titleMedium)
                        Text("Prezzo: €${item.price}", style = typography.titleMedium)
                        Text("Indirizzo: ${item.propertyAddress}", style = typography.titleMedium)
                        Text("Data: ${item.date}", style = typography.titleMedium)
                        StatusBadge(statoOfferta = stringToStatoOfferta(item.status))
                    }
                    is Appointment -> {
                        Text("Dettagli Appuntamento", style = typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                        Text("Cliente: ${item.clientName}", style = typography.titleMedium)
                        Text("Immobile: ${item.propertyAddress}", style = typography.titleMedium)
                        Text("Data: ${item.date}", style = typography.titleMedium)
                        Text("Slot orario: ${item.timeSlot}", style = typography.titleMedium)
                        Text("Note: ${item.notes}", style = typography.titleMedium)
                    }
                    is Report -> {
                        Text("Dettagli Report", style = typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                        Text("Titolo: ${item.title}", style = typography.titleMedium)
                        Text("Descrizione: ${item.description}", style = typography.titleMedium)
                        Text("Data: ${item.date}", style = typography.titleMedium)
                        Text("Sintesi: ${item.summary}", style = typography.bodyMedium, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Manager Screen Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Manager Screen Dark")
@Composable
fun ManagerScreenPreview() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        ManagerScreen(navController = navController)
    }
}