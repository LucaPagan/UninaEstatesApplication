package com.dieti.dietiestates25.ui.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar

@Composable
fun AdminCreateAdminScreen(
    navController: NavController,
    viewModel: AdminScreenViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val operationState by viewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operationState) {
        if (operationState is AdminOperationState.Success) {
            snackbarHostState.showSnackbar((operationState as AdminOperationState.Success).message)
            email = ""; password = ""
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
                title = "Nuovo Amministratore",
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

            Spacer(modifier = Modifier.height(8.dp))

            AppPrimaryButton(
                text = if (operationState is AdminOperationState.Loading) "Caricamento..." else "Crea Amministratore",
                onClick = { viewModel.createAdmin(email, password) },
                icon = Icons.Default.PersonAdd,
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is AdminOperationState.Loading
            )
        }
    }
}