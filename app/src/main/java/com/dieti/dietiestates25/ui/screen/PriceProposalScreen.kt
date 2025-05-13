package com.dieti.dietiestates25.ui.screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

import android.app.Activity

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceProposalScreen(
    navController: NavController
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        var proposedPrice by remember { mutableStateOf("") }
        var isPriceFieldFocused by remember { mutableStateOf(false) }
        val placeholder = "110.000"
        val startingPrice = 129500.0
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val haptic = LocalHapticFeedback.current
        val view = LocalView.current

        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                colorScheme.primary.toArgb().also { window.statusBarColor = it }
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                colorScheme.primary.toArgb().also { window.navigationBarColor = it }
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }

        Scaffold(
            // Gestisce automaticamente insets per status bar, navigation bar e keyboard

            modifier = Modifier.fillMaxSize(),


            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Proponi prezzo",
                            style = typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Chiudi"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.surface,
                        titleContentColor = colorScheme.onBackground
                    ),
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    HorizontalDivider(color = colorScheme.onBackground, thickness = 1.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorScheme.background)
                            .padding(16.dp),
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val price =
                                    if (proposedPrice.isEmpty()) placeholder.replace(".", "").toDoubleOrNull()
                                        ?: 0.0
                                    else proposedPrice.replace(".", "").toDoubleOrNull() ?: 0.0
                                // Qui puoi usare il prezzo per le operazioni successive
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Prosegui",
                                color = colorScheme.onPrimary,
                                style = typography.labelLarge
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.surface)
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding() // Gestisce la tastiera
                ) {
                    Divider(color = colorScheme.onBackground, thickness = 1.dp)

                    // Information card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colorScheme.outline)
                    ) {
                        Text(
                            text = "Proponi un nuovo prezzo all'inserzionista, senza impegno, adatto al tuo budget",
                            modifier = Modifier.padding(16.dp),
                            style = typography.bodyMedium,
                            color = colorScheme.onSecondary
                        )
                    }

                    // Starting price
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prezzo di partenza",
                            style = typography.bodyLarge,
                            color = colorScheme.onBackground
                        )
                        Text(
                            text = "€${String.format("%,.0f", startingPrice)}",
                            style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = colorScheme.onBackground
                        )
                    }

                    // Your proposal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "La tua proposta",
                            style = typography.bodyLarge,
                            color = colorScheme.onBackground
                        )

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier.width(160.dp),
                                ) {
                                    // Animazione del bordo
                                    val infiniteTransition = rememberInfiniteTransition(label = "border-animation")
                                    val borderWidth by infiniteTransition.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 2f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(durationMillis = 500, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "border-width"
                                    )

                                    OutlinedTextField(
                                        value = proposedPrice,
                                        onValueChange = { newValue ->
                                            // Accetta solo numeri e punti
                                            if (newValue.all { it.isDigit() || it == '.' }) {
                                                if (newValue.isNotEmpty()) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                }
                                                proposedPrice = newValue
                                            }
                                        },
                                        placeholder = {
                                            if (proposedPrice.isEmpty()) {
                                                Text(
                                                    text = placeholder,
                                                    style = typography.bodyLarge.copy(
                                                        textAlign = TextAlign.Start
                                                    ),
                                                    color = colorScheme.primary.copy(alpha = 0.5f)
                                                )
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = colorScheme.surfaceDim, // Nascondiamo il bordo predefinito
                                            unfocusedBorderColor = colorScheme.primary.copy(alpha = 0.6f), // Bordo visibile quando non è in focus
                                            cursorColor = colorScheme.primary,
                                            focusedTextColor = colorScheme.primary,
                                            unfocusedTextColor = colorScheme.primary
                                        ),
                                        textStyle = typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Start
                                        ),
                                        prefix = { Text("€", color = colorScheme.primary) },
                                        modifier = Modifier
                                            .width(130.dp)
                                            .onFocusChanged { focusState ->
                                                isPriceFieldFocused = focusState.isFocused
                                                if (focusState.isFocused) {
                                                    keyboardController?.show()
                                                }
                                            }
                                            .then(
                                                if (isPriceFieldFocused) {
                                                    Modifier.border(
                                                        width = borderWidth.dp,
                                                        color = colorScheme.primary,
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                } else {
                                                    Modifier.border(
                                                        width = 1.dp,
                                                        color = colorScheme.primary.copy(alpha = 0.6f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                }
                                            ),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            proposedPrice = ""
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Cancella proposta",
                                            tint = colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Price difference
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Differenza di prezzo",
                            style = typography.bodyLarge,
                            color = colorScheme.onBackground
                        )

                        // Calcolare la differenza percentuale quando c'è un valore
                        val differencePercent = if (proposedPrice.isNotEmpty()) {
                            try {
                                val proposedPriceValue = proposedPrice.replace(".", "").toDouble()
                                val calculatedPercent = (proposedPriceValue - startingPrice) / startingPrice * 100
                                // Limita il valore massimo a 100.0
                                minOf(calculatedPercent, 100.0)
                            } catch (e: Exception) {
                                0.0
                            }
                        } else {
                            0.0
                        }

                        // Formatta il testo della percentuale
                        val priceDifference = String.format("%.1f", differencePercent) + "%"

                        // Scegli il colore in base alla percentuale
                        val differenceColor = when {
                            differencePercent > 0.0 -> colorScheme.onBackground // Grigio
                            differencePercent == 0.0 -> colorScheme.onBackground // Grigio
                            differencePercent > -12.0 && differencePercent < 0.0 -> colorScheme.primary
                            differencePercent >= -15.0 && differencePercent <= -12.0 -> colorScheme.tertiary // Orange
                            else -> colorScheme.error
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(differenceColor)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = priceDifference,
                                color = colorScheme.surface,
                                style = typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Information note
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colorScheme.outline)
                    ) {
                        Text(
                            text = "Il prezzo è stato elaborato da un professionista immobiliare. Per mantenere coerenza con il mercato, puoi fare un'offerta con una variazione massima del 15%",
                            modifier = Modifier.padding(16.dp),
                            style = typography.bodySmall,
                            color = colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PriceProposalScreenPreview() {
    val navController = rememberNavController()
    PriceProposalScreen(navController = navController)
}