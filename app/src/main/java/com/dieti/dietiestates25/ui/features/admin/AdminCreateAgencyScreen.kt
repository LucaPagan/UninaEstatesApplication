package com.dieti.dietiestates25.ui.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.AdminOptionDTO
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateAgencyScreen(
    navController: NavController,
    viewModel: AdminScreenViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var indirizzo by remember { mutableStateOf("") }

    val adminOptions by viewModel.adminOptions.collectAsState()
    var selectedAdmin by remember { mutableStateOf<AdminOptionDTO?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val operationState by viewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.fetchAdministrators() }

    LaunchedEffect(operationState) {
        if (operationState is AdminOperationState.Success) {
            snackbarHostState.showSnackbar((operationState as AdminOperationState.Success).message)
            nome = ""; indirizzo = ""; selectedAdmin = null
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
                title = "Nuova Agenzia",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome Agenzia") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = indirizzo,
                onValueChange = { indirizzo = it },
                label = { Text("Indirizzo Sede") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown Amministratori
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedAdmin?.email ?: "Seleziona Amministratore",
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
                    adminOptions.forEach { admin ->
                        DropdownMenuItem(
                            text = { Text(admin.email) },
                            onClick = {
                                selectedAdmin = admin
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppPrimaryButton(
                text = if (operationState is AdminOperationState.Loading) "Caricamento..." else "Crea Agenzia",
                onClick = { selectedAdmin?.let { viewModel.createAgency(nome, indirizzo, it.id) } },
                icon = Icons.Default.Store,
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is AdminOperationState.Loading && nome.isNotBlank() && selectedAdmin != null
            )
        }
    }
}