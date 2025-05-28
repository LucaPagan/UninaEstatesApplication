package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.theme.Dimensions

import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

import android.content.Intent
import android.content.res.Configuration
import android.util.Log // Utile per il debug

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapType
import com.google.android.gms.maps.CameraUpdateFactory
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.foundation.BorderStroke

// ... (tutti gli import rimangono gli stessi, assicurati che Dialog e DialogProperties siano importati)
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
// ... (altri import)
import androidx.compose.material3.Surface // Aggiunto per il contenuto del Dialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PropertyScreen(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val context = LocalContext.current
    val dimensions = Dimensions

    val coroutineScope = rememberCoroutineScope()

    val propertyImages = listOf(
        R.drawable.property1,
        R.drawable.property2,
    )

    val totalImages = propertyImages.size
    val pagerState = rememberPagerState(initialPage = 0) { totalImages }

    val propertyCoordinates = LatLng(40.8518, 14.2681) // Esempio Napoli
    var isMapInteractive by remember { mutableStateOf(false) }
    var showFullscreenMap by remember { mutableStateOf(false) }
    var isPageScrollEnabled by remember { mutableStateOf(true) }

    val initialZoomMiniMap = 12f
    val zoomForFullscreenMap = 15f

    val cameraPositionStateMiniMap = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(propertyCoordinates, initialZoomMiniMap)
    }
    // NON creiamo più cameraPositionStateFullscreen qui se FullscreenMapDialog lo gestisce internamente.
    // Se invece vuoi tenerlo per altri usi, va bene, ma non lo passeremo al Dialog.

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

    Scaffold(
        topBar = { Box {} }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isMapInteractive) {
                    if (isMapInteractive) {
                        detectTapGestures(
                            onTap = { onMapInteractionEnd() }
                        )
                    }
                }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                userScrollEnabled = isPageScrollEnabled
            ) {
                item {
                    PropertyImagePager(pagerState, propertyImages, coroutineScope, colorScheme, typography, dimensions)
                }
                item {
                    PropertyPriceAndLocation("€129.500", "Appartamento Napoli, Via Francesco Girardi 90", colorScheme, typography, dimensions)
                }
                item {
                    PropertyFeaturesRow(colorScheme, typography, dimensions)
                }
                item {
                    PropertyCharacteristicsSection(listOf("115 m²", "2 Camere da letto", "1 Bagno", "3 Locali"), colorScheme, typography, dimensions)
                }
                item {
                    PropertyDescriptionSection("Scopri questo accogliente appartamento situato nel cuore di Napoli...", colorScheme, typography, dimensions)
                }
                item {
                    MiniMapSection(
                        propertyCoordinates = propertyCoordinates,
                        cameraPositionState = cameraPositionStateMiniMap,
                        isMapInteractive = isMapInteractive,
                        onMapInteractionStart = onMapInteractionStart,
                        onFullscreenClick = {
                            Log.d("PropertyScreen", "Fullscreen button clicked. Setting showFullscreenMap = true")
                            showFullscreenMap = true
                        },
                        colorScheme = colorScheme,
                        typography = typography,
                        dimensions = dimensions
                    )
                }
                item {
                    AgentInfoSection("Agenzia Gianfranco Lombardi", "081 192 6079", "VIA G. PORZIO ISOLA Es 3, Napoli (NA), Campania, 80143", colorScheme, typography, dimensions)
                }
                item {
                    ActionButtonsSection(navController, "081 192 6079", typography, context, dimensions)
                }
                item {
                    ReportAdSection(colorScheme, typography, dimensions)
                }
                item {
                    SimilarListingsSection(navController, colorScheme, typography, dimensions)
                }
                item {
                    Spacer(modifier = Modifier.height(dimensions.paddingExtraLarge * 2 + dimensions.paddingMedium))
                }
            }

            PropertyTopAppBar(colorScheme, navController, dimensions)

            if (showFullscreenMap) {
                FullscreenMapDialog(
                    initialPropertyCoordinates = propertyCoordinates,
                    initialZoom = zoomForFullscreenMap,
                    // NESSUN cameraPositionState esterno passato qui
                    onDismiss = {
                        showFullscreenMap = false
                        if (isMapInteractive) {
                            onMapInteractionEnd()
                        } else {
                            isPageScrollEnabled = true
                        }
                        Log.d("PropertyScreen", "FullscreenMapDialog dismissed.")
                    },
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }
        }
    }
}

@Composable
private fun MiniMapSection(
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
            text = "Mappa e dintorni",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.spacingMedium)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
        ) {
            GoogleMap(
                modifier = Modifier.matchParentSize().background(colorScheme.surfaceVariant),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                    isBuildingEnabled = true,
                    isTrafficEnabled = false,
                    minZoomPreference = 10f,
                    maxZoomPreference = 18f
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = false,
                    scrollGesturesEnabled = isMapInteractive,
                    zoomGesturesEnabled = isMapInteractive,
                    tiltGesturesEnabled = isMapInteractive,
                    rotationGesturesEnabled = isMapInteractive
                ),
                onMapClick = {
                    if (!isMapInteractive) {
                        onMapInteractionStart()
                    }
                },
            ) {
                Marker(
                    state = MarkerState(position = propertyCoordinates),
                    title = "Posizione Proprietà"
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
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Clicca per interagire con la mappa",
                        color = Color.White,
                        style = typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(dimensions.paddingMedium)
                    )
                }
            }
            CircularIconActionButton(
                onClick = onFullscreenClick,
                iconVector = Icons.Filled.Fullscreen,
                contentDescription = "Mappa a schermo intero",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(dimensions.spacingSmall),
                backgroundColor = colorScheme.surface.copy(alpha = 0.8f),
                iconTint = colorScheme.onSurface,
                buttonSize = 36.dp,
                iconSize = dimensions.iconSizeMedium
            )
        }
        Text(
            text = "La mappa mostra un'area di circa 5-10km. I P.O.I. sono indicativi.",
            style = typography.labelSmall,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = dimensions.spacingSmall)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullscreenMapDialog(
    initialPropertyCoordinates: LatLng,
    initialZoom: Float,
    // cameraPositionState: CameraPositionState, // RIMOSSO DAI PARAMETRI
    onDismiss: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        // Crea e ricorda lo stato della camera INTERNAMENTE
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(initialPropertyCoordinates, initialZoom)
        }
        var isMapLoaded by remember { mutableStateOf(false) }

        LaunchedEffect(isMapLoaded, initialPropertyCoordinates, initialZoom, cameraPositionState) {
            // Questo `LaunchedEffect` ora si riferisce al `cameraPositionState` interno.
            // Le chiavi initialPropertyCoordinates e initialZoom sono importanti se vuoi
            // che la mappa si ri-centri se questi parametri dovessero cambiare mentre
            // il dialogo è aperto (improbabile nel tuo scenario attuale, ma buona pratica).
            if (isMapLoaded) {
                // Se la posizione iniziale è già stata impostata da rememberCameraPositionState,
                // questo LaunchedEffect potrebbe non aver bisogno di muovere la camera di nuovo,
                // a meno che tu non voglia assicurarti che si resetti ogni volta o applicare un'animazione.
                // Per sicurezza, o per un'animazione all'apertura:
                val targetCameraPosition = CameraPosition.fromLatLngZoom(initialPropertyCoordinates, initialZoom)
                Log.d("FullscreenMapDialog", "LaunchedEffect (map is loaded, internal state): Attempting to animate camera to $targetCameraPosition")
                try {
                    cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(targetCameraPosition), 700)
                    Log.d("FullscreenMapDialog", "LaunchedEffect (map is loaded, internal state): Camera animation initiated.")
                } catch (e: Exception) {
                    Log.e("FullscreenMapDialog", "LaunchedEffect (map is loaded, internal state): Error animating camera", e)
                }
            } else {
                Log.d("FullscreenMapDialog", "LaunchedEffect (internal state): Waiting for onMapLoaded callback.")
            }
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                containerColor = colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = { Text("Mappa Proprietà", color = colorScheme.onPrimary) },
                        navigationIcon = {
                            CircularIconActionButton(
                                onClick = onDismiss,
                                iconVector = Icons.Filled.Close,
                                contentDescription = "Chiudi mappa",
                                backgroundColor = Color.Transparent,
                                iconTint = colorScheme.onPrimary,
                                buttonSize = dimensions.iconSizeExtraLarge,
                                iconSize = dimensions.iconSizeMedium,
                                modifier = Modifier.padding(start = dimensions.spacingExtraSmall)
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
                    )
                }
            ) { paddingValues ->
                GoogleMap(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    cameraPositionState = cameraPositionState, // Usa lo stato INTERNO
                    properties = MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = true,
                        isBuildingEnabled = true,
                        isTrafficEnabled = true,
                        minZoomPreference = 8f,
                        maxZoomPreference = 20f
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        mapToolbarEnabled = true,
                        myLocationButtonEnabled = true,
                        scrollGesturesEnabled = true,
                        zoomGesturesEnabled = true,
                        tiltGesturesEnabled = true,
                        rotationGesturesEnabled = true
                    ),
                    onMapLoaded = {
                        Log.d("FullscreenMapDialog", "GoogleMap (internal state): onMapLoaded triggered.")
                        isMapLoaded = true
                    }
                ) {
                    Marker(
                        state = MarkerState(position = initialPropertyCoordinates),
                        title = "Posizione Proprietà"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTopAppBar(
    colorScheme: ColorScheme,
    navController: NavController,
    dimensions: Dimensions
) {
    TopAppBar(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 10.dp),
        navigationIcon = {
            CircularIconActionButton(
                onClick = { navController.popBackStack() },
                iconVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                backgroundColor = colorScheme.primary,
                iconTint = colorScheme.onPrimary,
                buttonSize = dimensions.iconSizeExtraLarge,
                iconSize = dimensions.iconSizeMedium
            )
        },
        title = { /* Empty title */ },
        actions = {
            val isFavorite = remember { mutableStateOf(false) }
            CircularIconActionButton(
                onClick = { isFavorite.value = !isFavorite.value },
                iconVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                backgroundColor = colorScheme.primary,
                iconTint = if (isFavorite.value) colorScheme.error else colorScheme.onPrimary,
                buttonSize = dimensions.iconSizeExtraLarge,
                iconSize = dimensions.iconSizeMedium
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyImagePager(
    pagerState: PagerState,
    images: List<Int>,
    coroutineScope: CoroutineScope,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    val totalImages = images.size
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Property Image ${page + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
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
                        val targetPage = if (pagerState.currentPage > 0) pagerState.currentPage - 1 else totalImages - 1
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                iconVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Image",
                backgroundColor = colorScheme.onBackground.copy(alpha = 0.6f),
                iconTint = colorScheme.background,
                buttonSize = dimensions.iconSizeExtraLarge,
                iconSize = dimensions.iconSizeMedium
            )
            Box(
                modifier = Modifier
                    .background(colorScheme.onBackground.copy(alpha = 0.6f), RoundedCornerShape(dimensions.cornerRadiusMedium))
                    .padding(horizontal = 12.dp, vertical = dimensions.paddingExtraSmall),
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/$totalImages",
                    color = colorScheme.background,
                    style = typography.labelMedium
                )
            }
            CircularIconActionButton(
                onClick = {
                    coroutineScope.launch {
                        val targetPage = if (pagerState.currentPage < totalImages - 1) pagerState.currentPage + 1 else 0
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                iconVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Image",
                backgroundColor = colorScheme.onBackground.copy(alpha = 0.6f),
                iconTint = colorScheme.background,
                buttonSize = dimensions.iconSizeExtraLarge,
                iconSize = dimensions.iconSizeMedium
            )
        }
    }
}

@Composable
fun PropertyPriceAndLocation(
    price: String,
    location: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium)
    ) {
        Text(text = price, style = typography.displayLarge, color = colorScheme.onBackground)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall)
        ) {
            Icon(Icons.Default.LocationOn, "Location", tint = colorScheme.onBackground, modifier = Modifier.size(dimensions.iconSizeSmall))
            Text(location, style = typography.bodyMedium, color = colorScheme.onBackground, modifier = Modifier.padding(start = dimensions.paddingExtraSmall))
        }
    }
}

@Composable
fun PropertyFeaturesRow(colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions = Dimensions) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PropertyFeature(Icons.Default.Hotel, "2 Letti", colorScheme, typography, dimensions)
        PropertyFeature(Icons.Default.Bathroom, "1 Bagno", colorScheme, typography, dimensions)
        PropertyFeature(Icons.Default.HomeWork, "3 Locali", colorScheme, typography, dimensions)
    }
}

@Composable
fun PropertyFeature(icon: ImageVector, label: String, colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(dimensions.paddingSmall)) {
        Icon(icon, label, tint = colorScheme.onBackground, modifier = Modifier.size(dimensions.iconSizeMedium))
        Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))
        Text(label, style = typography.labelMedium, color = colorScheme.onBackground)
    }
}

@Composable
fun PropertyCharacteristicsSection(
    characteristics: List<String>,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium)
    ) {
        Text("Caratteristiche", style = typography.titleMedium, color = colorScheme.onBackground, modifier = Modifier.padding(bottom = dimensions.paddingSmall))
        characteristics.forEach { characteristic ->
            PropertyCharacteristic(characteristic, colorScheme, typography, dimensions)
        }
    }
}

@Composable
fun PropertyCharacteristic(text: String, colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = dimensions.paddingExtraSmall)
    ) {
        Box(modifier = Modifier.size(6.dp).background(colorScheme.primary, CircleShape))
        Text(text, style = typography.labelLarge, color = colorScheme.onBackground, modifier = Modifier.padding(start = dimensions.paddingSmall))
    }
}

@Composable
fun PropertyDescriptionSection(
    description: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium)
    ) {
        Text("Descrizione", style = typography.titleMedium, color = colorScheme.onBackground, modifier = Modifier.padding(bottom = dimensions.paddingSmall))
        Text(description, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AgentInfoSection(
    agencyName: String,
    phoneNumber: String,
    address: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium)
    ) {
        Text(
            text = "Contatta l'Agenzia",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.spacingMedium)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Business, "Agency", tint = colorScheme.primary, modifier = Modifier.size(dimensions.iconSizeMedium))
            Column(modifier = Modifier.padding(start = dimensions.spacingMedium)) {
                Text(agencyName, style = typography.titleSmall.copy(fontWeight = FontWeight.Bold) , color = colorScheme.onBackground)
                Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))
                Text("Telefono: $phoneNumber", style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                Text(address, style = typography.bodySmall, color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    navController: NavController,
    phoneNumber: String,
    typography: Typography,
    context: android.content.Context,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        AppPrimaryButton(
            onClick = {
                val dialIntent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
                context.startActivity(dialIntent)
            },
            text = "Chiama ora", textStyle = typography.labelLarge, icon = Icons.Default.Phone, iconContentDescription = "Call",
            modifier = Modifier.fillMaxWidth(),
        )
        AppSecondaryButton(
            onClick = { navController.navigate(Screen.AppointmentBookingScreen.route) },
            text = "Fissa una Visita", icon = Icons.Default.DateRange, iconContentDescription = "Visit",
            modifier = Modifier.fillMaxWidth(),
        )
        AppSecondaryButton(
            onClick = { navController.navigate(Screen.PriceProposalScreen.route) },
            text = "Proponi Prezzo", icon = Icons.Default.Euro, iconContentDescription = "Propose price",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun ReportAdSection(colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DecorativeLine(colorScheme = colorScheme, isTop = true, dimensions = dimensions)
        Button(
            onClick = { /* TODO: Logica per segnalare annuncio */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensions.cornerRadiusSmall),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.surface,
                contentColor = colorScheme.error
            ),
            border = BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Default.WarningAmber, "Report", tint = colorScheme.error, modifier = Modifier.size(dimensions.iconSizeSmall))
            Text("Segnala Annuncio", color = colorScheme.error, style = typography.labelMedium, modifier = Modifier.padding(start = dimensions.paddingSmall))
        }
        DecorativeLine(colorScheme = colorScheme, isTop = false, dimensions = dimensions)
    }
}

@Composable
fun DecorativeLine(colorScheme: ColorScheme, isTop: Boolean, dimensions: Dimensions) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(1.dp)
            .background(brush = Brush.horizontalGradient(colors = listOf(Color.Transparent, colorScheme.outline.copy(alpha = 0.6f), Color.Transparent)))
            .padding(top = if(isTop) 0.dp else dimensions.paddingExtraSmall, bottom = if(isTop) dimensions.paddingExtraSmall else 0.dp)
    )
}

@Composable
fun SimilarListingsSection(
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.paddingMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensions.paddingMedium)
                .padding(bottom = dimensions.spacingSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Annunci simili",
                style = typography.titleMedium,
                color = colorScheme.onBackground
            )
            TextButton(onClick = { navController.navigate(Screen.ApartmentListingScreen.withArgs("", "")) }) {
                Text("Vedi tutti", style = typography.labelLarge, color = colorScheme.primary)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = dimensions.paddingMedium),
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            repeat(7) { index ->
                SimilarPropertyCard(
                    price = if (index == 0) "400.000 €" else "${300 + index * 50}.000 €",
                    modifier = Modifier
                        .width(200.dp)
                        .height(150.dp),
                    navController = navController,
                    colorScheme = colorScheme,
                    dimensions = dimensions,
                    typography = typography
                )
            }
        }
    }
}

@Composable
fun SimilarPropertyCard(
    navController: NavController,
    price: String,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    dimensions: Dimensions,
    typography: Typography
) {
    Card(
        modifier = modifier.clickable { navController.navigate(Screen.PropertyScreen.route) },
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        elevation = CardDefaults.cardElevation(dimensions.cardDefaultElevation)
    ) {
        Box {
            Image(painterResource(R.drawable.property1), "Similar Property", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Text(
                text = price,
                color = Color.White,
                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(dimensions.paddingSmall)
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "PropertyScreen Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "PropertyScreen Dark")
@Composable
fun PropertyDetailScreenPreview() {
    val navController = rememberNavController()
    PropertyScreen(navController = navController)
}