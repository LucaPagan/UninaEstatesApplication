package com.dieti.dietiestates25.ui.screen
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PropertyScreen(
    navController: NavController
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val context = LocalContext.current


        Scaffold(
            topBar = {
                Box {
                    // This is an empty box that ensures the top app bar space is reserved
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Main content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Property Images Section with navigation buttons
                    item {
                        // State for tracking current image
                        val totalImages = 10 // Esempio con 10 immagini
                        val currentImageIndex = remember { mutableStateOf(0) }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        ) {
                            // HorizontalPager for images
                            HorizontalPager(
                                state = rememberPagerState { totalImages },
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                // Update the current index
                                currentImageIndex.value = page

                                // Property Image
                                Image(
                                    painter = painterResource(
                                        // Qui dovresti caricare l'immagine corretta in base all'indice
                                        // Per questo esempio usiamo un placeholder
                                        id = R.drawable.property2
                                    ),
                                    contentDescription = "Property Image ${page + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // Image Navigation
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Previous image button
                                IconButton(
                                    onClick = {
                                        if (currentImageIndex.value > 0) {
                                            currentImageIndex.value -= 1
                                        } else {
                                            // Loop to the last image
                                            currentImageIndex.value = totalImages - 1
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Previous Image",
                                        tint = Color.White
                                    )
                                }

                                // Image counter indicator
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color.Black.copy(alpha = 0.6f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${currentImageIndex.value + 1}/$totalImages",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Next image button
                                IconButton(
                                    onClick = {
                                        if (currentImageIndex.value < totalImages - 1) {
                                            currentImageIndex.value += 1
                                        } else {
                                            // Loop to the first image
                                            currentImageIndex.value = 0
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Next Image",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Price and Location
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "€129,500",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
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
                                    text = "Appartamento Napoli, Via Francesco Girardi 90",
                                    fontSize = 14.sp,
                                    color = colorScheme.onBackground,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }

                    // Property Features
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            PropertyFeature(
                                icon = Icons.Default.Hotel,
                                label = "2 Letti"
                            )

                            PropertyFeature(
                                icon = Icons.Default.Bathroom,
                                label = "1 Bagno"
                            )

                            PropertyFeature(
                                icon = Icons.Default.Home,
                                label = "3 Locali"
                            )
                        }
                    }

                    // Caratteristiche Section
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Caratteristiche",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            PropertyCharacteristic(text = "115 m²")
                            PropertyCharacteristic(text = "2 Camere da letto")
                            PropertyCharacteristic(text = "1 Bagno")
                            PropertyCharacteristic(text = "3 Locali")
                            PropertyCharacteristic(text = "3° Piano, con ascensore")
                            PropertyCharacteristic(text = "Disponibilità di mezzi pubblici")
                        }
                    }

                    // Descrizione Section
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Descrizione",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = "Scopri questo accogliente appartamento situato nel cuore di Napoli, ideale per chi cerca comfort e comodità. L'immobile è situato al terzo piano di un edificio con ascensore, è perfetto per famiglie o coppie alla ricerca di uno spazio ben organizzato e luminoso.",
                                fontSize = 14.sp,
                                color = colorScheme.onBackground,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Agent Info
                    item {
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
                                        text = "Agenzia: Gianfranco Lombardi",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colorScheme.onBackground
                                    )

                                    Text(
                                        text = "Telefono: 081 192 6079",
                                        fontSize = 14.sp,
                                        color = colorScheme.onBackground
                                    )

                                    Text(
                                        text = "VIA G. PORZIO ISOLA Es 3, Napoli (NA), Campania, 80143",
                                        fontSize = 12.sp,
                                        color = colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    // Action Buttons
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    // Intent per aprire il tastierino numerico con un numero preimpostato
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:081 192 6079"))
                                    context.startActivity(dialIntent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = "Chiama ora")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    navController.navigate(Screen.AppointmentBookingScreen.route)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorScheme.onSecondary,
                                    containerColor = colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Visit",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = "Visita")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    navController.navigate(Screen.PriceProposalScreen.route)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colorScheme.onSecondary,
                                    containerColor = colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Euro,
                                    contentDescription = "Propose price",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(text = "Proponi prezzo")
                            }
                        }
                    }

                    // Report Ad with decorative lines and shadow
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top decorative line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(1.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Red.copy(alpha = 0.6f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

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
                                        tint = Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Segnala Annuncio",
                                        color = Color.Red,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                            }

                            // Bottom decorative line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(1.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Red.copy(alpha = 0.6f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
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
                                        navController = navController
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
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "View More",
                                            tint = colorScheme.primary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = "Vedi tutti",
                                            color = colorScheme.primary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
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
                        .background(Color.Transparent)
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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = colorScheme.onPrimary
                            )
                        }
                    },
                    title = { /* Empty title */ },
                    actions = {
                        IconButton(
                            onClick = {

                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorite",
                                tint = colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun PropertyFeature(
    icon: ImageVector,
    label: String
) {
    val colorScheme = MaterialTheme.colorScheme

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
            fontSize = 14.sp,
            color = colorScheme.onBackground
        )
    }
}

@Composable
fun PropertyCharacteristic(text: String) {
    val colorScheme = MaterialTheme.colorScheme

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
            fontSize = 14.sp,
            color = colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SimilarPropertyCard(
    navController: NavController,
    price: String,
    modifier: Modifier = Modifier
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
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
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
    DietiEstatesTheme {
        PropertyScreen(navController = navController)
    }
}