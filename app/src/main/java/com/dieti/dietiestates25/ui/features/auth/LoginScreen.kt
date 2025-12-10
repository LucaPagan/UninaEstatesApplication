package com.dieti.dietiestates25.ui.features.auth

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun LoginScreenPreviewOnly() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    // Questi si adattano automaticamente al tema chiaro/scuro
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

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

        // Bottoni Login e Sign Up
        Row(modifier = Modifier.padding(bottom = Dimensions.spacingLarge)) {
            Button(
                onClick = {},
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

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = colorScheme.onBackground) },
            singleLine = true,
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
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = colorScheme.onBackground
                )
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
                colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary)
            )
            Text("Ricordami", color = colorScheme.onBackground)
        }

        // Bottone Login
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.spacingMedium)
        ) {
            Text("Login", color = colorScheme.onPrimary)
        }

        // Separatore
        Text(
            "Oppure",
            color = colorScheme.onBackground.copy(alpha = 0.6f),
            style = typography.bodyMedium,
            modifier = Modifier.padding(bottom = Dimensions.spacingMedium)
        )

        // Bottone Apple → SEMPRE NERO
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black), // forzato nero
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.spacingSmall)
        ) {
            Image(
                painter = painterResource(id = R.drawable.apple),
                contentDescription = "Apple Logo",
                modifier = Modifier
                    .size(Dimensions.iconSizeLarge)
                    .padding(end = Dimensions.spacingSmall),
                contentScale = ContentScale.Fit
            )
            Text("Continua con Apple", color = Color.White) // testo bianco su sfondo nero
        }

        // Bottone Google → SEMPRE BIANCO
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.White), // forzato bianco
            border = BorderStroke(Dimensions.borderStrokeSmall, colorScheme.onBackground.copy(alpha = 0.3f)),
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
            Text("Continua con Google", color = Color.Black) // testo nero su sfondo bianco
        }

        // Bottone Facebook
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.facebook),
                contentDescription = "Facebook Logo",
                modifier = Modifier
                    .size(Dimensions.iconSizeLarge)
                    .padding(end = Dimensions.spacingSmall),
                contentScale = ContentScale.Fit
            )
            Text("Continua con Facebook", color = Color.White)
        }
    }
}

@Preview(showBackground = true, name = "Login Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Login Dark")
@Composable
fun LoginScreenPreview() {
    DietiEstatesTheme {
        LoginScreenPreviewOnly()
    }
}
