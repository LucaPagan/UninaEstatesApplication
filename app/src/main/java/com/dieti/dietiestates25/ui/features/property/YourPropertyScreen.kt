package com.dieti.dietiestates25.ui.features.property

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.ui.components.AppPropertyViewButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.data.remote.RetrofitClient
// Import dei DTO necessari
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.ImmagineDto
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

@Composable
fun YourPropertyScreen(
    navController: NavController,
    idUtente: String,
    viewModel: YourPropertyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val gradientColors = arrayOf(
        0.0f to colorScheme.primary, 0.20f to colorScheme.background,
        0.60f to colorScheme.background, 1.0f to colorScheme.primary
    )

    LaunchedEffect(Unit) {
        viewModel.loadMyProperties()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GeneralHeaderBar(
                title = "Le tue proprietà",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors))
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is YourPropertyState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorScheme.onPrimary)
                    }
                }
                is YourPropertyState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = colorScheme.error,
                            style = typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(dimensions.paddingMedium)
                        )
                    }
                }
                is YourPropertyState.Success -> {
                    if (state.immobili.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Non hai ancora inserito nessun immobile.",
                                style = typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = dimensions.paddingMedium,
                                end = dimensions.paddingMedium,
                                top = dimensions.paddingMedium,
                                bottom = dimensions.paddingLarge
                            ),
                            verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
                        ) {
                            items(items = state.immobili, key = { it.id }) { immobile ->
                                MyPropertyCard(
                                    immobile = immobile,
                                    onClick = {
                                        navController.navigate(Screen.PropertyScreen.withId(immobile.id))
                                    },
                                    onEditClick = {
                                        navController.navigate(Screen.EditPropertyScreen.withId(immobile.id))
                                    }
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

/**
 * Card personalizzata che accetta ImmobileDTO.
 */
@Composable
fun MyPropertyCard(
    immobile: ImmobileDTO,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val dimensions = Dimensions

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationSmall),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // SEZIONE IMMAGINE (Peso 0.6)
            Box(modifier = Modifier.weight(0.6f)) {

                // FIX CRASH: Cast sicuro (as?) per gestire il caso in cui Gson inserisce null
                // in un campo dichiarato Non-Null (immagini).
                // FIX: Uso dell'URL helper sicuro
                val imageUrl = immobile.immagini.firstOrNull()?.url?.let { RetrofitClient.getFullUrl(it) }

                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )

                // Etichetta Prezzo
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(topStart = dimensions.cornerRadiusMedium, bottomEnd = dimensions.cornerRadiusMedium),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    val prezzoFormat = "€ ${immobile.prezzo?.let { String.format("%,d", it) } ?: "N/D"}"
                    Text(
                        text = prezzoFormat,
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // SEZIONE DETTAGLI (Peso 0.4)
            Column(
                modifier = Modifier.weight(0.4f).padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        // MODIFICATO: ImmobileDTO non ha 'titolo', usiamo 'categoria' come fallback
                        text = immobile.categoria ?: "Immobile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = immobile.indirizzo ?: "Indirizzo non specificato",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val detailsList = mutableListOf<String>()
                    immobile.mq?.let { detailsList.add("${it}mq") }
                    val bagni = immobile.ambienti.filter { it.tipologia.contains("bagno", ignoreCase = true) }.sumOf { it.numero }
                    if (bagni > 0) detailsList.add("$bagni bagni")

                    Text(
                        text = detailsList.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )

                    // Tasto Modifica
                    AppPropertyViewButton(
                        text = "Modifica",
                        onClick = onEditClick
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun YourPropertyScreenPreview() {
    val navController = rememberNavController()
    DietiEstatesTheme {
        YourPropertyScreen(
            navController = navController,
            idUtente = "previewUser"
        )
    }
}