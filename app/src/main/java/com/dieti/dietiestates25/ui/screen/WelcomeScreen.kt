@file:Suppress("DEPRECATION")

package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

private const val WELCOME_IMAGE_WIDTH_FRACTION = 0.9f
private const val WELCOME_IMAGE_ASPECT_RATIO = 1f / 0.8f

@Composable
fun WelcomeScreen(navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    val idUtente = "Danilo Scala"

        Scaffold(topBar = {}) { paddingValuesScaffold ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = gradientColors))
                    .padding(paddingValuesScaffold),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensions.paddingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge)) // Es. 32dp o più, regola a piacere

                    AppIconDisplay(
                        size = 100.dp, // Dimensione come l'avevi prima
                        shapeRadius = dimensions.cornerRadiusLarge,
                        internalPadding = dimensions.paddingExtraSmall,
                        imageClipRadius = dimensions.cornerRadiusMedium
                    )

                    Spacer(modifier = Modifier.weight(1f)) // Peso minore per spingere l'immagine un po' più in alto

                    Image(
                        painter = painterResource(id = R.drawable.welcome_image),
                        contentDescription = "House illustration",
                        modifier = Modifier
                            .fillMaxWidth(WELCOME_IMAGE_WIDTH_FRACTION)
                            .aspectRatio(WELCOME_IMAGE_ASPECT_RATIO)
                            .weight(1f, fill = false), // L'immagine prende spazio proporzionale
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.weight(0.7f))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Benvenuto",
                            color = colorScheme.onBackground,
                            style = typography.displaySmall,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                        Text(
                            text = "Compra la tua casa dei sogni con facilità e sicurezza.",
                            color = colorScheme.onBackground.copy(alpha = 0.8f),
                            style = typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = dimensions.paddingSmall)
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.3f))

                    AppPrimaryButton(
                        text = "Inizia ora",
                        onClick = {
                            navController.navigate(Screen.HomeScreen.withIdUtente(idUtente))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
                }
            }
        }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun WelcomeScreenPreview() {
    DietiEstatesTheme {
        val navController = rememberNavController()
        WelcomeScreen(navController = navController)
    }
}