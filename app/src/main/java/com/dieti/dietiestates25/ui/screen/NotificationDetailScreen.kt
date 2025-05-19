package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Locale

// ViewModel for Notification Details
class NotificationDetailViewModel : ViewModel() {
    data class NotificationDetail(
        val id: Int,
        val senderType: String,
        val senderName: String,
        val message: String,
        val propertyAddress: String, // Potrebbe essere usato nel messaggio o in un campo separato
        val propertyPrice: Double,    // Potrebbe essere usato nel messaggio o in un campo separato
        val offerAmount: Double? = null // Aggiunto per chiarezza se c'è un'offerta specifica
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

    // Metodo per formattare il messaggio con i dati dinamici
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
        // Implementa logica di accettazione
        println("Proposta accettata")
    }

    fun rejectProposal() {
        // Implementa logica di rifiuto
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

        Scaffold(
            topBar = {
                NotificationDetailTopAppBar(
                    navController = navController,
                    colorScheme = MaterialTheme.colorScheme,
                    typography = MaterialTheme.typography
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
                typography = MaterialTheme.typography
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
                text = "Dettaglio Notifica", // Titolo più specifico
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
            IconButton(onClick = { /* */ }) {
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
    typography: Typography
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.primary) // Sfondo primario per l'effetto "notch" superiore
    ) {
        // Box principale con angoli arrotondati solo in alto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(colorScheme.background) // Sfondo del contenuto effettivo
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween // Spinge i bottoni in basso
        ) {
            Column(modifier = Modifier.weight(1f)) { // Colonna per header e dettagli proposta (scrollabile)
                NotificationHeaderCard(
                    senderType = notificationDetail.senderType,
                    senderName = notificationDetail.senderName,
                    colorScheme = colorScheme,
                    typography = typography
                )
                Spacer(modifier = Modifier.height(16.dp)) // Spazio tra header e dettagli
                ProposalDetailsCard(
                    message = formattedMessage, // Usa il messaggio formattato
                    colorScheme = colorScheme,
                    typography = typography
                )
            }
            ActionButtonsSection(
                onAccept = onAccept,
                onReject = onReject,
                modifier = Modifier.padding(top = 16.dp) // Spazio prima dei bottoni
            )
        }
    }
}

@Composable
private fun NotificationHeaderCard(
    senderType: String,
    senderName: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // Angoli più dolci
            .background(colorScheme.surfaceVariant) // Un colore di sfondo per la card interna
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp) // Dimensione icona leggermente aggiustata
                .clip(RoundedCornerShape(10.dp))
                .background(colorScheme.primaryContainer), // Colore per il box icona
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone, // o un'icona più specifica basata su senderType
                contentDescription = "Icona Notifica",
                modifier = Modifier.size(32.dp),
                tint = colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = senderType,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold), // Titolo più appropriato
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
    typography: Typography
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f)) // Sfondo leggermente diverso
            .padding(horizontal = 16.dp, vertical = 12.dp) // Padding interno
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxSize() // Occupa tutto lo spazio del Box
                .verticalScroll(scrollState) // Rende il testo scrollabile
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Spazio tra i bottoni
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
    // Per testare un messaggio più lungo nella preview:
    val previewViewModel = viewModel<NotificationDetailViewModel>()
    // Puoi modificare _currentNotification.value nel ViewModel per testare scenari specifici se necessario,
    // ma la preview userà i dati di default del ViewModel.

    NotificationDetailScreen(
        navController = navController,
        viewModel = previewViewModel
    )
}