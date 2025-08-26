package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
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
import com.dieti.dietiestates25.ui.components.AppTopBarProfileNotification
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.model.ManagerViewModel
import java.util.Locale
import androidx.compose.foundation.layout.PaddingValues

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
            AppTopBarProfileNotification(
                title = "Manager",
                actionIcon = Icons.Filled.Shield,
                actionContentDescription = "Mostra opzioni manager",
                onActionClick = { /* Gestisci il click dell'icona dello scudo */ },
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
                dimensions = dimensions,
                colorScheme = colorScheme,
                typography = typography,
                offers = offers,
                appointments = appointments,
                reports = reports
            )
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
    offers: List<String>,
    appointments: List<String>,
    reports: List<String>
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
                    DataList(
                        modifier = Modifier.weight(1f),
                        data = offers,
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
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
                    DataList(
                        modifier = Modifier.weight(1f),
                        data = appointments,
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
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
                    DataList(
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
private fun DataList(
    modifier: Modifier = Modifier,
    data: List<String>,
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
        items(data) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .background(colorScheme.surfaceVariant)
                    .padding(dimensions.paddingMedium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
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
            .padding(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.paddingMedium
            )
            .height(52.dp)
            .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingExtraSmall),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = ManagerTab.entries.toTypedArray()
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

enum class ManagerTab {
    OFFERS, APPOINTMENTS, REPORTS
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
