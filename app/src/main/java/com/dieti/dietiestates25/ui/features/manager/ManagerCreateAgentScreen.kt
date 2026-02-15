package com.dieti.dietiestates25.ui.features.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar

@Composable
fun ManagerCreateAgentScreen(
    navController: NavController,
    viewModel: ManagerViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Gestione feedback operazione
    LaunchedEffect(uiState.creationSuccess) {
        if (uiState.creationSuccess) {
            snackbarHostState.showSnackbar("Agente creato con successo!")
            nome = ""; cognome = ""; email = ""; password = ""
            viewModel.resetCreationState()
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.creationError) {
        uiState.creationError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.resetCreationState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            GeneralHeaderBar(
                title = "Nuovo Agente",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Inserisci i dati del nuovo agente per la tua agenzia.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = cognome,
                onValueChange = { cognome = it },
                label = { Text("Cognome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppPrimaryButton(
                text = if (uiState.isCreating) "Creazione in corso..." else "Crea Agente",
                onClick = { viewModel.createSubAgent(nome, cognome, email, password) },
                icon = Icons.Default.PersonAdd,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isCreating && nome.isNotBlank() && cognome.isNotBlank() && email.isNotBlank() && password.isNotBlank()
            )
        }
    }
}