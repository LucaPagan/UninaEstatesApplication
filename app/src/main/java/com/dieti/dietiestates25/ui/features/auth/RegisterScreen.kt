package com.dieti.dietiestates25.ui.features.auth

import android.content.res.Configuration
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun RegisterScreenPreviewOnly() {
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

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

        // Bottoni Login/Sign Up
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

        // TextFields
        val fieldModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimensions.spacingSmall)

        val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
            unfocusedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
            focusedTextColor = colorScheme.onBackground,
            unfocusedTextColor = colorScheme.onBackground
        )

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome", color = colorScheme.onBackground) },
            singleLine = true,
            colors = fieldColors,
            modifier = fieldModifier
        )
        OutlinedTextField(
            value = cognome,
            onValueChange = { cognome = it },
            label = { Text("Cognome", color = colorScheme.onBackground) },
            singleLine = true,
            colors = fieldColors,
            modifier = fieldModifier
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = colorScheme.onBackground) },
            singleLine = true,
            colors = fieldColors,
            modifier = fieldModifier
        )
        OutlinedTextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Numero", color = colorScheme.onBackground) },
            singleLine = true,
            colors = fieldColors,
            modifier = fieldModifier
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = colorScheme.onBackground) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = colorScheme.onBackground)
            },
            colors = fieldColors,
            modifier = fieldModifier
        )

        // Bottone Register
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.spacingMedium)
        ) {
            Text("Registra", color = colorScheme.onPrimary)
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
                colors = CheckboxDefaults.colors(checkedColor = colorScheme.primary)
            )
            Text("Ricordami", color = colorScheme.onBackground)
        }
    }
}

@Preview(showBackground = true, name = "Register Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Register Dark")
@Composable
fun RegisterScreenPreview() {
    DietiEstatesTheme {
        RegisterScreenPreviewOnly()
    }
}
