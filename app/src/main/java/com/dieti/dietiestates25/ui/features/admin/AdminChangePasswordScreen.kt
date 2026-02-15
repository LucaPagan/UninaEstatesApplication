package com.dieti.dietiestates25.ui.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockReset
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
fun AdminChangePasswordScreen(
    navController: NavController,
    viewModel: AdminScreenViewModel = viewModel()
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    val operationState by viewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operationState) {
        if (operationState is AdminOperationState.Success) {
            snackbarHostState.showSnackbar((operationState as AdminOperationState.Success).message)
            oldPass = ""; newPass = ""; confirmPass = ""
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
                title = "Cambia Password",
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
            Text(
                text = "Modifica la tua password di accesso.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = oldPass,
                onValueChange = { oldPass = it },
                label = { Text("Vecchia Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("Nuova Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                label = { Text("Conferma Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppPrimaryButton(
                text = if (operationState is AdminOperationState.Loading) "Caricamento..." else "Aggiorna Password",
                onClick = { viewModel.changeMyPassword(oldPass, newPass) },
                icon = Icons.Default.LockReset,
                modifier = Modifier.fillMaxWidth(),
                enabled = newPass.isNotEmpty() && newPass == confirmPass && operationState !is AdminOperationState.Loading
            )
        }
    }
}