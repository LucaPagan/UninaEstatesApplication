// In un nuovo file ui/theme/Gradient.kt o dentro Theme.kt

package com.dieti.dietiestates25.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

object AppGradients {
    val primaryToBackground: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        )


    val primaryToSecondary: Brush
        @Composable
        get() = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.background,
            )
        )
}