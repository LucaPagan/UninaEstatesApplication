package com.dieti.dietiestates25.ui.features.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel // Import necessario
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppIconDisplay
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.register.AuthViewModel
import com.dieti.dietiestates25.ui.theme.AppGradients
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.theme.TealDeep

// Assicurati di importare il ViewModel corretto
// import com.dieti.dietiestates25.ui.viewmodel.AuthViewModel

private const val WELCOME_IMAGE_WIDTH_FRACTION = 0.9f
private const val WELCOME_IMAGE_ASPECT_RATIO = 1f / 0.8f

@SuppressLint("SuspiciousIndentation")
@Composable
fun WelcomeScreen(
    navController: NavController,
    idUtente: String? = null,
    // Iniettiamo il ViewModel per salvare lo stato "Prima Apertura"
    viewModel: AuthViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Status Bar con colore TealDeep fisso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(TealDeep)
        )

        // Contenuto principale con gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppGradients.primaryToBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))

                AppIconDisplay(
                    size = dimensions.logoLarge,
                    shapeRadius = dimensions.cornerRadiusLarge,
                    internalPadding = dimensions.paddingExtraSmall,
                    imageClipRadius = dimensions.cornerRadiusMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.welcome_image),
                    contentDescription = "House illustration",
                    modifier = Modifier
                        .fillMaxWidth(WELCOME_IMAGE_WIDTH_FRACTION)
                        .aspectRatio(WELCOME_IMAGE_ASPECT_RATIO)
                        .weight(1f, fill = false),
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

                // --- MODIFICA FONDAMENTALE QUI ---
                AppPrimaryButton(
                    text = "Inizia ora",
                    onClick = {
                        // 1. Diciamo al sistema che la Welcome Screen è stata vista
                        viewModel.completaWelcomeScreen()

                        // 2. Navighiamo al Login rimuovendo la Welcome dal backstack
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo("welcome_intro_screen") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(dimensions.spacingExtraLarge))
            }
        }
    }
}