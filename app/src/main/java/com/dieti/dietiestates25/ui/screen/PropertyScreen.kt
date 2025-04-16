package com.dieti.dietiestates25.ui.screen

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
import com.dieti.dietiestates25.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    onBackPressed: () -> Unit = {},
    onFavoritePressed: () -> Unit = {}
) {
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
                // Property Images Section
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        // Property Image
                        Image(
                            painter = painterResource(id = R.drawable.property1),
                            contentDescription = "Property Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Image Navigation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { },
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

                            IconButton(
                                onClick = { },
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

                        // Time display at top left
                        Text(
                            text = "9:30",
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
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
                            color = TextGray
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = TextGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Appartamento Napoli, Via Francesco Girardi 90",
                                fontSize = 14.sp,
                                color = TextGray,
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
                            color = TextGray,
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
                            color = TextGray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Scopri questo accogliente appartamento situato nel cuore di Napoli, ideale per chi cerca comfort e comodità. L'immobile è situato al terzo piano di un edificio con ascensore, è perfetto per famiglie o coppie alla ricerca di uno spazio ben organizzato e luminoso.",
                            fontSize = 14.sp,
                            color = TextGray,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Map Section
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 16.dp)
                            .background(SurfaceGray, RoundedCornerShape(8.dp))
                    ) {
                        // Here you would integrate an actual map like Google Maps
                        // For the mockup, we're using a placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = TealPrimary,
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }
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
                                tint = TealPrimary,
                                modifier = Modifier.size(24.dp)
                            )

                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = "Agenzia: Gianfranco Lombardi",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextGray
                                )

                                Text(
                                    text = "Telefono: 081 1929 6079",
                                    fontSize = 14.sp,
                                    color = TextGray
                                )

                                Text(
                                    text = "VIA G. PORZIO ISOLA Es 3, Napoli (NA), Campania, 80143",
                                    fontSize = 12.sp,
                                    color = TextGray.copy(alpha = 0.7f)
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
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
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
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TealPrimary
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
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TealPrimary
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

                // Report Ad
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
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
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Similar Listings
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
                            color = TextGray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SimilarPropertyCard(
                                price = "400.000",
                                modifier = Modifier.weight(1f)
                            )

                            SimilarPropertyCard(
                                price = "",  // This can be updated as needed
                                modifier = Modifier.weight(1f)
                            )
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
                modifier = Modifier.background(Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                        modifier = Modifier
                            .size(40.dp)
                            .background(TealPrimary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = { /* Empty title */ },
                actions = {
                    IconButton(
                        onClick = onFavoritePressed,
                        modifier = Modifier
                            .size(40.dp)
                            .background(TealPrimary, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = Color.White
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

@Composable
fun PropertyFeature(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextGray
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextGray
        )
    }
}

@Composable
fun PropertyCharacteristic(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(TealPrimary, CircleShape)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextGray,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun SimilarPropertyCard(
    price: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp),
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
    PropertyDetailScreen()
}