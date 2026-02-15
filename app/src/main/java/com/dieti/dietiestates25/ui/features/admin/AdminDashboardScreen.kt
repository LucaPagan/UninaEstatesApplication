package com.dieti.dietiestates25.ui.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.AdminOptionDTO
import com.dieti.dietiestates25.data.remote.AgenziaOptionDTO
import com.dieti.dietiestates25.ui.components.AppTopBar
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.theme.typography

// ... (AdminDashboardScreen e AdminActionCard rimangono uguali a prima) ...
// Per completezza li includo minimizzati o uguali al passo precedente
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    onNavigateToCreateAdmin: () -> Unit,
    onNavigateToCreateAgent: () -> Unit,
    onNavigateToCreateAgency: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToYourProperties: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard Admin",
                showAppIcon = true,
                actionIcon = Icons.Default.Logout,
                onActionClick = onLogout,
                colorScheme = colorScheme,
                typography = MaterialTheme.typography,
                dimensions = Dimensions
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card per accedere alla gestione globale degli immobili
            AdminActionCard(
                title = "Gestione Immobili",
                description = "Visualizza la lista completa di tutti gli immobili nel database.",
                icon = Icons.Default.HomeWork,
                onClick = onNavigateToYourProperties
            )

            AdminActionCard("Crea Agenzia", "Registra nuova sede.", Icons.Default.Store, onNavigateToCreateAgency)
            AdminActionCard("Crea Agente", "Registra agente o capo.", Icons.Default.Badge, onNavigateToCreateAgent)
            AdminActionCard("Crea Amministratore", "Nuovo admin.", Icons.Default.PersonAdd, onNavigateToCreateAdmin)
            AdminActionCard("Cambia Password", "Aggiorna la tua password.", Icons.Default.LockReset, onNavigateToChangePassword)
        }
    }
}

@Composable
fun AdminActionCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ); Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}