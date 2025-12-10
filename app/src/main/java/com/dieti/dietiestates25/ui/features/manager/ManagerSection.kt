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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.data.model.Appointment
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import java.util.Locale

// Enum per lo stato dell'offerta, necessario per il StatusBadge
enum class StatoOfferta {
    IN_ATTESA, ACCETTATA, RIFIUTATA
}

@Composable
fun ManagerScreen(
    navController: NavController,
    idUtente: String = "sconosciuto",
    viewModel: ManagerViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val currentTab by viewModel.currentTab.collectAsState()
    val offers by viewModel.offers.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()

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
                    onActionClick = { viewModel.setSelectedItem(null) },
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
                    onTabSelected = { viewModel.setCurrentTab(it) },
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions,
                    offers = offers,
                    appointments = appointments,
                    reports = reports,
                    onItemClick = { item -> viewModel.setSelectedItem(item) }
                )
            }
        }
    }
}

@Composable
private fun ManagerScreenContent(
    currentTab: ManagerViewModel.ManagerTab,
    onTabSelected: (ManagerViewModel.ManagerTab) -> Unit,
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
            ManagerViewModel.ManagerTab.OFFERS -> {
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
            ManagerViewModel.ManagerTab.APPOINTMENTS -> {
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
            ManagerViewModel.ManagerTab.REPORTS -> {
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
    currentTab: ManagerViewModel.ManagerTab,
    onTabSelected: (ManagerViewModel.ManagerTab) -> Unit,
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
        val tabs = ManagerViewModel.ManagerTab.entries.toTypedArray()
        tabs.forEach { tab ->
            ManagerTabButton(
                text = when (tab) {
                    ManagerViewModel.ManagerTab.OFFERS -> "Offerte"
                    ManagerViewModel.ManagerTab.APPOINTMENTS -> "Appuntamenti"
                    ManagerViewModel.ManagerTab.REPORTS -> "Report"
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