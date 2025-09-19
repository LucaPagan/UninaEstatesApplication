package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.model.*
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import java.util.Locale

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

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    Scaffold(
        topBar = {
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
            ManagerScreenContent(
                currentTab = currentTab,
                onTabSelected = { viewModel.setCurrentTab(it) },
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions,
                offers = offers,
                appointments = appointments,
                reports = reports
            )
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
    reports: List<Report>
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
                        typography = typography
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
                        typography = typography
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
                        typography = typography
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
    typography: Typography
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = dimensions.paddingMedium,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(data) { offer ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(colorScheme.surfaceVariant)
                    .padding(dimensions.paddingMedium)
            ) {
                Column {
                    Text("${offer.buyerName} offre €${offer.price}", style = typography.titleMedium)
                    Text("Indirizzo: ${offer.propertyAddress}", style = typography.bodyMedium)
                    Text("Stato: ${offer.status}", style = typography.bodySmall, color = colorScheme.primary)
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
    typography: Typography
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = dimensions.paddingMedium, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(data) { appointment ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(colorScheme.surfaceVariant)
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
    typography: Typography
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = dimensions.paddingMedium, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(data) { report ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(colorScheme.surfaceVariant)
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
            .height(52.dp)
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingExtraSmall),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = ManagerViewModel.ManagerTab.entries.toTypedArray()
        tabs.forEach { tab ->
            ManagerTabButton(
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
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
            .background(if (isSelected) colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
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

@Preview(showBackground = true, name = "Manager Screen Light")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Manager Screen Dark")
@Composable
fun ManagerScreenPreview() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        ManagerScreen(navController = navController)
    }
}
