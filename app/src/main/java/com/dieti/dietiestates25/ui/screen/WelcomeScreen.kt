package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

private const val WELCOME_IMAGE_WIDTH_FRACTION = 0.9f
private const val WelcomeImageAspectRatio = 1f / 0.8f


@Composable
fun WelcomeScreen(navController: NavController) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val dimensions = Dimensions

        val gradientColors = listOf(
            colorScheme.primary,
            colorScheme.background,
            colorScheme.primary
        )

        // TODO: Retrieve 'idUtente' dynamically (e.g., from ViewModel, authentication state, or pass as argument).
        val idUtente = "Danilo Scala" // Placeholder

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors)),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = dimensions.buttonHeight)
                    .padding(horizontal = dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = RoundedCornerShape(dimensions.cornerRadiusLarge),
                    color = colorScheme.surface,
                    shadowElevation = dimensions.elevationLarge
                ) {
                    Box(
                        modifier = Modifier
                            .padding(dimensions.paddingExtraSmall)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.appicon1),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(dimensions.spacingMedium + dimensions.spacingExtraSmall)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Welcome Image
                Image(
                    painter = painterResource(id = R.drawable.welcome_image),
                    contentDescription = "House illustration",
                    modifier = Modifier
                        .fillMaxWidth(WELCOME_IMAGE_WIDTH_FRACTION)
                        .aspectRatio(WelcomeImageAspectRatio, matchHeightConstraintsFirst = false)
                        .padding(bottom = dimensions.paddingMedium),
                    contentScale = ContentScale.Fit
                )

                // Content Column (Text and Button)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensions.paddingMedium)
                        .padding(bottom = dimensions.iconSizeExtraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Benvenuto",
                        color = colorScheme.onBackground,
                        style = typography.displaySmall,
                        textAlign = TextAlign.Center
                    )

                    // SpacerHeightSmall era 8.dp
                    Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                    Text(
                        text = "Compra la tua casa dei sogni con facilità e sicurezza.",
                        color = colorScheme.onBackground.copy(alpha = 0.7f),
                        style = typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimensions.iconSizeExtraLarge))

                    AppPrimaryButton(
                        text = "Inizia ora",
                        onClick = {
                            navController.navigate(Screen.HomeScreen.withArgs(idUtente))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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