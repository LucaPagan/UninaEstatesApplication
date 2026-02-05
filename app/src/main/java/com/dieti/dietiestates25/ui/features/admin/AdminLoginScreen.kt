package com.dieti.dietiestates25.ui.features.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
    onLogout: () -> Unit,
    viewModel: AdminDashboardViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Admin") },
                actions = { IconButton(onClick = { viewModel.logout(); onLogout() }) { Icon(Icons.Default.Logout, "Logout") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AdminActionCard("Crea Agenzia", "Registra nuova sede.", Icons.Default.Store, onNavigateToCreateAgency)
            AdminActionCard("Crea Agente", "Registra agente o capo.", Icons.Default.Badge, onNavigateToCreateAgent)
            AdminActionCard("Crea Amministratore", "Nuovo admin.", Icons.Default.PersonAdd, onNavigateToCreateAdmin)
            AdminActionCard("Cambia Password", "Aggiorna la tua password.", Icons.Default.LockReset, onNavigateToChangePassword)
        }
    }
}

@Composable
fun AdminActionCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column { Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(text = description, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

// --- CREAZIONE AGENZIA (Nuova con Dropdown Admin) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateAgencyScreen(
    navController: NavController,
    viewModel: AdminScreenViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var indirizzo by remember { mutableStateOf("") }

    // Admin Dropdown State
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
        topBar = { TopAppBar(title = { Text("Nuova Agenzia") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome Agenzia") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = indirizzo, onValueChange = { indirizzo = it }, label = { Text("Indirizzo Sede") }, modifier = Modifier.fillMaxWidth())

            // DROPDOWN AMMINISTRATORI
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedAdmin?.email ?: "Seleziona Amministratore",
                    onValueChange = {}, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    adminOptions.forEach { admin ->
                        DropdownMenuItem(
                            text = { Text(admin.email) },
                            onClick = { selectedAdmin = admin; expanded = false }
                        )
                    }
                }
            }

            Button(
                onClick = { selectedAdmin?.let { viewModel.createAgency(nome, indirizzo, it.id) } },
                modifier = Modifier.fillMaxWidth(),
                enabled = operationState !is AdminOperationState.Loading && nome.isNotBlank() && selectedAdmin != null
            ) {
                Text(if (operationState is AdminOperationState.Loading) "Caricamento..." else "Crea Agenzia")
            }
        }
    }
}

// --- CREAZIONE AGENTE (Dropdown Agenzie) ---
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
        topBar = { TopAppBar(title = { Text("Nuovo Agente") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = cognome, onValueChange = { cognome = it }, label = { Text("Cognome") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedAgenzia?.nome ?: "Seleziona Agenzia",
                    onValueChange = {}, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    agenzieOptions.forEach { agenzia ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(agenzia.nome)
                                    if (agenzia.haCapo) Text("Ha già un capo", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            onClick = { selectedAgenzia = agenzia; if (agenzia.haCapo) isCapo = false; expanded = false }
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = isCapo, onCheckedChange = { isCapo = it }, enabled = selectedAgenzia != null && !selectedAgenzia!!.haCapo)
                Column {
                    Text("Assegna ruolo Capo Agenzia")
                    if (selectedAgenzia != null && selectedAgenzia!!.haCapo) Text("Non disponibile: l'agenzia ha già un capo.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                }
            }

            Button(onClick = { selectedAgenzia?.let { viewModel.createAgent(nome, cognome, email, password, it.id, isCapo) } }, modifier = Modifier.fillMaxWidth(), enabled = operationState !is AdminOperationState.Loading && selectedAgenzia != null) {
                Text(if (operationState is AdminOperationState.Loading) "Caricamento..." else "Crea Agente")
            }
        }
    }
}

// --- CAMBIO PASSWORD (SOLO SE STESSI) ---
@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = { TopAppBar(title = { Text("Cambia Password") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Modifica la tua password di accesso.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(value = oldPass, onValueChange = { oldPass = it }, label = { Text("Vecchia Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
            OutlinedTextField(value = newPass, onValueChange = { newPass = it }, label = { Text("Nuova Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
            OutlinedTextField(value = confirmPass, onValueChange = { confirmPass = it }, label = { Text("Conferma Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

            Button(
                onClick = { viewModel.changeMyPassword(oldPass, newPass) },
                modifier = Modifier.fillMaxWidth(),
                enabled = newPass.isNotEmpty() && newPass == confirmPass && operationState !is AdminOperationState.Loading
            ) {
                Text(if (operationState is AdminOperationState.Loading) "Caricamento..." else "Aggiorna Password")
            }
        }
    }
}

// --- CREAZIONE ADMIN (Invariata) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateAdminScreen(navController: NavController, viewModel: AdminScreenViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val operationState by viewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(operationState) {
        if (operationState is AdminOperationState.Success) {
            snackbarHostState.showSnackbar((operationState as AdminOperationState.Success).message); email = ""; password = ""; viewModel.resetState()
        } else if (operationState is AdminOperationState.Error) {
            snackbarHostState.showSnackbar((operationState as AdminOperationState.Error).message); viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Nuovo Amministratore") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { viewModel.createAdmin(email, password) }, modifier = Modifier.fillMaxWidth(), enabled = operationState !is AdminOperationState.Loading) {
                Text(if (operationState is AdminOperationState.Loading) "Caricamento..." else "Crea Amministratore")
            }
        }
    }
}