package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dieti.dietiestates25.ui.theme.Dimensions

/**
 * Un componente generico per mostrare un messaggio quando una lista è vuota.
 * Occupa tutto lo spazio disponibile e centra il messaggio.
 *
 * @param modifier Il modificatore da applicare al contenitore Box.
 * @param message Il testo del messaggio da visualizzare.
 * @param dimensions L'oggetto Dimensions per accedere a padding e spacing consistenti.
 * @param colorScheme Lo schema di colori del tema corrente.
 * @param typography La tipografia del tema corrente.
 */
@Composable
fun AppEmptyDisplayView(
    modifier: Modifier = Modifier,
    message: String,
    dimensions: Dimensions = Dimensions,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}