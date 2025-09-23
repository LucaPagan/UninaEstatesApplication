package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun CustomPriceMarker(
    price: String,
    isSelected: Boolean = false,
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    typography: Typography = MaterialTheme.typography,
    scale: Float = 1f,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        colorScheme.primaryContainer
    } else {
        colorScheme.primary
    }

    val textColor = if (isSelected) {
        colorScheme.onPrimaryContainer
    } else {
        colorScheme.onPrimary
    }

    val borderColor = if (isSelected) {
        colorScheme.primary
    } else {
        colorScheme.surfaceDim
    }

    val dimensions = Dimensions

    Box(
        modifier = modifier
            .then(
                if (scale != 1f) {
                    Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                } else Modifier
            )
            .shadow(
                elevation = if (isSelected) dimensions.elevationLarge else dimensions.elevationMedium,
                shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
                ambientColor = Color.Black.copy(alpha = 0.2f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
            )
            .border(
                width = if (isSelected) dimensions.elevationSmall else dimensions.elevationNone,
                color = borderColor,
                shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
            )
            .padding(
                horizontal = dimensions.paddingMedium,
                vertical = dimensions.paddingSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = price,
            style = typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Un Composable che definisce l'aspetto visivo di un'icona personalizzata per la mappa.
 * Questo Composable verrà convertito in una Bitmap per essere usato come marker.
 *
 * @param modifier Modificatori standard di Compose.
 * @param tint Il colore con cui tingere l'icona.
 * @param iconSize La dimensione dell'icona (e del Box che la contiene).
 */
@Composable
fun AppCustomMapMarker(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    iconSize: Dp = Dimensions.customMarker, // Dimensione base, puoi renderla "molto grande"
    scale: Float = 1f,
    dimensions: Dimensions
) {
    Box(
        modifier = modifier
            .size(iconSize) // La dimensione base è definita qui
            .then(
                if (scale != 1f) { // Applica la scala se diversa da 1
                    Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        AppIconDisplay(
            size = dimensions.logoLarge,
            shapeRadius = dimensions.cornerRadiusLarge,
            internalPadding = dimensions.paddingExtraSmall,
            imageClipRadius = dimensions.cornerRadiusMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomPriceMarkerPreview() {
    DietiEstatesTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Marker normale:")
            CustomPriceMarker(price = "€850/mese")

            Text("Marker selezionato:")
            CustomPriceMarker(price = "€850/mese", isSelected = true)

            Text("Marker con scala 0.8:")
            CustomPriceMarker(price = "€850/mese", scale = 0.8f)

            Text("Marker con scala 0.6:")
            CustomPriceMarker(price = "€850/mese", scale = 0.6f)

            Text("Icona casa:")
            AppCustomMapMarker(dimensions = Dimensions)
        }
    }
}