package com.dieti.dietiestates25.ui.features.auth

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions


@Composable
fun LoginScreen(
    navController: NavController? = null,
    // Iniettiamo il ViewModel (lo stesso usato per la registrazione)
    viewModel: AuthViewModel = viewModel()
) {
    // Stati UI locali
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    // Aggiunto stato per visibilità password
    var passwordVisible by remember { mutableStateOf(false) }

    // Osserviamo lo stato dal ViewModel (Idle, Loading, Success, Error)
    val authState by viewModel.state.observeAsState(RegisterState.Idle)

    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // --- LOGICA DI NAVIGAZIONE AUTOMATICA ---
    // Questo blocco osserva lo stato. Se diventa Success, naviga alla Home.
    LaunchedEffect(authState) {
        when (val state = authState) {
            is RegisterState.Success -> {
                val userId = state.utente.id
                Toast.makeText(context, "Benvenuto ${state.utente.nome}!", Toast.LENGTH_SHORT).show()

                // Navigazione verso la Home passando l'ID
                // popUpTo(0) rimuove Login e Intro dal tasto "Indietro"
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

    // Variabile per disabilitare i controlli durante il caricamento
    val isLoading = authState is RegisterState.Loading

    // Box root per sovrapporre il caricamento
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
                .padding(Dimensions.paddingLarge)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo App
            AppIconDisplay(
                size = Dimensions.logoLarge,
                shapeRadius = Dimensions.cornerRadiusMedium,
                modifier = Modifier.padding(bottom = Dimensions.spacingLarge)
            )

            // Bottoni Login e Sign Up (Top Bar)
            Row(modifier = Modifier.padding(bottom = Dimensions.spacingLarge)) {
                Button(
                    onClick = { /* Siamo già nel login, non fa nulla */ },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                ) {
                    Text("Login", color = colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.width(Dimensions.spacingMedium))
                Button(
                    onClick = {
                        // Navigazione verso la schermata di Registrazione
                        navController?.navigate("register_screen")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primaryContainer),
                    enabled = !isLoading
                ) {
                    Text("Registrati", color = colorScheme.onPrimaryContainer)
                }
            }

            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = colorScheme.onBackground) },
                singleLine = true,
                enabled = !isLoading, // Disabilita se carica
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                    unfocusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                    focusedTextColor = colorScheme.onBackground,
                    unfocusedTextColor = colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.spacingSmall)
            )

            // Campo Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = colorScheme.onBackground) },
                singleLine = true,
                enabled = !isLoading, // Disabilita se carica
                // Logica per mostrare/nascondere password
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password",
                            tint = colorScheme.onBackground
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                    unfocusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                    focusedTextColor = colorScheme.onBackground,
                    unfocusedTextColor = colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.spacingSmall)
            )

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
                    enabled = !isLoading,
                    colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary)
                )
                Text("Ricordami", color = colorScheme.onBackground)
            }

            // Bottone Login Principale
            Button(
                onClick = {
                    // CHIAMATA AL VIEWMODEL PER IL LOGIN
                    viewModel.eseguiLogin(email, password, rememberMe)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                enabled = !isLoading, // Importante: evita doppi click
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.spacingMedium)
            ) {
                Text(if (isLoading) "Accesso in corso..." else "Login", color = colorScheme.onPrimary)
            }

            // Separatore
            Text(
                "Oppure",
                color = colorScheme.onBackground.copy(alpha = 0.6f),
                style = typography.bodyMedium,
                modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
            )

            // Bottone Google (UI only per ora)
            Button(
                onClick = { /* Logica Google futura */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(
                    Dimensions.borderStrokeSmall,
                    colorScheme.onBackground.copy(alpha = 0.3f)
                ),
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimensions.spacingSmall)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(Dimensions.iconSizeLarge)
                        .padding(end = Dimensions.spacingSmall),
                    contentScale = ContentScale.Fit
                )
                Text("Continua con Google", color = Color.Black)
            }
        }

        // INDICATORE DI CARICAMENTO (Sovrapposto)
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary
            )
        }
    }
}

// Preview per il design (senza logica)
@Preview(showBackground = true, name = "Login Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Login Dark")
@Composable
fun LoginScreenPreview() {
    DietiEstatesTheme {
        LoginScreen()
    }
}