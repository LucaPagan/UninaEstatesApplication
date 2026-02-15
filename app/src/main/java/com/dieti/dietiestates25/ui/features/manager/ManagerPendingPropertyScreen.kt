package com.dieti.dietiestates25.ui.features.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.features.property.PropertyContent
import com.dieti.dietiestates25.ui.features.property.PropertyTopAppBar
import com.dieti.dietiestates25.ui.features.property.PropertyViewModel
import com.dieti.dietiestates25.ui.theme.Dimensions
import kotlinx.coroutines.launch

@Composable
fun ManagerPendingPropertyScreen(
    navController: NavController,
    immobileId: String,
    viewModel: PropertyViewModel = viewModel(),
    notificationsViewModel: ManagerNotificationsViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val dimensions = Dimensions
    val coroutineScope = rememberCoroutineScope()

    // Carica i dati dell'appartamento
    LaunchedEffect(immobileId) {
        viewModel.loadProperty(immobileId)
    }

    val property by viewModel.property.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isActionLoading by notificationsViewModel.isLoading.collectAsState()

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (error != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = error ?: "Errore", color = colorScheme.error) }
            } else if (property != null) {
                val imm = property!!

                // Riutilizziamo la UI dell'appartamento, passando i pulsanti da manager
                PropertyContent(
                    immobile = imm,
                    navController = navController,
                    coroutineScope = coroutineScope,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions,
                    context = context,
                    bottomActions = {
                        ManagerPendingActions(
                            isProcessing = isActionLoading,
                            colorScheme = colorScheme,
                            onAccept = {
                                coroutineScope.launch {
                                    notificationsViewModel.accettaRichiesta(imm.id)
                                    navController.popBackStack()
                                }
                            },
                            onDecline = {
                                coroutineScope.launch {
                                    notificationsViewModel.rifiutaRichiesta(imm.id)
                                    navController.popBackStack()
                                }
                            }
                        )
                    }
                )
            }

            PropertyTopAppBar(colorScheme = colorScheme, navController = navController, dimensions = dimensions)
        }
    }
}

@Composable
fun ManagerPendingActions(
    isProcessing: Boolean,
    colorScheme: ColorScheme,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Assegnazione Incarico",
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isProcessing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.errorContainer,
                        contentColor = colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Close, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Rifiuta")
                }

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32), // Verde
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Accetta")
                }
            }
        }
    }
}