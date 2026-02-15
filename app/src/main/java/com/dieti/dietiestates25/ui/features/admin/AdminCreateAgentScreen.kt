package com.dieti.dietiestates25.ui.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.AgenziaOptionDTO
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateAgentScreen(
    navController: NavController,
    viewModel: AdminScreenViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isCapo by remember { mutableStateOf(false) }

    val agenzieOptions by viewModel.agenzieOptions.collectAsState()
    var selectedAgenzia by remember { mutableStateOf<AgenziaOptionDTO?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val operationState by viewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.fetchAgencies() }

    LaunchedEffect(operationState) {
        if (operationState is AdminOperationState.Success) {
            snackbarHostState.showSnackbar((operationState as AdminOperationState.Success).message)
            nome = ""; cognome = ""; email = ""; password = ""; selectedAgenzia = null; isCapo = false
            viewModel.resetState()
        } else if (operationState is AdminOperationState.Error) {
            snackbarHostState.showSnackbar((operationState as AdminOperationState.Error).message)
            viewModel.resetState()
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
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cognome,
                onValueChange = { cognome = it },
                label = { Text("Cognome") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown Agenzie
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedAgenzia?.nome ?: "Seleziona Agenzia",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    agenzieOptions.forEach { agenzia ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(agenzia.nome)
                                    if (agenzia.haCapo) {
                                        Text(
                                            "Ha già un capo",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            },
                            onClick = {
                                selectedAgenzia = agenzia
                                if (agenzia.haCapo) isCapo = false
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Checkbox Capo Agenzia
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isCapo,
                    onCheckedChange = { isCapo = it },
                    enabled = selectedAgenzia != null && !selectedAgenzia!!.haCapo
                )
                Column {
                    Text("Assegna ruolo Capo Agenzia")
                    if (selectedAgenzia != null && selectedAgenzia!!.haCapo) {
                        Text(
                            "Non disponibile: l'agenzia ha già un capo.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppPrimaryButton(
                text = if (operationState is AdminOperationState.Loading) "Caricamento..." else "Crea Agente",
                onClick = { selectedAgenzia?.let { viewModel.createAgent(nome, cognome, email, password, it.id, isCapo) } },
                icon = Icons.Default.PersonAdd,
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is AdminOperationState.Loading && selectedAgenzia != null
            )
        }
    }
}