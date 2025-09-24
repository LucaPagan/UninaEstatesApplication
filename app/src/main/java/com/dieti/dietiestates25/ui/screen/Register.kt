package com.dieti.dietiestates25.ui.screen

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
import androidx.compose.ui.graphics.Color
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
        // Logo App al posto del testo
        AppIconDisplay(
            size = Dimensions.logoLarge,
            shapeRadius = Dimensions.cornerRadiusMedium,
            modifier = Modifier.padding(bottom = Dimensions.spacingLarge)
        )

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
                Text("Sign Up", color = colorScheme.onPrimaryContainer)
            }
        }

        // --- TextFields ---
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome", color = colorScheme.onBackground) },
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

        OutlinedTextField(
            value = cognome,
            onValueChange = { cognome = it },
            label = { Text("Cognome", color = colorScheme.onBackground) },
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

        OutlinedTextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Numero", color = colorScheme.onBackground) },
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

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = colorScheme.onBackground) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = colorScheme.onBackground)
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

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.spacingMedium)
        ) {
            Text("Register", color = colorScheme.onPrimary)
        }

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
            Text("Remember me", color = colorScheme.onBackground)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    DietiEstatesTheme {
        RegisterScreenPreviewOnly()
    }
}
