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
import com.dieti.dietiestates25.ui.components.AppPrimaryButton // Import aggiunto
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun RegisterScreen(
    navController: NavController? = null,
    viewModel: AuthViewModel = viewModel(),
) {
    // Stati UI locali
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Osserviamo lo stato dal ViewModel
    val registerState by viewModel.state.observeAsState(RegisterState.Idle)

    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    // GESTIONE DEGLI EFFETTI COLLATERALI
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Success -> {
                val userId = state.utente.id
                Toast.makeText(context, "Benvenuto ${state.utente.nome}!", Toast.LENGTH_SHORT)
                    .show()
                navController?.navigate(Screen.HomeScreen.route + "/$userId") {
                    popUpTo(0) { inclusive = true }
                }
            }

            is RegisterState.Error -> {
                Toast.makeText(context, state.errore, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBars)
            .background(colorScheme.primaryContainer)
    )

    // Contenitore principale
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppGradients.primaryToBackground)
            .imePadding() // <--- MODIFICA CRITICA: Ridimensiona la Box quando esce la tastiera
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Permette lo scroll del contenuto
                .padding(horizontal = Dimensions.paddingLarge, vertical = Dimensions.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo App
            AppIconDisplay(
                size = Dimensions.logoLarge,
                shapeRadius = Dimensions.cornerRadiusMedium,
                modifier = Modifier.padding(
                    bottom = Dimensions.spacingLarge,
                    top = Dimensions.spacingLarge
                )
            )

            // Bottoni Login/Sign Up
            Row(modifier = Modifier.padding(bottom = Dimensions.spacingLarge)) {
                Button(
                    onClick = { navController?.navigate(Screen.LoginScreen.route) },
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

            // --- FORM ---
            val fieldModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.spacingSmall)

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                unfocusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                focusedTextColor = colorScheme.onBackground,
                unfocusedTextColor = colorScheme.onBackground
            )

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

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = null,
                            tint = colorScheme.onBackground
                        )
                    }
                },
                colors = fieldColors,
                modifier = fieldModifier,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(Dimensions.spacingMedium))

            // Checkbox
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

            // Bottone Registra
            AppPrimaryButton(
                text = if (isLoading) "Registrazione in corso..." else "Registra",
                onClick = {
                    viewModel.eseguiRegistrazione(
                        nome = nome,
                        cognome = cognome,
                        email = email,
                        pass = password,
                        telefono = numero,
                        rememberMe = rememberMe
                    )
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.spacingMedium)
            )

            // Spazio extra per scrollare oltre la tastiera
            Spacer(modifier = Modifier.height(Dimensions.spacingExtraLarge))
        }

        // Loading Indicator
        if (registerState is RegisterState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary
            )
        }
    }

}