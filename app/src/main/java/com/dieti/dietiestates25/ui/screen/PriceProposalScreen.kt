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
import com.dieti.dietiestates25.ui.theme.*

@Composable
fun PriceProposalScreen(
    onDismiss: () -> Unit = {},
    onContinue: (Double) -> Unit = {}
) {
    var proposedPrice by remember { mutableStateOf("110.000") }
    val startingPrice = 129500.0

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(16.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = TextGray
                    )
                }
                Text(
                    text = "Proponi prezzo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Divider(color = SurfaceGray, thickness = 1.dp)

            // Information card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = OffWhite),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = "Proponi un nuovo prezzo all'inserzionista, senta impegno, adatto al tuo budget",
                    modifier = Modifier.padding(16.dp),
                    color = TextGray
                )
            }

            Divider(color = SurfaceGray, thickness = 1.dp)

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
                    color = TextGray
                )
                Text(
                    text = "€${String.format("%,.0f", startingPrice)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGray
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
                    color = TextGray
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
                            color = TealPrimary,
                            modifier = Modifier.padding(end = 4.dp)
                        )

                        Text(
                            text = "€$proposedPrice",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TealPrimary
                        )

                        IconButton(
                            onClick = { /* Cancel action */ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Cancella proposta",
                                tint = TealPrimary
                            )
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .width(160.dp)
                            .padding(top = 4.dp),
                        color = TealLightest,
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
                    color = TextGray
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Black)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "0%",
                        color = White,
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
                colors = CardDefaults.cardColors(containerColor = OffWhite),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text(
                    text = "Il prezzo è stato elaborato da un professionista immobiliare. Per mantenere coerenza con il mercato, puoi fare un'offerta con una variazione massima del 15%",
                    modifier = Modifier.padding(16.dp),
                    color = TextGray,
                    fontSize = 14.sp
                )
            }

            // Continue button
            Button(
                onClick = {
                    val price = proposedPrice.replace(".", "").toDoubleOrNull() ?: 0.0
                    onContinue(price)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text(
                    text = "Prosegui",
                    color = White,
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
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        PriceProposalScreen()
    }
}