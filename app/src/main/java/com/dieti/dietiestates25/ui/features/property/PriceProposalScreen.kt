package com.dieti.dietiestates25.ui.features.property

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.AppOutlinedTextField
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.Dimensions
// Import necessari per correggere gli errori 'getValue' e 'collectAsState'
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceProposalScreen(
    navController: NavController,
    immobileId: String?,
    viewModel: PriceProposalViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val context = LocalContext.current

    // Caricamento dati immobile
    val immobileData by viewModel.immobileData.collectAsState()

    LaunchedEffect(immobileId) {
        if (immobileId != null) {
            viewModel.loadImmobileData(immobileId)
        }
    }

    // Prezzo base dinamico
    val startingPrice = immobileData?.prezzo?.toDouble() ?: 0.0
    val placeholder = immobileData?.prezzo?.let { String.format("%,d", it) } ?: "0"

    var proposedPrice by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    // Ora questo funziona perché abbiamo gli import corretti
    val uiState by viewModel.proposalState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is ProposalState.Success -> {
                navController.popBackStack()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GeneralHeaderBar(
                title = "Proponi prezzo",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background)
                        .padding(dimensions.paddingMedium),
                ) {
                    AppPrimaryButton(
                        onClick = {
                            if (uiState !is ProposalState.Loading && immobileId != null) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val finalPrice = if (proposedPrice.isEmpty()) placeholder else proposedPrice
                                viewModel.sendProposal(immobileId, finalPrice, context = context)
                            }
                        },
                        text = if (uiState is ProposalState.Loading) "Invio in corso..." else "Proponi",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState !is ProposalState.Loading
                    )
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
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                // Gestione Errore
                if (uiState is ProposalState.Error) {
                    val errorMessage = (uiState as ProposalState.Error).message
                    Text(
                        text = errorMessage,
                        color = colorScheme.error,
                        modifier = Modifier.padding(dimensions.paddingMedium)
                    )
                }

                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(dimensions.paddingMedium),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.secondary),
                    shape = RoundedCornerShape(dimensions.cornerRadiusSmall),
                    border = BorderStroke(dimensions.borderStrokeSmall, colorScheme.outline)
                ) {
                    Text(
                        text = "Proponi un nuovo prezzo all'inserzionista, senza impegno, adatto al tuo budget",
                        modifier = Modifier.padding(dimensions.paddingMedium),
                        style = typography.bodyMedium,
                        color = colorScheme.onSecondary
                    )
                }

                // Starting Price Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Prezzo di partenza", style = typography.bodyLarge, color = colorScheme.onBackground)
                    Text(
                        text = if(startingPrice > 0.0) "€${String.format("%,.0f", startingPrice)}" else "Caricamento...",
                        style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onBackground
                    )
                }

                // Proposal Input Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = dimensions.paddingMedium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("La tua proposta", style = typography.bodyLarge, color = colorScheme.onBackground)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.width(dimensions.infoCardHeight),
                    ) {
                        AppOutlinedTextField(
                            value = proposedPrice,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() || it == '.' }) {
                                    if (newValue.isNotEmpty()) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    proposedPrice = newValue
                                }
                            },
                            placeholder = {
                                if (proposedPrice.isEmpty()) Text(placeholder, style = typography.bodyLarge, color = colorScheme.primary.copy(alpha = 0.5f))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colorScheme = colorScheme,
                            typography = typography
                        )
                        IconButton(onClick = { proposedPrice = ""; focusManager.clearFocus() }) {
                            Icon(Icons.Default.Cancel, "Cancella", tint = colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            if (uiState is ProposalState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            }
        }
    }
}