package com.dieti.dietiestates25.ui.features.property

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.components.*
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.getIconForRoomType
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyScreen(
    navController: NavController,
    idProperty: String?,
    viewModel: PropertyViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val dimensions = Dimensions
    val coroutineScope = rememberCoroutineScope()

    // Caricamento Dati
    LaunchedEffect(idProperty) {
        if (idProperty != null) {
            viewModel.loadProperty(idProperty)
        }
    }

    val property by viewModel.property.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Errore sconosciuto", color = colorScheme.error)
                }
            } else if (property != null) {
                val imm = property!!
                PropertyContent(
                    immobile = imm,
                    navController = navController,
                    coroutineScope = coroutineScope,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions,
                    context = context
                )
            }

            // Top Bar Overlay
            PropertyTopAppBar(
                colorScheme = colorScheme,
                navController = navController,
                dimensions = dimensions
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyContent(
    immobile: ImmobileDTO,
    navController: NavController,
    coroutineScope: CoroutineScope,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    context: Context
) {
    // Gestione Immagini Sicura
    val imageUrls = remember(immobile.immagini) {
        if (immobile.immagini.isNotEmpty()) {
            immobile.immagini.mapNotNull { RetrofitClient.getFullUrl(it.url) }
        } else {
            emptyList()
        }
    }
    val pagerState = rememberPagerState(initialPage = 0) {
        if (imageUrls.isNotEmpty()) imageUrls.size else 1
    }

    // Gestione Mappa
    val lat = immobile.lat ?: 40.8518 // Default Napoli
    val lon = immobile.long ?: 14.2681
    val propertyCoordinates = LatLng(lat, lon)
    val hasMapData = immobile.lat != null && immobile.long != null

    var isMapInteractive by remember { mutableStateOf(false) }
    val cameraPositionStateMiniMap = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(propertyCoordinates, 14f)
    }

    // Gestione Scroll vs Mappa
    var isPageScrollEnabled by remember { mutableStateOf(true) }
    val onMapInteractionStart = {
        if (!isMapInteractive) {
            isMapInteractive = true
            isPageScrollEnabled = false
        }
    }
    val onMapInteractionEnd = {
        if (isMapInteractive) {
            isMapInteractive = false
            isPageScrollEnabled = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isMapInteractive) {
                if (isMapInteractive) {
                    detectTapGestures(onTap = { onMapInteractionEnd() })
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = isPageScrollEnabled
        ) {
            // 1. Immagini
            item {
                PropertyImagePager(
                    pagerState = pagerState,
                    imageUrls = imageUrls,
                    coroutineScope = coroutineScope,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }

            // 2. Info Principali: Prezzo, Indirizzo, Tipo Vendita, Categoria
            item {
                PropertyHeaderInfo(
                    immobile = immobile,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }

            // 3. Caratteristiche Principali (Superficie + Lista Ambienti con Icone)
            item {
                PropertyMainFeaturesRow(
                    immobile = immobile,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }

            // 4. Servizi in Zona (Solo se presenti)
            item {
                if (immobile.parco || immobile.scuola || immobile.servizioPubblico) {
                    PropertyAmenitiesSection(
                        immobile = immobile,
                        colorScheme = colorScheme,
                        typography = typography,
                        dimensions = dimensions
                    )
                }
            }

            // 5. Descrizione
            item {
                PropertyDescriptionSection(
                    description = immobile.descrizione ?: "Nessuna descrizione fornita.",
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }

            // 6. Dettagli Estesi (Tutte le altre info scrollabili)
            item {
                PropertyExtendedDetailsSection(
                    immobile = immobile,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }

            // (Rimosso item PropertyAmbientiSection come richiesto)

            // 7. Mappa
            if (hasMapData) {
                item {
                    MiniMapSection(
                        propertyCoordinates = propertyCoordinates,
                        cameraPositionState = cameraPositionStateMiniMap,
                        isMapInteractive = isMapInteractive,
                        onMapInteractionStart = onMapInteractionStart,
                        onFullscreenClick = {
                            navController.navigate(
                                Screen.FullScreenMapScreen.withPosition(lat, lon, 15f)
                            )
                        },
                        colorScheme = colorScheme,
                        typography = typography,
                        dimensions = dimensions
                    )
                }
            }

            // 8. Bottoni Azione
            item {
                ActionButtonsSection(
                    navController = navController,
                    typography = typography,
                    dimensions = dimensions
                )
            }

            item {
                Spacer(Modifier.height(dimensions.paddingLarge))
            }
        }
    }
}

@Composable
fun PropertyTopAppBar(
    colorScheme: ColorScheme,
    navController: NavController,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall)
    ) {
        CircularIconActionButton(
            onClick = { navController.popBackStack() },
            iconVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            backgroundColor = colorScheme.surface.copy(alpha = 0.8f),
            iconTint = colorScheme.onSurface,
            buttonSize = dimensions.iconSizeExtraLarge,
            iconSize = dimensions.iconSizeMedium,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        val isFavorite = remember { mutableStateOf(false) }
        CircularIconActionButton(
            onClick = { isFavorite.value = !isFavorite.value },
            iconVector = if (isFavorite.value) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = "Favorite",
            backgroundColor = colorScheme.surface.copy(alpha = 0.8f),
            iconTint = if (isFavorite.value) colorScheme.tertiary else colorScheme.onSurface,
            buttonSize = dimensions.iconSizeExtraLarge,
            iconSize = dimensions.iconSizeMedium,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyImagePager(
    pagerState: PagerState,
    imageUrls: List<String>,
    coroutineScope: CoroutineScope,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.propertyCardHeight)
            .background(Color.LightGray)
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            if (imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrls[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto immobile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.ImageNotSupported,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                }
            }
        }

        if (imageUrls.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(dimensions.paddingMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularIconActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val targetPage = if (pagerState.currentPage > 0) pagerState.currentPage - 1 else imageUrls.size - 1
                            pagerState.animateScrollToPage(targetPage)
                        }
                    },
                    iconVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous",
                    backgroundColor = Color.Black.copy(alpha = 0.5f),
                    iconTint = Color.White
                )

                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1}/${imageUrls.size}",
                        color = Color.White,
                        style = typography.labelMedium
                    )
                }

                CircularIconActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val targetPage = if (pagerState.currentPage < imageUrls.size - 1) pagerState.currentPage + 1 else 0
                            pagerState.animateScrollToPage(targetPage)
                        }
                    },
                    iconVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next",
                    backgroundColor = Color.Black.copy(alpha = 0.5f),
                    iconTint = Color.White
                )
            }
        }
    }
}

// --- NUOVI COMPOSABLES PER TUTTI I CAMPI DTO ---

@Composable
fun PropertyHeaderInfo(
    immobile: ImmobileDTO,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium)
    ) {
        // Badge Tipo Vendita e Categoria
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tipoVenditaText = if (immobile.tipoVendita) "VENDITA" else "AFFITTO"
            val tipoVenditaColor = if (immobile.tipoVendita) colorScheme.primaryContainer else colorScheme.secondaryContainer
            val tipoVenditaContentColor = if (immobile.tipoVendita) colorScheme.onPrimaryContainer else colorScheme.onSecondaryContainer

            BadgeItem(text = tipoVenditaText, containerColor = tipoVenditaColor, contentColor = tipoVenditaContentColor)

            immobile.categoria?.let {
                BadgeItem(text = it.uppercase(), containerColor = colorScheme.surfaceVariant, contentColor = colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spacingMedium))

        // Prezzo
        val prezzoText = if (immobile.tipoVendita) {
            "€ ${immobile.prezzo?.let { String.format("%,d", it) } ?: "N/D"}"
        } else {
            "€ ${immobile.prezzo}/mese"
        }
        Text(text = prezzoText, style = typography.displayMedium, color = colorScheme.onBackground)

        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

        // Indirizzo e Località
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, "Location", tint = colorScheme.onSurfaceVariant, modifier = Modifier.size(dimensions.iconSizeSmall))
            Spacer(modifier = Modifier.width(dimensions.spacingExtraSmall))
            val indirizzoCompleto = listOfNotNull(immobile.indirizzo, immobile.localita).joinToString(", ")
            Text(
                text = if (indirizzoCompleto.isNotBlank()) indirizzoCompleto else "Indirizzo non disponibile",
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BadgeItem(text: String, containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = contentColor)
    }
}


@Composable
fun PropertyMainFeaturesRow(
    immobile: ImmobileDTO,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    val mq = immobile.mq ?: 0
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Caratteristiche Principali",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(
                start = dimensions.paddingMedium,
                end = dimensions.paddingMedium,
                bottom = dimensions.spacingMedium
            )
        )

        // Usiamo LazyRow per scorrere le icone dei locali se sono tante
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = dimensions.paddingMedium)
        ) {
            // Superficie sempre presente
            item {
                PropertyFeatureItem(Icons.Filled.SquareFoot, "$mq m²", "Superficie", colorScheme, typography, dimensions)
            }

            // Lista dinamica ambienti
            items(immobile.ambienti) { ambiente ->
                val icon = getIconForRoomType(ambiente.tipologia)
                // Mostra il numero e il nome (es. "2 Bagni") o solo "Bagno" se numero=1
                val label = ambiente.tipologia.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                PropertyFeatureItem(
                    icon = icon,
                    value = "${ambiente.numero}",
                    label = label,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }
        }

        // SLIDER / SCROLLBAR logic
        val layoutInfo = listState.layoutInfo
        val totalItemsCount = layoutInfo.totalItemsCount
        val visibleItemsInfo = layoutInfo.visibleItemsInfo

        if (visibleItemsInfo.isNotEmpty()) {
            val visibleItemsCount = visibleItemsInfo.size
            // Mostra scrollbar solo se c'è contenuto da scrollare
            if (totalItemsCount > visibleItemsCount) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .padding(horizontal = dimensions.paddingMedium)
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colorScheme.surfaceVariant)
                ) {
                    // Calcolo della larghezza del pollice (thumb)
                    // Se abbiamo 10 items e ne vediamo 3, il thumb dovrebbe essere circa il 30% della barra
                    val thumbFraction = (visibleItemsCount.toFloat() / totalItemsCount).coerceIn(0.15f, 0.5f)

                    val alignBias by remember {
                        derivedStateOf {
                            val firstIndex = listState.firstVisibleItemIndex
                            val firstOffset = listState.firstVisibleItemScrollOffset

                            // Dimensione stimata del primo elemento per calcolare offset frazionario
                            val itemSize = visibleItemsInfo.firstOrNull()?.size ?: 1

                            // Indice massimo teorico a cui può arrivare il firstVisibleItemIndex
                            // È approssimativamente totale - visibili.
                            // Aggiustiamo leggermente per essere sicuri di raggiungere il fondo (1.0)
                            val maxIndex = (totalItemsCount - visibleItemsCount).toFloat().coerceAtLeast(1f)

                            val currentScroll = firstIndex + (firstOffset.toFloat() / itemSize)

                            val progress = (currentScroll / maxIndex).coerceIn(0f, 1f)

                            // Map 0..1 to -1..1 for BiasAlignment
                            (progress * 2) - 1
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(thumbFraction)
                            .align(BiasAlignment(alignBias, 0f))
                            .background(colorScheme.primary, RoundedCornerShape(2.dp))
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, start = dimensions.paddingMedium, end = dimensions.paddingMedium), color = colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun PropertyFeatureItem(
    icon: ImageVector,
    value: String,
    label: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = colorScheme.primary, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, style = typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            text = label,
            style = typography.bodySmall,
            color = colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PropertyAmenitiesSection(
    immobile: ImmobileDTO,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    // Se nessun servizio è presente, non mostriamo nulla
    if (!immobile.parco && !immobile.scuola && !immobile.servizioPubblico) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingMedium)
    ) {
        Text(
            text = "Servizi e Trasporti in Zona",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.spacingMedium)
        )

        // FlowRow sarebbe ideale, ma qui usiamo una Row scrollabile o semplice Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (immobile.parco) {
                AmenityChip(Icons.Default.Park, "Parco", colorScheme)
            }
            if (immobile.scuola) {
                AmenityChip(Icons.Default.School, "Scuola", colorScheme)
            }
            if (immobile.servizioPubblico) {
                AmenityChip(Icons.Default.DirectionsBus, "Trasporti", colorScheme)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp), color = colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun AmenityChip(icon: ImageVector, label: String, colorScheme: ColorScheme) {
    Surface(
        color = colorScheme.primaryContainer,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PropertyExtendedDetailsSection(
    immobile: ImmobileDTO,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium)
    ) {
        Text(
            text = "Dettagli Immobile",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.spacingMedium)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
            border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(dimensions.paddingMedium)) {
                // Info Strutturali
                DetailRow("Categoria", immobile.categoria ?: "N/D", Icons.Default.Home, colorScheme, typography)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant.copy(alpha = 0.3f))

                DetailRow("Stato Proprietà", immobile.statoProprieta ?: "N/D", Icons.Default.Build, colorScheme, typography)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant.copy(alpha = 0.3f))

                DetailRow("Anno Costruzione", immobile.annoCostruzione?.take(4) ?: "N/D", Icons.Default.CalendarToday, colorScheme, typography)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Piano e Accessibilità
                val pianoInfo = if (immobile.piano != null) "${immobile.piano}° Piano" else "Piano N/D"
                val ascensoreInfo = if (immobile.ascensore == true) "(con Ascensore)" else "(no Ascensore)"
                DetailRow("Piano", "$pianoInfo $ascensoreInfo", Icons.Default.Layers, colorScheme, typography)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant.copy(alpha = 0.3f))

                // Comfort e Spese
                val climaText = if (immobile.climatizzazione == true) "Presente" else "Assente"
                DetailRow("Climatizzazione", climaText, Icons.Default.AcUnit, colorScheme, typography)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant.copy(alpha = 0.3f))

                DetailRow("Esposizione", immobile.esposizione ?: "N/D", Icons.Default.WbSunny, colorScheme, typography)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant.copy(alpha = 0.3f))

                DetailRow("Arredamento", immobile.arredamento ?: "N/D", Icons.Default.Weekend, colorScheme, typography)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = colorScheme.outlineVariant.copy(alpha = 0.3f))

                val speseText = immobile.speseCondominiali?.let { "€ $it / mese" } ?: "N/D"
                DetailRow("Spese Condominiali", speseText, Icons.Default.AttachMoney, colorScheme, typography)
            }
        }

        Spacer(modifier = Modifier.height(dimensions.spacingLarge))
    }
}

@Composable
fun DetailRow(label: String, value: String, icon: ImageVector, colorScheme: ColorScheme, typography: Typography) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
        }
        Text(
            text = value,
            style = typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun PropertyDescriptionSection(
    description: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(modifier = Modifier.fillMaxWidth().padding(dimensions.paddingMedium)) {
        Text("Descrizione", style = typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Text(
            text = description,
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun MiniMapSection(
    propertyCoordinates: LatLng,
    cameraPositionState: CameraPositionState,
    isMapInteractive: Boolean,
    onMapInteractionStart: () -> Unit,
    onFullscreenClick: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingLarge)
    ) {
        Text(
            text = "Posizione",
            style = typography.titleMedium,
            modifier = Modifier.padding(bottom = dimensions.spacingMedium)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                    minZoomPreference = 10f,
                    maxZoomPreference = 18f
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    scrollGesturesEnabled = isMapInteractive,
                    zoomGesturesEnabled = isMapInteractive
                ),
                onMapClick = { if (!isMapInteractive) onMapInteractionStart() }
            ) {
                Marker(
                    state = MarkerState(position = propertyCoordinates),
                    title = "Immobile"
                )
            }

            if (!isMapInteractive) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onMapInteractionStart
                        )
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tocca per esplorare",
                        color = Color.White,
                        style = typography.labelMedium,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    )
                }
            }

            IconButton(
                onClick = onFullscreenClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(colorScheme.surface, androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(Icons.Filled.Fullscreen, "Fullscreen", tint = colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    navController: NavController,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        AppSecondaryButton(
            onClick = { navController.navigate(Screen.PriceProposalScreen.route) },
            text = "Fai un'Offerta",
            icon = Icons.Default.Euro,
            iconContentDescription = "Offer",
            modifier = Modifier.fillMaxWidth()
        )
    }
}