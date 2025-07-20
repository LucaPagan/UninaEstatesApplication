package com.dieti.dietiestates25.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Orange

data class Offer(
    val id: Int,
    val propertyName: String,
    val offerAmount: String,
    val clientName: String,
    val status: String,
    val date: String
)

val mockOffers = listOf(
    Offer(
        id = 1,
        propertyName = "Via Roma 10, Milano",
        offerAmount = "€ 340.000",
        clientName = "Luca Bianchi",
        status = "In attesa",
        date = "2025-05-20"
    ),
    Offer(
        id = 2,
        propertyName = "Corso Vittorio Emanuele 20, Roma",
        offerAmount = "€ 490.000",
        clientName = "Giulia Neri",
        status = "Accettata",
        date = "2025-05-18"
    ),
    Offer(
        id = 3,
        propertyName = "Piazza Duomo 5, Firenze",
        offerAmount = "€ 800.000",
        clientName = "Marco Gialli",
        status = "Rifiutata",
        date = "2025-05-15"
    )
)

// Activity principale dell'applicazione (per dimostrazione, puoi integrarla nella tua MainActivity esistente)
class OfferManagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietiEstatesTheme {
                val navController = rememberNavController()
                OfferManagerScreen(navController = navController, idUtente = "manager_test")
            }
        }
    }
}

// Schermata principale per la gestione delle offerte
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferManagerScreen(navController: NavController, idUtente: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestione Offerte",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            AppBottomNavigation(navController = navController, idUtente = idUtente)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                "Offerte Ricevute",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockOffers) { offer ->
                    OfferCard(offer = offer)
                }
            }
        }
    }
}

// Composable per la visualizzazione di una singola card offerta
@Composable
fun OfferCard(offer: Offer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Immobile: ${offer.propertyName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Offerta: ${offer.offerAmount}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Cliente: ${offer.clientName}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Data Offerta: ${offer.date}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Stato: ${offer.status}",
                fontSize = 14.sp,
                color = when (offer.status) {
                    "Accettata" -> Color(0xFF4CAF50) // Verde originale
                    "Rifiutata" -> Color(0xFFF44336) // Rosso originale
                    else -> Orange // Usa il colore Orange importato per "In attesa"
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Preview della schermata di gestione offerte
@Preview(showBackground = true)
@Composable
fun PreviewOfferManagerScreen() {
    DietiEstatesTheme {
        val navController = rememberNavController()
        OfferManagerScreen(navController = navController, idUtente = "manager_preview")
    }
}
