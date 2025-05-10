package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

@Composable
fun WelcomeScreen(navController: NavController) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        // Sfumatura perfezionata: transizione morbida tra primary e background
        val gradientColors = listOf(
            colorScheme.primary.copy(alpha = 1f),
            colorScheme.background,
            colorScheme.primary.copy(alpha = 1f)
        )

        val idUtente = "Danilo Scala" // Consider getting this dynamically

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 55.dp) // Increased top padding
                    .padding(horizontal = 24.dp), // Added horizontal padding
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween // Distribute space vertically
            ) {
                // App Icon within a subtly elevated rounded surface
                Surface(
                    modifier = Modifier
                        .size(90.dp), // Container size remains the same
                    shape = RoundedCornerShape(24.dp), // Rounded rectangular shape
                    color = colorScheme.surface, // Surface color as background
                    shadowElevation = 8.dp // Subtle shadow
                ) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp) // Padding minimo per icona grande
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.appicon1),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Fit // Adatta l'icona mantenendo l'aspect ratio
                        )
                    }
                }

                // Welcome Image
                Image(
                    painter = painterResource(id = R.drawable.welcome_image),
                    contentDescription = "Illustrazione casa",
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f / 0.8f, matchHeightConstraintsFirst = false)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                // Content Column (Text and Button)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Benvenuto",
                        color = colorScheme.onBackground,
                        style = typography.displaySmall, // Riproponiamo displaySmall per un titolo più incisivo
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Compra la tua casa dei sogni con facilità e sicurezza.",
                        color = colorScheme.onBackground.copy(alpha = 0.7f),
                        style = typography.bodyMedium, // Riproponiamo bodyMedium per il testo descrittivo
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = {
                            navController.navigate(Screen.HomeScreen.withArgs(idUtente))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Inizia ora",
                            style = typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun WelcomeScreenPreview() {
    val navController = rememberNavController()
    WelcomeScreen(navController = navController)
}