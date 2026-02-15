package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.data.model.PropertyMarker
import com.dieti.dietiestates25.ui.theme.Dimensions

// --- MAP INFO WINDOW (CARD ANTEPRIMA) ---
@Composable
fun PropertyPreviewInfoWindow(
    property: PropertyMarker,
    onClick: () -> Unit,
    onClose: () -> Unit,
    dimensions: Dimensions,
    typography: Typography,
    colorScheme: ColorScheme,
    modifier: Modifier = Modifier
) {
    // FIX STILE: Riusiamo AppPropertyCard per garantire coerenza grafica con il resto dell'app.
    // La card gestisce già il caricamento immagine (URL o Resource) e il layout orizzontale.
    AppPropertyCard(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp), // Altezza fissa ottimale per la preview su mappa
        price = property.price,
        imageUrl = property.imageUrl,
        imageResId = property.imageRes,
        address = property.title, // Usiamo il titolo (Categoria) come intestazione principale
        // Combiniamo superficie e tipo contratto nei dettagli standard
        details = listOfNotNull(
            property.surface,
            property.purchaseType.replaceFirstChar { it.uppercase() }
        ),
        onClick = onClick,
        horizontalMode = true,
        elevationDp = dimensions.elevationLarge
    )
}

@Composable
fun AppPropertyCard(
    modifier: Modifier = Modifier,
    price: String,
    imageResId: Int? = null,
    imageUrl: String? = null,
    address: String,
    details: List<String>,
    onClick: () -> Unit,
    actionButton: @Composable (() -> Unit)? = null,
    horizontalMode: Boolean = false,
    imageHeightVerticalRatio: Float = 0.6f,
    elevationDp: Dp = 4.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevationDp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        if (horizontalMode) {
            Row(modifier = Modifier.fillMaxSize()) {
                PropertyImageSection(
                    imageResId = imageResId,
                    imageUrl = imageUrl,
                    modifier = Modifier.weight(0.4f).fillMaxHeight()
                )
                Column(
                    modifier = Modifier
                        .weight(1f - imageHeightVerticalRatio)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center // Centra verticalmente il contenuto
                ) {
                    PropertyInfoContent(price, address, details)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                PropertyImageSection(
                    imageResId = imageResId,
                    imageUrl = imageUrl,
                    modifier = Modifier.weight(imageHeightVerticalRatio).fillMaxWidth()
                )
                Column(modifier = Modifier.weight(1f - imageHeightVerticalRatio).padding(12.dp)) {
                    PropertyInfoContent(price, address, details)
                    actionButton?.invoke()
                }
            }
        }
    }
}

@Composable
fun PropertyImageSection(
    imageResId: Int?,
    imageUrl: String?,
    modifier: Modifier
) {
    Box(modifier = modifier.background(Color.LightGray)) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                // Placeholder per evitare sfarfallii o spazi vuoti
                error = painterResource(R.drawable.ic_launcher_foreground),
                placeholder = painterResource(R.drawable.ic_launcher_foreground)
            )
        } else if (imageResId != null && imageResId != 0) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Placeholder vuoto o icona di default
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    alpha = 0.3f
                )
            }
        }
    }
}

@Composable
fun PropertyInfoContent(
    price: String,
    address: String,
    details: List<String>
) {
    Column (
        modifier = Modifier.fillMaxSize(),
    ){
        Text(
            text = price,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = details.joinToString(" • "),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// --- UTILS ---
@Composable
fun AppPropertyViewButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text("Vedi", fontSize = 12.sp)
    }
}

@Composable
fun <T> PropertyShowcaseSection(
    title: String,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    onSeeAllClick: () -> Unit,
    listContentPadding: PaddingValues
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            TextButton(onClick = onSeeAllClick) { Text("Vedi tutti") }
        }
        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = listContentPadding,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items.size) { index -> itemContent(items[index]) }
        }
    }
}