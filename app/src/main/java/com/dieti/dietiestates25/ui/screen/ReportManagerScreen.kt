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


data class Report(
    val id: Int,
    val propertyName: String,
    val reporterName: String,
    val description: String,
    val status: String,
    val date: String
)

val mockReports = listOf(
    Report(
        id = 1,
        propertyName = "Via Roma 10, Milano",
        reporterName = "Giovanni Esposito",
        description = "Problema con impianto elettrico.",
        status = "Aperta",
        date = "2025-05-25"
    ),
    Report(
        id = 2,
        propertyName = "Piazza Duomo 5, Firenze",
        reporterName = "Francesca Russo",
        description = "Perdita d'acqua in bagno.",
        status = "In Lavorazione",
        date = "2025-05-22"
    ),
    Report(
        id = 3,
        propertyName = "Corso Vittorio Emanuele 20, Roma",
        reporterName = "Antonio Conti",
        description = "Mancanza di riscaldamento.",
        status = "Risolta",
        date = "2025-05-19"
    )
)

// Activity principale dell'applicazione per la gestione delle segnalazioni
class ReportManagerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietiEstatesTheme {
                val navController = rememberNavController()
                ReportManagerScreen(navController = navController, idUtente = "manager_test")
            }
        }
    }
}

// Schermata principale per la gestione delle segnalazioni
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportManagerScreen(navController: NavController, idUtente: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestione Segnalazioni",
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
            // Inserisci qui il tuo AppBottomNavigation
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
                "Segnalazioni Ricevute",
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
                items(mockReports) { report ->
                    ReportCard(report = report)
                }
            }
        }
    }
}

// Composable per la visualizzazione di una singola card segnalazione
@Composable
fun ReportCard(report: Report) {
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
                text = "Immobile: ${report.propertyName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Segnalato da: ${report.reporterName}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Descrizione: ${report.description}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Data Segnalazione: ${report.date}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Stato: ${report.status}",
                fontSize = 14.sp,
                color = when (report.status) {
                    "Aperta" -> Orange
                    "In Lavorazione" -> Color(0xFF2196F3)
                    "Risolta" -> Color(0xFF4CAF50)
                    else -> Color.Gray
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Preview della schermata di gestione segnalazioni
@Preview(showBackground = true)
@Composable
fun PreviewReportManagerScreen() {
    DietiEstatesTheme {
        val navController = rememberNavController()
        ReportManagerScreen(navController = navController, idUtente = "manager_preview")
    }
}
