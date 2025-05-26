package com.dieti.dietiestates25.ui.components // o dove preferisci metterlo

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize // Aggiunto import mancante per la preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview // Per la Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
// Import CORRETTI per MapLibre

import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme


// SOSTITUISCI CON LA TUA CHIAVE API REALE
private const val GEOAPIFY_API_KEY = "LA_TUA_CHIAVE_API_GEOAPIFY" // Sostituisci!
private val GEOAPIFY_STYLE_URL = "https://maps.geoapify.com/v1/styles/osm-bright/style.json?apiKey=$GEOAPIFY_API_KEY"

// Valori di default per la camera iniziale (esempio: Napoli)
private val INITIAL_LATITUDE = 40.8518
private val INITIAL_LONGITUDE = 14.2681
private const val INITIAL_ZOOM_LEVEL = 10.0

@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    initialCenter: LatLng = LatLng(INITIAL_LATITUDE, INITIAL_LONGITUDE),
    initialZoom: Double = INITIAL_ZOOM_LEVEL,
    onMapReady: (MapLibreMap) -> Unit = {} // Callback quando la mappa è pronta
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var isMapReady by remember { mutableStateOf(false) }
    var maplibreMapInstance: MapLibreMap? by remember { mutableStateOf(null) }


    DisposableEffect(lifecycleOwner, mapView) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> {
                    // Pulisci le risorse della mappa in modo esplicito
                    maplibreMapInstance?.clear() // Rimuovi marker, layer, ecc.
                    maplibreMapInstance = null
                    mapView.onDestroy()
                }
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            // Chiamare onDestroy qui è importante, ma se l'observer lo fa già
            // potrebbe essere ridondante. Assicurati che venga chiamato una volta.
            // Se mapView non è null e non è già stato distrutto dall'observer:
            // if (mapView.isDestroyed == false) { // isDestroyed non è una API pubblica standard
            //    mapView.onDestroy()
            // }
            // Per sicurezza, l'observer dovrebbe gestire onDestroy.
            // Se l'observer non lo fa in tempo, potresti doverlo chiamare qui.
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(maplibreMap: MapLibreMap) {
                        maplibreMapInstance = maplibreMap // Salva l'istanza
                        maplibreMap.setStyle(Style.Builder().fromUri(GEOAPIFY_STYLE_URL)) { style ->
                            println("MapLibre Map Style caricato con successo.")
                            isMapReady = true
                            onMapReady(maplibreMap)
                        }

                        maplibreMap.cameraPosition = org.maplibre.android.camera.CameraPosition.Builder() // Usa il CameraPosition di MapLibre
                            .target(initialCenter)
                            .zoom(initialZoom)
                            .build()

                        maplibreMap.uiSettings.isZoomControlsEnabled = true
                        maplibreMap.uiSettings.isZoomGesturesEnabled = true
                    }
                })
            }
            mapView
        },
        modifier = modifier
    )

    LaunchedEffect(initialCenter, initialZoom, isMapReady, maplibreMapInstance) {
        if (isMapReady && maplibreMapInstance != null) {
            maplibreMapInstance?.animateCamera(
                org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition( // Usa CameraUpdateFactory di MapLibre
                    org.maplibre.android.camera.CameraPosition.Builder() // Usa CameraPosition di MapLibre
                        .target(initialCenter)
                        .zoom(initialZoom)
                        .build()
                ),
                1000 // Durata animazione in ms
            )
        }
    }
}
