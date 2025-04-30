package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.*

@Composable
fun PriceProposalScreen(
    navController: NavController
) {
    var proposedPrice by remember { mutableStateOf("") }
    var isPriceFieldFocused by remember { mutableStateOf(false) }
    val placeholder = "110.000"
    val startingPrice = 129500.0
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = GrayBlue
                    )
                }
                Text(
                    text = "Proponi prezzo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Divider(color = NeutralLight, thickness = 1.dp)

            // Information card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = NeutralLight),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = "Proponi un nuovo prezzo all'inserzionista, senta impegno, adatto al tuo budget",
                    modifier = Modifier.padding(16.dp),
                    color = GrayBlue
                )
            }

            Divider(color = NeutralLight, thickness = 1.dp)

            // Starting price
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Prezzo di partenza",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue
                )
                Text(
                    text = "€${String.format("%,.0f", startingPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue
                )
            }

            // Your proposal - MODIFICATO
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "La tua proposta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue
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
                            modifier = Modifier.width(160.dp)
                        ) {
                            OutlinedTextField(
                                value = proposedPrice,
                                onValueChange = { newValue ->
                                    // Accetta solo numeri e punti
                                    if (newValue.all { it.isDigit() || it == '.' }) {
                                        proposedPrice = newValue
                                    }
                                },
                                placeholder = {
                                    if (proposedPrice.isEmpty()) {
                                        Text(
                                            text = placeholder,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TealVibrant.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TealVibrant,
                                    unfocusedBorderColor = Color.Transparent,
                                    cursorColor = TealVibrant,
                                    focusedTextColor = TealVibrant,
                                    unfocusedTextColor = TealVibrant
                                ),
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Start
                                ),
                                prefix = { Text("€", color = TealVibrant) },
                                modifier = Modifier
                                    .width(130.dp)
                                    .onFocusChanged { focusState ->
                                        isPriceFieldFocused = focusState.isFocused
                                        if (focusState.isFocused) {
                                            keyboardController?.show()
                                        }
                                    },
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    proposedPrice = ""
                                    focusManager.clearFocus()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Cancella proposta",
                                    tint = TealVibrant
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
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Differenza di prezzo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue
                )

                // Calcolare la differenza percentuale quando c'è un valore
                val priceDifference = if (proposedPrice.isNotEmpty()) {
                    try {
                        val proposedPriceValue = proposedPrice.replace(".", "").toDouble()
                        val difference = (proposedPriceValue - startingPrice) / startingPrice * 100
                        String.format("%.0f", difference) + "%"
                    } catch (e: Exception) {
                        "0%"
                    }
                } else {
                    "0%"
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (priceDifference == "0%") {
                                Color.Black
                            } else if (priceDifference >= "-1%" || priceDifference <= "-12%") {
                                Color.Green
                            } else if (priceDifference >= "-13%" || priceDifference <= "-15%") {
                                Color.Blue
                            } else {
                                Color.Red
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = priceDifference,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Information note
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = NeutralLight),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = "Il prezzo è stato elaborato da un professionista immobiliare. Per mantenere coerenza con il mercato, puoi fare un'offerta con una variazione massima del 15%",
                    modifier = Modifier.padding(16.dp),
                    color = GrayBlue,
                    fontSize = 14.sp
                )
            }

            // Continue button
            Button(
                onClick = {
                    val price =
                        if (proposedPrice.isEmpty()) placeholder.replace(".", "").toDoubleOrNull()
                            ?: 0.0
                        else proposedPrice.replace(".", "").toDoubleOrNull() ?: 0.0
                    // Qui puoi usare il prezzo per le operazioni successive
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealVibrant)
            ) {
                Text(
                    text = "Prosegui",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PriceProposalScreenPreview() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        PriceProposalScreen(navController = navController)
    }
}