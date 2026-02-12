package com.dieti.dietiestates25.ui.features.notification

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.data.remote.NotificaDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.TrattativaSummaryDTO
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions

enum class NotificationTab { GENERALE, OFFERTE }

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions

    val generalList by viewModel.generalNotifications.collectAsState()
    val offersList by viewModel.negotiations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var currentTab by remember { mutableStateOf(NotificationTab.GENERALE) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        topBar = { AppTopBar(title = "Centro Messaggi", colorScheme = colorScheme, typography = MaterialTheme.typography, dimensions = dimensions) },
        bottomBar = { AppBottomNavigation(navController = navController, idUtente = "session") }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // TABS
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                TabButton("Notifiche", currentTab == NotificationTab.GENERALE, Modifier.weight(1f)) { currentTab = NotificationTab.GENERALE }
                Spacer(Modifier.width(8.dp))
                TabButton("Le tue Offerte", currentTab == NotificationTab.OFFERTE, Modifier.weight(1f)) { currentTab = NotificationTab.OFFERTE }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                when (currentTab) {
                    NotificationTab.GENERALE -> GeneralList(generalList, navController)
                    NotificationTab.OFFERTE -> OffersList(offersList, navController)
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) { Text(text) }
}

@Composable
fun GeneralList(list: List<NotificaDTO>, navController: NavController) {
    if (list.isEmpty()) EmptyState("Nessuna notifica di sistema.")
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(list) { notifica ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val encodedTitle = Uri.encode(notifica.titolo)
                        val encodedBody = Uri.encode(notifica.corpo)
                        navController.navigate("notification_detail_generic/$encodedTitle/$encodedBody")
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(notifica.titolo, fontWeight = FontWeight.Bold)
                        Text(notifica.corpo, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(notifica.data, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun OffersList(list: List<TrattativaSummaryDTO>, navController: NavController) {
    if (list.isEmpty()) EmptyState("Non hai trattative in corso.")
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // MODIFICA: itemsIndexed per il divisore anche qui
        itemsIndexed(list) { index, item ->

            // 1. Logica Divisore
            val isTerminated = item.ultimoStato == "ACCETTATA" || item.ultimoStato == "RIFIUTATA"
            val showDivider = if (index > 0) {
                val prevItem = list[index - 1]
                val prevTerminated = prevItem.ultimoStato == "ACCETTATA" || prevItem.ultimoStato == "RIFIUTATA"
                isTerminated && !prevTerminated
            } else {
                false
            }

            // 2. Renderizza Divisore
            if (showDivider) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = "CONCLUSE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
            }

            // 3. Renderizza Card
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    navController.navigate("negotiation_detail_screen/${item.offertaId}")
                },
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = RetrofitClient.getFullUrl(item.immagineUrl ?: ""),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(item.immobileTitolo, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.ultimoStato.replace("_", " "), color = getStatusColor(item.ultimoStato), fontWeight = FontWeight.SemiBold)
                        Text("Ultimo agg: ${item.ultimaModifica}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(msg: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(msg, color = Color.Gray)
    }
}

fun getStatusColor(status: String): Color {
    return when(status) {
        "ACCETTATA" -> Color(0xFF2E7D32)
        "RIFIUTATA" -> Color.Red
        "CONTROPROPOSTA" -> Color(0xFFF57C00) // Arancione
        else -> Color.Gray
    }
}