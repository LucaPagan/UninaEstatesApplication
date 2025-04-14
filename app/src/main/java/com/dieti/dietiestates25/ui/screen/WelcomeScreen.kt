package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.R

// Assicurati che questi colori siano accessibili
val TealPrimary = Color(0xFF00796B)
val OffWhite = Color(0xFFF5F5F5)
val TealLightest = Color(0xFFB2DFDB)

@Composable
fun WelcomeScreen() {

    val colorStops = arrayOf(
        0.0f to TealPrimary,
        0.20f to OffWhite,  // Raggiunge bianco al 20%
        0.60f to OffWhite,  // Mantiene bianco fino al 60% (copre il centro)
        1.0f to TealPrimary // Sfuma verso Teal
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colorStops = colorStops)),
    ) {
        // --- Immagine ---
        Image(
            painter = painterResource(id = R.drawable.welcome_image),
            contentDescription = "Illustrazione casa",
            modifier = Modifier
                .align(Alignment.Center) // <-- Allinea l'immagine al centro del Box genitore
                .fillMaxWidth(0.9f)      // Definisci la larghezza desiderata
                .aspectRatio(1f / 0.8f)
                .padding(bottom = 8.dp), // Mantieni le proporzioni
            contentScale = ContentScale.Fit
        )

        // --- Testo e Bottone ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Allinea l'intera colonna in basso al centro
                .fillMaxWidth()               // Occupa tutta la larghezza
                .padding(horizontal = 32.dp)  // Padding laterale
                .padding(bottom = 30.dp),     // Padding dal fondo per staccare il bottone (aggiusta se serve)
            horizontalAlignment = Alignment.CenterHorizontally // Centra gli elementi *dentro* la colonna
        ) {
            Text(
                text = "Benvenuto",
                color = Color.White, // Si troverà sulla parte Teal inferiore
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Compra la tua casa dei sogni",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { /* Azione */ },
                modifier = Modifier
                    .widthIn(max = 300.dp) // Manteniamo la larghezza massima
                    .height(50.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(25),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealLightest.copy(alpha = 0.95f),
                    contentColor = TealPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = "Continua",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun DefaultImageCenteredPreview() {
    WelcomeScreen()
}