package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.typography

import kotlinx.coroutines.launch

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PropertyScreen(
    navController: NavController
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val context = LocalContext.current

        val coroutineScope = rememberCoroutineScope()

        // Elenco delle immagini da mostrare
        val propertyImages = listOf(
            R.drawable.property1,
            R.drawable.property2,
            // Aggiungi qui altre immagini
            // R.drawable.property4,
            // R.drawable.property5,
            // ecc.
        )

        val totalImages = propertyImages.size
        // Crea un PagerState controllabile
        val pagerState = rememberPagerState(initialPage = 0) { totalImages }

        Scaffold(
            topBar = {
                Box {
                    // This is an empty box that ensures the top app bar space is reserved
                }
            }

        ) { innerPadding ->
            //Box per l'immagine a scorrimento
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Main content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Property Images Section with navigation buttons
                    item {
                        PropertyImagePager(pagerState, propertyImages, coroutineScope, colorScheme)
                    }

                    // Price and Location
                    item {
                        PropertyPriceAndLocation(
                            price = "€129.500",
                            location = "Appartamento Napoli, Via Francesco Girardi 90",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }

                    // Property Features
                    item {
                        PropertyFeaturesRow(
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }

                    // Caratteristiche Section
                    item {
                        PropertyCharacteristicsSection(
                            characteristics = listOf(
                                "115 m²",
                                "2 Camere da letto",
                                "1 Bagno",
                                "3 Locali"
                            ),
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }

                    // Descrizione Section
                    item {
                        PropertyDescriptionSection(
                            description = "Scopri questo accogliente appartamento situato nel cuore di Napoli, ideale per chi cerca comfort e comodità. L'immobile è situato al terzo piano di un edificio con ascensore, è perfetto per famiglie o coppie alla ricerca di uno spazio ben organizzato e luminoso.",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }

                    // Agent Info
                    item {
                        AgentInfoSection(
                            agencyName = "Agenzia Gianfranco Lombardi",
                            phoneNumber = "081 192 6079",
                            address = "VIA G. PORZIO ISOLA Es 3, Napoli (NA), Campania, 80143",
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }

                    // Action Buttons
                    item {
                        ActionButtonsSection(
                            navController = navController,
                            phoneNumber = "081 192 6079",
                            typography = typography,
                            context = context
                        )
                    }

                    // Report Ad with decorative lines and shadow
                    item {
                        ReportAdSection(colorScheme = colorScheme, typography = typography)
                    }

                    // Similar Listings Section with Horizontal Scrolling
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Annunci simili",
                                style = typography.titleMedium,
                                color = colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Horizontal scrollable row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)

                            ) {
                                // Add up to 7 similar properties
                                repeat(7) { index ->
                                    SimilarPropertyCard(
                                        price = if (index == 0) "400.000" else "${300 + index * 50}.000",
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(150.dp),
                                        navController = navController,
                                        colorScheme = colorScheme
                                    )
                                }

                                // "View More" button at the end
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(colorScheme.background)
                                        .clickable {
                                            navController.navigate(Screen.ApartmentListingScreen.withArgs("", ""))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = "View More",
                                            tint = colorScheme.primary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = "Vedi tutti",
                                            color = colorScheme.primary,
                                            style = typography.labelLarge,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Add some padding at the bottom
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }

                // Fixed top navigation bar (stays visible during scroll)
                TopAppBar(
                    modifier = Modifier
                        .background(colorScheme.surfaceDim)
                        .padding(horizontal = 10.dp),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back",
                                tint = colorScheme.onPrimary
                            )
                        }
                    },
                    title = { /* Empty title */ },
                    actions = {
                        val isFavorite = remember { mutableStateOf(false) }

                        IconButton(
                            onClick = {

                                isFavorite.value = !isFavorite.value
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite",
                                tint = if (isFavorite.value) colorScheme.error else colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.surfaceDim
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyImagePager(
    pagerState: PagerState,
    images: List<Int>,
    coroutineScope: CoroutineScope,
    colorScheme: ColorScheme
) {
    val totalImages = images.size
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        // HorizontalPager per le immagini
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            // Immagine della proprietà
            Image(
                painter = painterResource(
                    id = images[page]
                ),
                contentDescription = "Property Image ${page + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Navigazione immagini
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Pulsante immagine precedente
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val targetPage = if (pagerState.currentPage > 0) {
                            pagerState.currentPage - 1
                        } else {
                            totalImages - 1 // Torna all'ultima immagine
                        }
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(colorScheme.onBackground.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Image",
                    tint = colorScheme.background
                )
            }

            // Indicatore del contatore immagini
            Box(
                modifier = Modifier
                    .background(
                        colorScheme.onBackground.copy(alpha = 0.6f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/$totalImages",
                    color = colorScheme.background,
                    style = typography.labelMedium // Utilizza la typography passata
                )
            }

            // Pulsante immagine successiva
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val targetPage = if (pagerState.currentPage < totalImages - 1) {
                            pagerState.currentPage + 1
                        } else {
                            0 // Torna alla prima immagine
                        }
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(colorScheme.onBackground.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Image",
                    tint = colorScheme.background
                )
            }
        }
    }
}

@Composable
fun PropertyPriceAndLocation(
    price: String,
    location: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = price,
            style = typography.displayLarge,
            color = colorScheme.onBackground
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = colorScheme.onBackground,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = location,
                style = typography.bodyMedium,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}


@Composable
fun PropertyFeaturesRow(colorScheme: ColorScheme, typography: Typography) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PropertyFeature(
            icon = Icons.Default.Hotel,
            label = "2 Letti",
            colorScheme = colorScheme,
            typography = typography
        )

        PropertyFeature(
            icon = Icons.Default.Bathroom,
            label = "1 Bagno",
            colorScheme = colorScheme,
            typography = typography
        )

        PropertyFeature(
            icon = Icons.Default.Home,
            label = "3 Locali",
            colorScheme = colorScheme,
            typography = typography
        )
    }
}

@Composable
fun PropertyFeature(
    icon: ImageVector,
    label: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorScheme.onBackground
        )
        Text(
            text = label,
            style = typography.labelMedium,
            color = colorScheme.onBackground
        )
    }
}


@Composable
fun PropertyCharacteristicsSection(
    characteristics: List<String>,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Caratteristiche",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        characteristics.forEach { characteristic ->
            PropertyCharacteristic(text = characteristic, colorScheme = colorScheme, typography = typography)
        }
    }
}


@Composable
fun PropertyCharacteristic(text: String, colorScheme: ColorScheme, typography: Typography) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(colorScheme.primary, CircleShape)
        )
        Text(
            text = text,
            style = typography.labelLarge,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
fun PropertyDescriptionSection(
    description: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Descrizione",
            style = typography.titleMedium,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = description,
            style = typography.labelLarge,
            color = colorScheme.onBackground
        )
    }
}

@Composable
fun AgentInfoSection(
    agencyName: String,
    phoneNumber: String,
    address: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Agency",
                tint = colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Agenzia: $agencyName",
                    style = typography.bodyLarge,
                    color = colorScheme.onBackground
                )

                Text(
                    text = "Telefono: $phoneNumber",
                    style = typography.labelLarge,
                    color = colorScheme.onBackground
                )

                Text(
                    text = address,
                    style = typography.labelMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    navController: NavController,
    phoneNumber: String,
    context: android.content.Context,
    typography: Typography
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AppPrimaryButton(
            onClick = {
                // Intent per aprire il tastierino numerico con un numero preimpostato
                val dialIntent = Intent(Intent.ACTION_DIAL,
                    "tel:$phoneNumber".toUri())
                context.startActivity(dialIntent)
            },
            text = "Chiama ora",
            textStyle = typography.labelLarge,
            icon = Icons.Default.Phone,
            iconContentDescription = "Call",
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppSecondaryButton(
            onClick = {
                navController.navigate(Screen.AppointmentBookingScreen.route)
            },
            text = "Visita",
            icon = Icons.Default.DateRange,
            iconContentDescription = "Visit",
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppSecondaryButton(
            onClick = {
                navController.navigate(Screen.PriceProposalScreen.route)
            },
            text = "Proponi prezzo",
            icon = Icons.Default.Euro,
            iconContentDescription = "Propose price",
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Composable
fun ReportAdSection(colorScheme: ColorScheme, typography: Typography) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top decorative line (estratta)
        DecorativeLine(colorScheme = colorScheme, isTop = true)

        // Report button with shadow
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.background
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Report",
                    tint = colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Segnala Annuncio",
                    color = colorScheme.error,
                    style = typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Bottom decorative line (estratta)
        DecorativeLine(colorScheme = colorScheme, isTop = false)
    }
}

@Composable
fun DecorativeLine(colorScheme: ColorScheme, isTop: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colorScheme.surfaceDim,
                        colorScheme.error.copy(alpha = 0.6f),
                        colorScheme.surfaceDim
                    )
                )
            )
            // Aggiungi un piccolo margine per separare dalla riga successiva/precedente
            .padding(top = if(isTop) 0.dp else 4.dp, bottom = if(isTop) 4.dp else 0.dp)
    )
}

@Composable
fun SimilarPropertyCard(
    navController: NavController,
    price: String,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable {
                navController.navigate(Screen.PropertyScreen.route)
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.property1),
                contentDescription = "Similar Property",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (price.isNotEmpty()) {
                Text(
                    text = price,
                    color = colorScheme.onBackground,
                    style = typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(
                            colorScheme.background.copy(alpha = 0.6f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyDetailScreenPreview() {
    val navController = rememberNavController()
    PropertyScreen(navController = navController)
}