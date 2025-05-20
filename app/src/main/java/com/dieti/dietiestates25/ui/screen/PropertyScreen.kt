package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.theme.Dimensions

import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

import android.content.Intent

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PropertyScreen(
    navController: NavController
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val context = LocalContext.current
        val dimensions = Dimensions // Istanza locale per un accesso più breve

        val coroutineScope = rememberCoroutineScope()

        val propertyImages = listOf(
            R.drawable.property1,
            R.drawable.property2,
        )

        val totalImages = propertyImages.size
        val pagerState = rememberPagerState(initialPage = 0) { totalImages }

        Scaffold(
            topBar = {
                Box {
                    // Empty box
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    item {
                        PropertyImagePager(
                            pagerState,
                            propertyImages,
                            coroutineScope,
                            colorScheme,
                            typography,
                            dimensions
                        )
                    }
                    item {
                        PropertyPriceAndLocation(
                            "€129.500",
                            "Appartamento Napoli, Via Francesco Girardi 90",
                            colorScheme,
                            typography,
                            dimensions
                        )
                    }
                    item {
                        PropertyFeaturesRow(colorScheme, typography)
                    }
                    item {
                        PropertyCharacteristicsSection(
                            listOf(
                                "115 m²",
                                "2 Camere da letto",
                                "1 Bagno",
                                "3 Locali"
                            ), colorScheme, typography, dimensions
                        )
                    }
                    item {
                        PropertyDescriptionSection(
                            "Scopri questo accogliente appartamento situato nel cuore di Napoli, ideale per chi cerca comfort e comodità. L'immobile è situato al terzo piano di un edificio con ascensore, è perfetto per famiglie o coppie alla ricerca di uno spazio ben organizzato e luminoso.",
                            colorScheme,
                            typography,
                            dimensions
                        )
                    }
                    item {
                        AgentInfoSection(
                            "Agenzia Gianfranco Lombardi",
                            "081 192 6079",
                            "VIA G. PORZIO ISOLA Es 3, Napoli (NA), Campania, 80143",
                            colorScheme,
                            typography,
                            dimensions
                        )
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
                        Spacer(modifier = Modifier.height(dimensions.paddingExtraLarge * 2 + dimensions.paddingMedium)) // Es. 80.dp
                    }
                }
                PropertyTopAppBar(colorScheme, navController, dimensions)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTopAppBar(
    colorScheme: ColorScheme,
    navController: NavController,
    dimensions: Dimensions // Aggiunto
) {
    TopAppBar(
        modifier = Modifier
            .statusBarsPadding() // Aggiunto per correttezza se questo TopAppBar è in cima
            .padding(horizontal = 10.dp), // 10.dp non in Dimensions, lasciato invariato
        navigationIcon = {
            CircularIconActionButton(
                onClick = { navController.popBackStack() },
                iconVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                backgroundColor = colorScheme.primary,
                iconTint = colorScheme.onPrimary,
                buttonSize = dimensions.iconSizeExtraLarge, // Es. 48.dp o 40.dp
                iconSize = dimensions.iconSizeMedium // Es. 24.dp
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
                buttonSize = dimensions.iconSizeExtraLarge, // Es. 48.dp o 40.dp
                iconSize = dimensions.iconSizeMedium // Es. 24.dp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surface.copy(alpha = 0.0f) // Trasparente se il contenuto scorre dietro
            // O colorScheme.surfaceDim se deve avere un colore
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
    typography: Typography, // Aggiunto per il testo del contatore
    dimensions: Dimensions  // Aggiunto
) {
    val totalImages = images.size
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp) // Altezza specifica, non un padding
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
                .padding(dimensions.paddingMedium), // SOSTITUITO 16.dp
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
                buttonSize = dimensions.iconSizeExtraLarge, // Es. 40.dp o 48.dp
                iconSize = dimensions.iconSizeMedium // Es. 24.dp
            )
            Box(
                modifier = Modifier
                    .background(colorScheme.onBackground.copy(alpha = 0.6f), RoundedCornerShape(dimensions.cornerRadiusMedium)) // SOSTITUITO 12.dp
                    .padding(horizontal = 12.dp, vertical = dimensions.paddingExtraSmall), // 12.dp non in Dimensions, SOSTITUITO 4.dp
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
                buttonSize = dimensions.iconSizeExtraLarge, // Es. 40.dp o 48.dp
                iconSize = dimensions.iconSizeMedium // Es. 24.dp
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
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium) // SOSTITUITO 16.dp
    ) {
        Text(text = price, style = typography.displayLarge, color = colorScheme.onBackground)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = dimensions.paddingExtraSmall) // SOSTITUITO 4.dp
        ) {
            Icon(Icons.Default.LocationOn, "Location", tint = colorScheme.onBackground, modifier = Modifier.size(dimensions.iconSizeSmall)) // SOSTITUITO 16.dp
            Text(location, style = typography.bodyMedium, color = colorScheme.onBackground, modifier = Modifier.padding(start = dimensions.paddingExtraSmall)) // SOSTITUITO 4.dp
        }
    }
}

@Composable
fun PropertyFeaturesRow(colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions = Dimensions) { // Aggiunto dimensions con default
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium), // SOSTITUITO 16.dp
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PropertyFeature(Icons.Default.Hotel, "2 Letti", colorScheme, typography)
        PropertyFeature(Icons.Default.Bathroom, "1 Bagno", colorScheme, typography)
        PropertyFeature(Icons.Default.Home, "3 Locali", colorScheme, typography)
    }
}

@Composable
fun PropertyFeature(icon: ImageVector, label: String, colorScheme: ColorScheme, typography: Typography) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, label, tint = colorScheme.onBackground)
        Text(label, style = typography.labelMedium, color = colorScheme.onBackground)
    }
}

@Composable
fun PropertyCharacteristicsSection(
    characteristics: List<String>,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium) // SOSTITUITO 16.dp
    ) {
        Text("Caratteristiche", style = typography.titleMedium, color = colorScheme.onBackground, modifier = Modifier.padding(bottom = dimensions.paddingSmall)) // SOSTITUITO 8.dp
        characteristics.forEach { characteristic ->
            PropertyCharacteristic(characteristic, colorScheme, typography, dimensions)
        }
    }
}

@Composable
fun PropertyCharacteristic(text: String, colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions) { // Aggiunto dimensions
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = dimensions.paddingExtraSmall) // SOSTITUITO 4.dp
    ) {
        Box(modifier = Modifier.size(6.dp).background(colorScheme.primary, CircleShape)) // 6.dp non in Dimensions
        Text(text, style = typography.labelLarge, color = colorScheme.onBackground, modifier = Modifier.padding(start = dimensions.paddingSmall)) // SOSTITUITO 8.dp
    }
}

@Composable
fun PropertyDescriptionSection(
    description: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium) // SOSTITUITO 16.dp
    ) {
        Text("Descrizione", style = typography.titleMedium, color = colorScheme.onBackground, modifier = Modifier.padding(bottom = dimensions.paddingSmall)) // SOSTITUITO 8.dp
        Text(description, style = typography.labelLarge, color = colorScheme.onBackground)
    }
}

@Composable
fun AgentInfoSection(
    agencyName: String,
    phoneNumber: String,
    address: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium) // SOSTITUITO 16.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Home, "Agency", tint = colorScheme.primary, modifier = Modifier.size(dimensions.iconSizeMedium)) // SOSTITUITO 24.dp
            Column(modifier = Modifier.padding(start = dimensions.paddingSmall)) { // SOSTITUITO 8.dp
                Text("Agenzia: $agencyName", style = typography.bodyLarge, color = colorScheme.onBackground)
                Text("Telefono: $phoneNumber", style = typography.labelLarge, color = colorScheme.onBackground)
                Text(address, style = typography.labelMedium, color = colorScheme.onBackground.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    navController: NavController,
    phoneNumber: String,
    typography: Typography, // Typography passata
    context: android.content.Context,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium) // SOSTITUITO 16.dp
    ) {
        AppPrimaryButton(
            onClick = {
                val dialIntent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
                context.startActivity(dialIntent)
            },
            text = "Chiama ora", textStyle = typography.labelLarge, icon = Icons.Default.Phone, iconContentDescription = "Call",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(dimensions.spacingSmall)) // SOSTITUITO 8.dp
        AppSecondaryButton(
            onClick = { navController.navigate(Screen.AppointmentBookingScreen.route) },
            text = "Visita", icon = Icons.Default.DateRange, iconContentDescription = "Visit",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(dimensions.spacingSmall)) // SOSTITUITO 8.dp
        AppSecondaryButton(
            onClick = { navController.navigate(Screen.PriceProposalScreen.route) },
            text = "Proponi prezzo", icon = Icons.Default.Euro, iconContentDescription = "Propose price",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun ReportAdSection(colorScheme: ColorScheme, typography: Typography, dimensions: Dimensions) { // Aggiunto dimensions
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingMedium), // SOSTITUITO 16.dp per entrambi
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DecorativeLine(colorScheme = colorScheme, isTop = true, dimensions = dimensions) // Passa dimensions
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensions.cornerRadiusSmall), // SOSTITUITO 8.dp (o cornerRadiusMedium se 8dp era inteso come medium)
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.background)
            ) {
                Icon(Icons.Default.Warning, "Report", tint = colorScheme.error, modifier = Modifier.size(dimensions.iconSizeSmall)) // SOSTITUITO 16.dp
                Text("Segnala Annuncio", color = colorScheme.error, style = typography.labelLarge, modifier = Modifier.padding(start = dimensions.paddingSmall)) // SOSTITUITO 8.dp
            }
        }
        DecorativeLine(colorScheme = colorScheme, isTop = false, dimensions = dimensions) // Passa dimensions
    }
}

@Composable
fun DecorativeLine(colorScheme: ColorScheme, isTop: Boolean, dimensions: Dimensions) { // Aggiunto dimensions
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(1.dp) // 1.dp non in Dimensions
            .background(brush = Brush.horizontalGradient(colors = listOf(colorScheme.surface.copy(alpha = 0.0f), colorScheme.error.copy(alpha = 0.6f), colorScheme.surface.copy(alpha = 0.0f)))) // Usato surface.copy alpha 0 per trasparenza ai lati
            .padding(top = if(isTop) 0.dp else dimensions.paddingExtraSmall, bottom = if(isTop) dimensions.paddingExtraSmall else 0.dp) // SOSTITUITO 4.dp
    )
}

@Composable
fun SimilarListingsSection( // Creato un Composable separato per questa sezione
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensions.paddingMedium) // SOSTITUITO 16.dp
    ) {
        Text(
            text = "Annunci simili",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = dimensions.paddingSmall) // SOSTITUITO 8.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = dimensions.paddingSmall), // SOSTITUITO 8.dp
            horizontalArrangement = Arrangement.spacedBy(12.dp) // 12.dp non in Dimensions
        ) {
            repeat(7) { index ->
                SimilarPropertyCard(
                    price = if (index == 0) "400.000" else "${300 + index * 50}.000",
                    modifier = Modifier
                        .width(200.dp) // Valori specifici, non padding
                        .height(150.dp),// Valori specifici, non padding
                    navController = navController,
                    colorScheme = colorScheme,
                    dimensions = dimensions, // Passa dimensions
                    typography = typography // Passa typography
                )
            }
            ViewMoreCard( // Estratto in un Composable
                navController = navController,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
    }
}

@Composable
fun SimilarPropertyCard(
    navController: NavController,
    price: String,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    dimensions: Dimensions, // Aggiunto
    typography: Typography // Aggiunto
) {
    Card(
        modifier = modifier
            .clickable { navController.navigate(Screen.PropertyScreen.route) },
        shape = RoundedCornerShape(dimensions.cornerRadiusSmall) // SOSTITUITO 8.dp (o cornerRadiusMedium se 8dp era inteso come medium)
    ) {
        Box {
            Image(painterResource(R.drawable.property1), "Similar Property", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            if (price.isNotEmpty()) {
                Text(
                    text = price,
                    color = colorScheme.onBackground, // Cambiato per leggibilità
                    style = typography.titleMedium, // Usato typography passato
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(dimensions.paddingSmall) // SOSTITUITO 8.dp
                        .background(colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(dimensions.cornerRadiusSmall)) // SOSTITUITO 4.dp
                        .padding(dimensions.paddingExtraSmall) // SOSTITUITO 4.dp
                )
            }
        }
    }
}

@Composable
fun ViewMoreCard( // Nuovo Composable per la card "Vedi tutti"
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .width(120.dp) // Valore specifico
            .height(150.dp) // Valore specifico
            .clip(RoundedCornerShape(dimensions.cornerRadiusSmall)) // SOSTITUITO 8.dp
            .background(colorScheme.surfaceVariant) // Cambiato per differenziarlo un po'
            .clickable { navController.navigate(Screen.ApartmentListingScreen.withArgs("", "")) }, // Assumi che withArgs sia definito
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "View More", tint = colorScheme.primary, modifier = Modifier.size(dimensions.iconSizeLarge)) // SOSTITUITO 36.dp
            Text("Vedi tutti", color = colorScheme.primary, style = typography.labelLarge, modifier = Modifier.padding(top = dimensions.paddingExtraSmall)) // SOSTITUITO 4.dp
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PropertyDetailScreenPreview() {
    val navController = rememberNavController()
    PropertyScreen(navController = navController)
}