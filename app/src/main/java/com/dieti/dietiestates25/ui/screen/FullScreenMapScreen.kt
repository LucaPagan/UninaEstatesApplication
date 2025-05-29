package com.dieti.dietiestates25.ui.screen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FullScreenMapScreen(
    navController: NavController,
    latitude: Double,
    longitude: Double,
    initialZoom: Float
) {
    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme
    val propertyCoordinates = remember(latitude, longitude) { LatLng(latitude, longitude) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(propertyCoordinates, initialZoom)
    }
    var isMapLoaded by remember { mutableStateOf(false) }

    // 1. Stato per i permessi di localizzazione
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    // 2. LaunchedEffect per richiedere i permessi quando la schermata appare (o quando serve)
    LaunchedEffect(locationPermissionsState) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(isMapLoaded, propertyCoordinates, initialZoom, locationPermissionsState.allPermissionsGranted) {
        if (isMapLoaded) {
            Log.d("FullScreenMapScreen", "Map is loaded. Animating camera to: $propertyCoordinates, zoom: $initialZoom. Permissions granted: ${locationPermissionsState.allPermissionsGranted}")
            try {
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(propertyCoordinates, initialZoom)
                    ),
                    1000
                )
            } catch (e: Exception) {
                Log.e("FullScreenMapScreen", "Error animating camera: ${e.message}", e)
            }
        } else {
            Log.d("FullScreenMapScreen", "Map not loaded yet or permissions not granted. Waiting.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mappa Proprietà", color = colorScheme.onPrimary) },
                navigationIcon = {
                    CircularIconActionButton(
                        onClick = { navController.popBackStack() },
                        iconVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        backgroundColor = Color.Transparent,
                        iconTint = colorScheme.onPrimary,
                        buttonSize = dimensions.iconSizeExtraLarge,
                        iconSize = dimensions.iconSizeMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 3. Mostra la mappa solo se i permessi sono concessi, o gestisci il caso contrario
            if (locationPermissionsState.allPermissionsGranted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.NORMAL,
                        isMyLocationEnabled = false, // Ora puoi provare a tenerlo true
                        isBuildingEnabled = true,
                        isTrafficEnabled = true
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        mapToolbarEnabled = true,
                        myLocationButtonEnabled = false, // Ora puoi provare a tenerlo true
                        scrollGesturesEnabled = true,
                        zoomGesturesEnabled = true,
                        tiltGesturesEnabled = true,
                        rotationGesturesEnabled = true
                    ),
                    onMapLoaded = {
                        Log.d("FullScreenMapScreen", "onMapLoaded callback triggered.")
                        isMapLoaded = true
                    }
                ) {
                    Marker(
                        state = MarkerState(position = propertyCoordinates),
                        title = "Posizione Proprietà"
                    )
                }
            } else {
                // Mostra un messaggio o un'interfaccia utente alternativa
                // se i permessi non sono concessi.
                // Potresti anche aggiungere un pulsante per richiederli di nuovo.
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("I permessi di localizzazione sono necessari per mostrare la tua posizione sulla mappa.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Richiedi Permessi")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO, name = "FullScreenMapScreen Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "FullScreenMapScreen Dark")
@Composable
fun FullScreenMapScreenPreview() {
    FullScreenMapScreen(
        navController = rememberNavController(),
        latitude = 40.8518, // Napoli
        longitude = 14.2681,
        initialZoom = 15f
    )
}