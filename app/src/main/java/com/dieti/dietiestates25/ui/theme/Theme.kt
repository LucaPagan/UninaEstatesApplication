@file:Suppress("DEPRECATION")

package com.dieti.dietiestates25.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

// Light theme colors
private val LightColors = lightColorScheme(
    primary = TealVibrant,
    onPrimary = Color.White,
    primaryContainer = TealDeep,
    onPrimaryContainer = Color.White,
    secondary = TealLight,
    onSecondary = GrayBlue,
    background = NeutralLight,
    onBackground = GrayBlue,
    surface = Color.White,
    onSurface = GrayBlue,
    scrim = Orange,
    error = Color.Red,
    onError = Color.White,
    surfaceDim = Color.Transparent,
    surfaceTint = Color(0xFFFFD700)
)

// Dark theme colors
private val DarkColors = darkColorScheme(
    primary = TealVibrant,
    onPrimary = Color.White,
    primaryContainer = TealDeep,
    onPrimaryContainer = Color.White,
    secondary = TealLight,
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    scrim = Orange,
    error = Color.Red,
    onError = Color.White,
    surfaceDim = Color.Transparent,
    surfaceTint = Color(0xFFFFD700)
)

// Typography
val typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 16.sp
    )
)

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun DietiEstatesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val selectedColorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            activity?.window?.let { window ->
                // 1. Permetti al contenuto di disegnarsi dietro le system bars
                WindowCompat.setDecorFitsSystemWindows(window, false)

                // 2. FORZA il colore TealDeep per la Status Bar (NON trasparente)
                val statusBarColor = TealDeep.toArgb()
                window.statusBarColor = statusBarColor

                // 3. Navigation Bar trasparente
                window.navigationBarColor = Color.Transparent.toArgb()

                // 4. Configura le icone della status bar
                val insetsController = WindowCompat.getInsetsController(window, view)

                // Su TealDeep (scuro) -> icone chiare
                insetsController.isAppearanceLightStatusBars = false

                // Navigation bar trasparente -> icone dipendono dal tema
                insetsController.isAppearanceLightNavigationBars = !darkTheme

                // 5. IMPORTANTE: Su alcune versioni Android, forza la modalità
                // Per Android 11+ assicurati che la status bar non sia trasparente
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    MaterialTheme(
        colorScheme = selectedColorScheme,
        typography = typography,
        content = content
    )
}