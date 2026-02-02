package com.dieti.dietiestates25.ui.features.auth

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

// Importa il tuo ViewModel e lo Stato
// Assicurati che i package siano corretti nel tuo progetto
// import com.dieti.dietiestates25.ui.viewmodel.AuthViewModel
// import com.dieti.dietiestates25.ui.state.RegisterState

@Composable
fun RegisterScreen(
    navController: NavController? = null,
    // Iniettiamo il ViewModel qui. Se usi Hilt sarà hiltViewModel(), altrimenti viewModel() standard
    viewModel: AuthViewModel = viewModel()
) {
    // Stati UI locali per i campi di testo
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") } // Telefono
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) } // Per mostrare/nascondere pw

    // Osserviamo lo stato dal ViewModel
    val registerState by viewModel.state.observeAsState(RegisterState.Idle)

    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    // GESTIONE DEGLI EFFETTI COLLATERALI (Navigazione e Errori)
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Success -> {
                // Recuperiamo l'ID dall'oggetto utente ricevuto
                val userId = state.utente.id
                Toast.makeText(context, "Benvenuto ${state.utente.nome}!", Toast.LENGTH_SHORT).show()

                // Navigazione alla WelcomeView passando l'ID
                // Assicurati che nel tuo NavHost la rotta sia definita come "welcome_view/{userId}"
                navController?.navigate("welcome_view/$userId") {
                    // Opzionale: Rimuove la schermata di registrazione dal backstack così se torna indietro esce dall'app o va al login
                    popUpTo("register_screen") { inclusive = true }
                }

                // Resettiamo lo stato per evitare loop se si torna indietro (opzionale)
                //viewModel.resetState()
            }
            is RegisterState.Error -> {
                Toast.makeText(context, state.errore, Toast.LENGTH_LONG).show()
            }
            else -> {
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .padding(Dimensions.paddingLarge)
                .verticalScroll(rememberScrollState()), // Rende la colonna scrollabile
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo App
            AppIconDisplay(
                size = Dimensions.logoLarge,
                shapeRadius = Dimensions.cornerRadiusMedium,
                modifier = Modifier.padding(bottom = Dimensions.spacingLarge)
            )

            // Bottoni Login/Sign Up
            Row(modifier = Modifier.padding(bottom = Dimensions.spacingLarge)) {
                Button(
                    onClick = { navController?.navigate("login_screen") }, // Naviga al login
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    Text("Login", color = colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.width(Dimensions.spacingMedium))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryContainer)
                ) {
                    Text("Registrati", color = colorScheme.onPrimaryContainer)
                }
            }

            // --- FORM DI REGISTRAZIONE ---
            val fieldModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.spacingSmall)

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                unfocusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                focusedTextColor = colorScheme.onBackground,
                unfocusedTextColor = colorScheme.onBackground
            )

            // Se è in caricamento, disabilitiamo i campi (opzionale)
            val isLoading = registerState is RegisterState.Loading

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                singleLine = true,
                colors = fieldColors,
                modifier = fieldModifier,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = cognome,
                onValueChange = { cognome = it },
                label = { Text("Cognome") },
                singleLine = true,
                colors = fieldColors,
                modifier = fieldModifier,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                colors = fieldColors,
                modifier = fieldModifier,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = numero,
                onValueChange = { numero = it },
                label = { Text("Numero di Telefono") },
                singleLine = true,
                colors = fieldColors,
                modifier = fieldModifier,
                enabled = !isLoading
            )

            // Password con toggle visibility
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = colorScheme.onBackground)
                    }
                },
                colors = fieldColors,
                modifier = fieldModifier,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Bottone Register
            Button(
                onClick = {
                    // CHIAMATA AL VIEWMODEL
                    viewModel.eseguiRegistrazione(
                        nome = nome,
                        cognome = cognome,
                        email = email,
                        pass = password,
                        telefono = numero
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                enabled = !isLoading, // Disabilita se sta caricando
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.spacingMedium)
            ) {
                Text(if (isLoading) "Registrazione in corso..." else "Registra", color = colorScheme.onPrimary)
            }

            // Checkbox Remember Me
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.spacingMedium)
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary),
                    enabled = !isLoading
                )
                Text("Ricordami", color = colorScheme.onBackground)
            }
        }

        // --- LOADING INDICATOR ---
        // Mostra una rotellina al centro sopra tutto se sta caricando
        if (registerState is RegisterState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary
            )
        }
    }
}