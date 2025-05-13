package com.dieti.dietiestates25.ui.screen

import android.app.Activity
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
// import androidx.compose.ui.res.stringResource // Import if you use stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

// --- Dimensions ---
// Private constants for dimensions, promoting reusability and maintainability within this screen.
private val WelcomeScreenPaddingTop = 55.dp
private val WelcomeScreenPaddingHorizontal = 24.dp
private val AppIconContainerSize = 90.dp
private val AppIconShapeRadius = 24.dp
private val AppIconInternalPadding = 4.dp
private val AppIconImageClipRadius = 20.dp // Adjusted for a visible clip inside the padded surface
private val WelcomeImageBottomPadding = 16.dp
private val WelcomeImageAspectRatio = 1f / 0.8f // Equivalent to 1.25f
private val ContentColumnHorizontalPadding = 16.dp
private val ContentColumnBottomPadding = 48.dp
private val SpacerHeightSmall = 8.dp
private val SpacerHeightLarge = 48.dp
private val ActionButtonHeight = 56.dp
private val ActionButtonShapeRadius = 12.dp
private val ActionButtonElevationDefault = 8.dp
private val ActionButtonElevationPressed = 4.dp
private const val WELCOME_IMAGE_WIDTH_FRACTION = 0.9f

@Composable
fun WelcomeScreen(navController: NavController) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        // System UI configuration
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                val primaryColorArgb = colorScheme.primary.toArgb()
                window.statusBarColor = primaryColorArgb
                window.navigationBarColor = primaryColorArgb
                WindowCompat.getInsetsController(window, view).let { controller ->
                    controller.isAppearanceLightStatusBars = false // Assuming dark primary color
                    controller.isAppearanceLightNavigationBars = false // Assuming dark primary color
                }
            }
        }

        // Gradient: smooth transition between primary and background colors.
        val gradientColors = listOf(
            colorScheme.primary,
            colorScheme.background,
            colorScheme.primary
        )

        // TODO: Retrieve 'idUtente' dynamically (e.g., from ViewModel, authentication state, or pass as argument).
        val idUtente = "Danilo Scala"

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .statusBarsPadding(), // Apply padding for the status bar
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = WelcomeScreenPaddingTop)
                    .padding(horizontal = WelcomeScreenPaddingHorizontal),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween // Distributes space between elements
            ) {
                // App Icon displayed within a slightly elevated, rounded Surface.
                Surface(
                    modifier = Modifier.size(AppIconContainerSize),
                    shape = RoundedCornerShape(AppIconShapeRadius),
                    color = colorScheme.surface,
                    shadowElevation = ActionButtonElevationDefault // Reusing for consistency or define new
                ) {
                    Box(
                        modifier = Modifier
                            .padding(AppIconInternalPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.appicon1),
                            // Consider using stringResource(R.string.app_icon_content_description)
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(AppIconImageClipRadius)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Welcome Image
                Image(
                    painter = painterResource(id = R.drawable.welcome_image),
                    // Consider using stringResource(R.string.welcome_image_content_description)
                    contentDescription = "House illustration",
                    modifier = Modifier
                        .fillMaxWidth(WELCOME_IMAGE_WIDTH_FRACTION)
                        .aspectRatio(WelcomeImageAspectRatio, matchHeightConstraintsFirst = false)
                        .padding(bottom = WelcomeImageBottomPadding), // Specific padding if needed before the next block
                    contentScale = ContentScale.Fit
                )

                // Content Column (Text and Button)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ContentColumnHorizontalPadding)
                        .padding(bottom = ContentColumnBottomPadding), // Padding at the very bottom of the screen content
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        // Consider using stringResource(R.string.welcome_title_text)
                        text = "Benvenuto",
                        color = colorScheme.onBackground,
                        style = typography.displaySmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(SpacerHeightSmall))

                    Text(
                        // Consider using stringResource(R.string.welcome_subtitle_text)
                        text = "Compra la tua casa dei sogni con facilità e sicurezza.",
                        color = colorScheme.onBackground.copy(alpha = 0.7f), // Subdued text color
                        style = typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(SpacerHeightLarge))

                    Button(
                        onClick = {
                            navController.navigate(Screen.HomeScreen.withArgs(idUtente))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ActionButtonHeight),
                        shape = RoundedCornerShape(ActionButtonShapeRadius),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = ActionButtonElevationDefault,
                            pressedElevation = ActionButtonElevationPressed
                        )
                    ) {
                        Text(
                            // Consider using stringResource(R.string.welcome_button_start)
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
    // It's good practice that previews can also wrap in the theme if not done internally by the Composable
    DietiEstatesTheme {
        val navController = rememberNavController()
        WelcomeScreen(navController = navController)
    }
}