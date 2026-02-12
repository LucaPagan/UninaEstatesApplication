package com.dieti.dietiestates25.ui.features.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.TrattativaSummaryDTO
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.SessionManager

@Composable
fun ManagerOffersScreen(
    navController: NavController,
    viewModel: ManagerOffersViewModel = viewModel()
) {
    val offerte by viewModel.offerte.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions
    val context = LocalContext.current

    val currentAgentId = remember { SessionManager.getUserId(context) }

    LaunchedEffect(currentAgentId) {
        if (!currentAgentId.isNullOrBlank()) {
            viewModel.loadOfferte(currentAgentId)
        }
    }

    Scaffold(
        topBar = {
            GeneralHeaderBar(
                title = "Gestione Proposte",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (offerte.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nessuna trattativa attiva.", color = colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                // MODIFICA: Usiamo itemsIndexed per capire dove inserire il divisore
                itemsIndexed(offerte) { index, trattativa ->

                    // 1. Logica Divisore
                    val isTerminated = trattativa.ultimoStato == "ACCETTATA" || trattativa.ultimoStato == "RIFIUTATA"

                    val showDivider = if (index > 0) {
                        val prevItem = offerte[index - 1]
                        val prevTerminated = prevItem.ultimoStato == "ACCETTATA" || prevItem.ultimoStato == "RIFIUTATA"
                        // Mostra se l'attuale è terminata E la precedente NON lo era (cambio di sezione)
                        isTerminated && !prevTerminated
                    } else {
                        // Opzionale: se la lista inizia subito con terminate (es. nessuna attiva), puoi mettere true se vuoi l'intestazione
                        false
                    }

                    // 2. Renderizza Divisore
                    if (showDivider) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.outlineVariant)
                            Text(
                                text = "STORICO / CONCLUSE",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = colorScheme.outlineVariant)
                        }
                    }

                    // 3. Renderizza Card
                    TrattativaItem(
                        item = trattativa,
                        onClick = {
                            navController.navigate("manager_negotiation_chat/${trattativa.offertaId}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TrattativaItem(item: TrattativaSummaryDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = RetrofitClient.getFullUrl(item.immagineUrl ?: ""),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = item.immobileTitolo,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.immobileIndirizzo ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(4.dp))

                val statusColor = when(item.ultimoStato) {
                    "NUOVA PROPOSTA" -> Color(0xFF2196F3) // Blu
                    "CONTROPROPOSTA" -> Color(0xFFF57C00) // Arancione
                    "ACCETTATA" -> Color(0xFF2E7D32) // Verde
                    "RIFIUTATA" -> Color.Red
                    else -> Color.Gray
                }

                Text(
                    text = item.ultimoStato.replace("_", " "),
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Aggiornato: ${item.ultimaModifica}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}