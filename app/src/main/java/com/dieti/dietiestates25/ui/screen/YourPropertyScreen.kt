package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppPropertyCard
import com.dieti.dietiestates25.ui.components.AppPropertyViewButton
import com.dieti.dietiestates25.ui.model.modelsource.sampleListingProperties // Using sample data
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourPropertyScreen(
    navController: NavController,
    idUtente: String // Assuming you might need this for fetching user-specific properties
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    // Properties to display - in a real app, you'd fetch properties for 'idUtente'
    val userProperties = sampleListingProperties() // Using sample data for now

    val gradientColors = arrayOf(
        0.0f to colorScheme.primary, 0.20f to colorScheme.background,
        0.60f to colorScheme.background, 1.0f to colorScheme.primary
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colorStops = gradientColors))
    ) {
        // Simple TopAppBar for this screen
        TopAppBar(
            title = {
                Text(
                    "Le Tue Proprietà",
                    style = typography.titleLarge,
                    color = colorScheme.onPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.primary
            ),
            modifier = Modifier.statusBarsPadding() // Apply padding for the status bar
        )

        if (userProperties.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.paddingLarge),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Non hai nessuna proprietà al momento.",
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = dimensions.paddingMedium,
                    end = dimensions.paddingMedium,
                    top = dimensions.paddingMedium, // Adjusted top padding
                    bottom = dimensions.paddingLarge
                ),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                items(items = userProperties, key = { it.id }) { property ->
                    AppPropertyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp), // Same height as in ApartmentListingScreen
                        price = property.price,
                        imageResId = property.imageRes,
                        address = property.location,
                        details = listOfNotNull(
                            property.type,
                            property.areaMq?.let { "$it mq" },
                            property.bathrooms?.let { numBagni ->
                                if (numBagni == 1) "$numBagni bagno" else "$numBagni bagni"
                            }
                        ).filter { it.isNotBlank() },
                        onClick = { },
                        actionButton = {
                            AppPropertyViewButton(
                                onClick = {
                                    navController.navigate(Screen.PropertyScreen.route)
                                }
                            )
                        },
                        horizontalMode = false,
                        imageHeightVerticalRatio = 0.50f,
                        elevationDp = Dimensions.elevationSmall
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
    YourPropertyScreen(
        navController = navController,
        idUtente = "previewUser"
    )
}
