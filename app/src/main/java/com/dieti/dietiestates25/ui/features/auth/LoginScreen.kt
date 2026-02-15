package com.dieti.dietiestates25.ui.features.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun LoginScreen(
    navController: NavController? = null,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.state.observeAsState(RegisterState.Idle)
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // --- LOGICA DI NAVIGAZIONE E SMISTAMENTO ---
    LaunchedEffect(authState) {
        when (val state = authState) {
            is RegisterState.Success -> {
                val userId = state.utente.id     // UUID Reale
                val userRole = state.utente.ruolo // "ADMIN", "MANAGER", "UTENTE"
                val userName = state.utente.nome

                Toast.makeText(context, "Benvenuto $userName!", Toast.LENGTH_SHORT).show()

                val navOptions = androidx.navigation.navOptions {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }

                // SMISTAMENTO A 3 VIE
                when (userRole) {
                    "ADMIN" -> {
                        // 1. Admin -> Dashboard Admin
                        navController?.navigate(Screen.AdminDashboardScreen.route, navOptions)
                    }
                    "MANAGER" -> {
                        // 2. Manager -> ManagerScreen (Nuova logica separata)
                        // Assumiamo che ManagerScreen abbia il metodo helper withIdUtente come HomeScreen
                        navController?.navigate(Screen.ManagerScreen.withIdUtente(userId), navOptions)
                    }
                    "UTENTE" -> {
                        // 3. Utente -> HomeScreen Classica
                        navController?.navigate(Screen.HomeScreen.withIdUtente(userId), navOptions)
                    }
                    else -> {
                        // Fallback per ruoli sconosciuti o null -> Home Utente
                        navController?.navigate(Screen.HomeScreen.withIdUtente(userId), navOptions)
                    }
                }
            }
            is RegisterState.Error -> {
                Toast.makeText(context, state.errore, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    val isLoading = authState is RegisterState.Loading

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .background(colorScheme.primaryContainer)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .padding(Dimensions.paddingLarge)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            AppIconDisplay(
                size = Dimensions.logoLarge,
                shapeRadius = Dimensions.cornerRadiusMedium,
                modifier = Modifier.padding(bottom = Dimensions.spacingLarge, top = Dimensions.spacingLarge)
            )

            // Bottoni Login/Register
            Row(modifier = Modifier.padding(bottom = Dimensions.spacingLarge)) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) { Text("Login", color = colorScheme.onPrimary) }
                Spacer(modifier = Modifier.width(Dimensions.spacingMedium))
                Button(
                    onClick = { navController?.navigate(Screen.RegisterScreen.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryContainer),
                    enabled = !isLoading
                ) { Text("Registrati", color = colorScheme.onPrimaryContainer) }
            }

            // Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.spacingSmall)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.spacingSmall)
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.spacingMedium)) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it }, enabled = !isLoading, colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary))
                Text("Ricordami", color = colorScheme.onBackground)
            }

            AppPrimaryButton(
                text = if (isLoading) "Accesso in corso..." else "Login",
                onClick = { viewModel.eseguiLogin(email, password, rememberMe) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = Dimensions.spacingMedium)
            )
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = colorScheme.primary)
        }
    }
}