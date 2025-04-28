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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

        val gradientColors = arrayOf(
            0.0f to colorScheme.primary,
            0.20f to colorScheme.background,
            0.60f to colorScheme.background,
            1.0f to colorScheme.primary
        )

        val idUtente = "fwfwefwefwefwefwef"

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors)),
        ) {
            // Welcome Image
            Image(
                painter = painterResource(id = R.drawable.welcome_image),
                contentDescription = "Illustrazione casa",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f / 0.8f)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            // Content Column (Text and Button)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Benvenuto",
                    color = colorScheme.onPrimary,
                    style = typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Compra la tua casa dei sogni",
                    color = colorScheme.onPrimary.copy(alpha = 0.9f),
                    style = typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.HomeScreen.withArgs(idUtente))
                    },
                    modifier = Modifier
                        .widthIn(max = 300.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(25),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.secondary,
                        contentColor = colorScheme.onSecondary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "Continua",
                        style = typography.labelLarge
                    )
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