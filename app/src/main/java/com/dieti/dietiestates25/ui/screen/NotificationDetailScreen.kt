package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

import java.text.NumberFormat
import java.util.Locale

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone // o altra icona rilevante
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp // Mantieni per valori hardcoded non in Dimensions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController

// ViewModel for Notification Details (invariato)
class NotificationDetailViewModel : ViewModel() {
    data class NotificationDetail(
        val id: Int,
        val senderType: String,
        val senderName: String,
        val message: String,
        val propertyAddress: String,
        val propertyPrice: Double,
        val offerAmount: Double? = null
    )

    private val _currentNotification = MutableStateFlow(
        NotificationDetail(
            id = 1,
            senderType = "Compratore",
            senderName = "Mario Rossi",
            message = "Il %SENDER_TYPE% %SENDER_NAME% ha fatto un'offerta di %OFFER_AMOUNT% per l'immobile da lei messo in vendita situato in %PROPERTY_ADDRESS%. L'immobile era stato inizialmente listato a %PROPERTY_PRICE%. Valuti attentamente la proposta e decida se accettare o rifiutare. Può contattare direttamente il proponente per ulteriori chiarimenti o negoziazioni. Questa è una fase cruciale della transazione, quindi prenda il tempo necessario per una decisione informata.",
            propertyAddress = "Via Ripuaria 48, Pozzuoli (NA)",
            propertyPrice = 150000.0,
            offerAmount = 120000.0
        )
    )
    val currentNotification = _currentNotification.asStateFlow()

    fun getFormattedMessage(detail: NotificationDetail): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.ITALY)
        return detail.message
            .replace("%SENDER_TYPE%", detail.senderType)
            .replace("%SENDER_NAME%", detail.senderName)
            .replace("%PROPERTY_ADDRESS%", detail.propertyAddress)
            .replace("%PROPERTY_PRICE%", currencyFormat.format(detail.propertyPrice))
            .replace("%OFFER_AMOUNT%", detail.offerAmount?.let { currencyFormat.format(it) } ?: "N/A")
    }

    fun acceptProposal() {
        println("Proposta accettata")
    }

    fun rejectProposal() {
        println("Proposta rifiutata")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    viewModel: NotificationDetailViewModel = viewModel()
) {
    DietiEstatesTheme {
        val notificationDetail by viewModel.currentNotification.collectAsState()
        val formattedMessage = remember(notificationDetail) {
            viewModel.getFormattedMessage(notificationDetail)
        }
        val dimensions = Dimensions // Istanza locale per accesso breve

        Scaffold(
            topBar = {
                NotificationDetailTopAppBar(
                    navController = navController,
                    colorScheme = MaterialTheme.colorScheme,
                    typography = MaterialTheme.typography
                    // dimensions non serve qui perché TopAppBar gestisce i suoi padding interni
                )
            }
        ) { paddingValues ->
            NotificationDetailContent(
                modifier = Modifier.padding(paddingValues),
                notificationDetail = notificationDetail,
                formattedMessage = formattedMessage,
                onAccept = viewModel::acceptProposal,
                onReject = viewModel::rejectProposal,
                colorScheme = MaterialTheme.colorScheme,
                typography = MaterialTheme.typography,
                dimensions = dimensions // Passa dimensions
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationDetailTopAppBar(
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography
) {
    TopAppBar(
        title = {
            Text(
                text = "Dettaglio Notifica",
                style = typography.titleLarge,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Implementa menu opzioni */ }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Menu"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary,
            titleContentColor = colorScheme.onPrimary,
            navigationIconContentColor = colorScheme.onPrimary,
            actionIconContentColor = colorScheme.onPrimary
        )
        // Non ha padding hardcoded da sostituire direttamente qui
    )
}

@Composable
private fun NotificationDetailContent(
    modifier: Modifier = Modifier,
    notificationDetail: NotificationDetailViewModel.NotificationDetail,
    formattedMessage: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) // 20.dp non in Dimensions, lasciato invariato
                .background(colorScheme.background)
                .padding(dimensions.paddingMedium), // SOSTITUITO 16.dp
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NotificationHeaderCard(
                    senderType = notificationDetail.senderType,
                    senderName = notificationDetail.senderName,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions // Passa dimensions
                )
                Spacer(modifier = Modifier.height(dimensions.spacingMedium)) // SOSTITUITO 16.dp
                ProposalDetailsCard(
                    message = formattedMessage,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions // Passa dimensions
                )
            }
            ActionButtonsSection(
                onAccept = onAccept,
                onReject = onReject,
                modifier = Modifier.padding(top = dimensions.paddingMedium), // SOSTITUITO 16.dp
                dimensions = dimensions // Passa dimensions
            )
        }
    }
}

@Composable
private fun NotificationHeaderCard(
    senderType: String,
    senderName: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)) // SOSTITUITO 12.dp
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingMedium), // SOSTITUITO 16.dp
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.bottomNavHeight) // SOSTITUITO 64.dp
                .clip(RoundedCornerShape(10.dp)) // 10.dp non in Dimensions, lasciato invariato
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Icona Notifica",
                modifier = Modifier.size(dimensions.iconSizeLarge), // SOSTITUITO 32.dp con 36.dp (più vicino e da Dimensions)
                tint = colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(dimensions.spacingMedium)) // SOSTITUITO 16.dp
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = senderType,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = senderName,
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ProposalDetailsCard(
    message: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)) // SOSTITUITO 12.dp
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(
                horizontal = dimensions.paddingMedium, // SOSTITUITO 16.dp
                vertical = 12.dp // 12.dp non in Dimensions, lasciato invariato
            )
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium) // SOSTITUITO 12.dp con 16.dp
    ) {
        AppPrimaryButton(
            onClick = onAccept,
            modifier = Modifier.fillMaxWidth(),
            text = "Accetta Proposta",
        )
        AppRedButton(
            onClick = onReject,
            modifier = Modifier.fillMaxWidth(),
            text = "Rifiuta Proposta"
        )
    }
}

@Preview(showBackground = true, name = "Notification Detail Light")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Notification Detail Dark")
@Composable
fun NotificationDetailScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = viewModel<NotificationDetailViewModel>()
    NotificationDetailScreen(
        navController = navController,
        viewModel = previewViewModel
    )
}