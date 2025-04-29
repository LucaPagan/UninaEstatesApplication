package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.*

@Composable
fun PriceProposalScreen(
    navController : NavController
) {
    var proposedPrice by remember { mutableStateOf("110.000") }
    val startingPrice = 129500.0

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White // Using standard Color.White instead of custom White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Using standard Color.White
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
                    navController.navigate(Screen.PropertyScreen.route)
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = GrayBlue // Changed from TextGray to GrayBlue
                    )
                }
                Text(
                    text = "Proponi prezzo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue, // Changed from TextGray to GrayBlue
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Divider(color = NeutralLight, thickness = 1.dp) // Changed from SurfaceGray to NeutralLight

            // Information card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = NeutralLight), // Changed from OffWhite to NeutralLight
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = "Proponi un nuovo prezzo all'inserzionista, senta impegno, adatto al tuo budget",
                    modifier = Modifier.padding(16.dp),
                    color = GrayBlue // Changed from TextGray to GrayBlue
                )
            }

            Divider(color = NeutralLight, thickness = 1.dp) // Changed from SurfaceGray to NeutralLight

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
                    color = GrayBlue // Changed from TextGray to GrayBlue
                )
                Text(
                    text = "€${String.format("%,.0f", startingPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue // Changed from TextGray to GrayBlue
                )
            }

            // Your proposal
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
                    color = GrayBlue // Changed from TextGray to GrayBlue
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Proposta",
                            fontSize = 12.sp,
                            color = TealVibrant, // Changed from TealPrimary to TealVibrant
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        Text(
                            text = "€$proposedPrice",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TealVibrant // Changed from TealPrimary to TealVibrant
                        )

                        IconButton(
                            onClick = { /* Cancel action */ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Cancella proposta",
                                tint = TealVibrant // Changed from TealPrimary to TealVibrant
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .width(160.dp)
                            .padding(top = 4.dp),
                        color = TealLight, // Changed from TealLightest to TealLight
                        thickness = 1.dp
                    )
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
                    color = GrayBlue // Changed from TextGray to GrayBlue
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black) // Using standard Color.Black
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "0%",
                        color = Color.White, // Using standard Color.White
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
                colors = CardDefaults.cardColors(containerColor = NeutralLight), // Changed from OffWhite to NeutralLight
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = "Il prezzo è stato elaborato da un professionista immobiliare. Per mantenere coerenza con il mercato, puoi fare un'offerta con una variazione massima del 15%",
                    modifier = Modifier.padding(16.dp),
                    color = GrayBlue, // Changed from TextGray to GrayBlue
                    fontSize = 14.sp
                )
            }

            // Continue button
            Button(
                onClick = {
                    val price = proposedPrice.replace(".", "").toDoubleOrNull() ?: 0.0
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealVibrant) // Changed from TealPrimary to TealVibrant
            ) {
                Text(
                    text = "Prosegui",
                    color = Color.White, // Using standard Color.White
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