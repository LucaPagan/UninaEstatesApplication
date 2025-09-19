package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dieti.dietiestates25.ui.model.Richiesta
import com.dieti.dietiestates25.ui.model.StatoRichiesta
import com.dieti.dietiestates25.ui.theme.Dimensions
import java.time.format.DateTimeFormatter
import java.util.Locale

// Componente specializzato per visualizzare una Richiesta
@Composable
fun AppRequestDisplay(
    richiesta: Richiesta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ITALIAN) }

    AppInfoItemCard(
        onClick = onClick,
        icon = Icons.Default.Description, // Icona generica per una richiesta
        iconBackgroundColor = colorScheme.primaryContainer,
        iconTint = colorScheme.onPrimaryContainer,
        title = richiesta.titolo,
        subtitle = richiesta.descrizione,
        trailingTopText = richiesta.data.format(dateFormatter),
        trailingBottomContent = {
            StatusBadge(state = richiesta.stato, dimensions = dimensions)
        },
        modifier = modifier
    )
}

// Badge per visualizzare lo stato della richiesta con il colore appropriato
@Composable
private fun StatusBadge(
    state: StatoRichiesta,
    dimensions: Dimensions,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val (backgroundColor, text, textColor) = when (state) {
        StatoRichiesta.CONFERMATA -> Triple(colorScheme.primary, "Confermata", colorScheme.onPrimary)
        StatoRichiesta.IN_ACCETTAZIONE -> Triple(colorScheme.scrim, "In Attesa", colorScheme.onPrimary)
        StatoRichiesta.RIFIUTATA -> Triple(colorScheme.error, "Rifiutata", colorScheme.onError)
    }
    Box(
        modifier = modifier
            .padding(vertical = dimensions.paddingMedium)
    ){
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(50)) // Forma a pillola
                .background(backgroundColor)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        }
    }
}
