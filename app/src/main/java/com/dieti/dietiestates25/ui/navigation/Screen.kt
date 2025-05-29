package com.dieti.dietiestates25.ui.navigation

import android.net.Uri // <<-- IMPORT AGGIUNTO
import com.dieti.dietiestates25.ui.model.FilterModel
// Rimosso URLEncoder se non più usato o usato solo per query params specifici
// import java.net.URLEncoder
// import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    data object WelcomeScreen : Screen("welcome_screen")

    data object HomeScreen : Screen("home_screen") {
        fun withIdUtente(idUtente: String): String {
            return "$route/${Uri.encode(idUtente)}" // Usa Uri.encode per path params
        }
    }

    data object SearchScreen : Screen("search_screen") {
        fun withIdUtente(idUtente: String): String {
            return "$route/${Uri.encode(idUtente)}"
        }
    }

    data object SearchFilterScreen : Screen("search_filter_screen") {
        fun withInitialArgs(idUtente: String, comune: String, ricercaQueryText: String): String {
            return "$route/${Uri.encode(idUtente)}/${Uri.encode(comune)}/${Uri.encode(ricercaQueryText)}"
        }
    }

    data object ApartmentListingScreen : Screen("apartment_listing_screen") {
        fun buildRoute(
            idUtentePath: String,
            comunePath: String,
            ricercaPath: String,
            filters: FilterModel? = null
        ): String {
            val basePathWithArgs = "$route/${Uri.encode(idUtentePath)}/${Uri.encode(comunePath)}/${Uri.encode(ricercaPath)}"

            if (filters == null) {
                return basePathWithArgs
            }

            val queryParams = mutableListOf<String>()
            filters.purchaseType?.let { queryParams.add("purchaseType=${Uri.encode(it)}") }
            filters.minPrice?.let { queryParams.add("minPrice=$it") }
            filters.maxPrice?.let { queryParams.add("maxPrice=$it") }
            filters.minSurface?.let { queryParams.add("minSurface=$it") }
            filters.maxSurface?.let { queryParams.add("maxSurface=$it") }
            filters.minRooms?.let { queryParams.add("minRooms=$it") }
            filters.maxRooms?.let { queryParams.add("maxRooms=$it") }
            filters.bathrooms?.let { queryParams.add("bathrooms=$it") }
            filters.condition?.let { queryParams.add("condition=${Uri.encode(it)}") }

            return if (queryParams.isNotEmpty()) {
                basePathWithArgs + "?" + queryParams.joinToString("&")
            } else {
                basePathWithArgs
            }
        }
    }

    data object PropertyScreen : Screen("property_screen") {
        fun withId(propertyId: String): String {
            return "$route/${Uri.encode(propertyId)}"
        }
    }

    data object PriceProposalScreen : Screen("price_screen")
    data object PropertySellScreen : Screen("property_sell_screen") {
        fun withIdUtente(idUtente: String): String {
            return "$route/${Uri.encode(idUtente)}"
        }
    }
    data object AppointmentBookingScreen : Screen("appointment_screen")
    data object NotificationScreen : Screen("notification_screen")
    data object NotificationDetailScreen : Screen("notification_detail_screen")
    data object ProfileScreen : Screen("profile_screen")

    data object MapSearchScreen : Screen("map_search_screen") {
        fun buildRoute(
            idUtentePath: String,
            comunePath: String,
            ricercaPath: String,
            filters: FilterModel? = null
        ): String {
            val basePathWithArgs = "$route/${Uri.encode(idUtentePath)}/${Uri.encode(comunePath)}/${Uri.encode(ricercaPath)}"

            if (filters == null) {
                return basePathWithArgs
            }

            val queryParams = mutableListOf<String>()
            filters.purchaseType?.let { queryParams.add("purchaseType=${Uri.encode(it)}") }
            filters.minPrice?.let { queryParams.add("minPrice=$it") }
            filters.maxPrice?.let { queryParams.add("maxPrice=$it") }
            filters.minSurface?.let { queryParams.add("minSurface=$it") }
            filters.maxSurface?.let { queryParams.add("maxSurface=$it") }
            filters.minRooms?.let { queryParams.add("minRooms=$it") }
            filters.maxRooms?.let { queryParams.add("maxRooms=$it") }
            filters.bathrooms?.let { queryParams.add("bathrooms=$it") }
            filters.condition?.let { queryParams.add("condition=${Uri.encode(it)}") }

            return if (queryParams.isNotEmpty()) {
                basePathWithArgs + "?" + queryParams.joinToString("&")
            } else {
                basePathWithArgs
            }
        }
    }

    data object SearchTypeSelectionScreen : Screen("search_type_selection_screen") {
        fun buildRoute(
            idUtentePath: String,
            comunePath: String,
            ricercaPath: String,
            filters: FilterModel? = null
        ): String {
            val basePathWithArgs = "$route/${Uri.encode(idUtentePath)}/${Uri.encode(comunePath)}/${Uri.encode(ricercaPath)}"

            if (filters == null) {
                return basePathWithArgs
            }

            val queryParams = mutableListOf<String>()
            filters.purchaseType?.let { queryParams.add("purchaseType=${Uri.encode(it)}") }
            filters.minPrice?.let { queryParams.add("minPrice=$it") }
            filters.maxPrice?.let { queryParams.add("maxPrice=$it") }
            filters.minSurface?.let { queryParams.add("minSurface=$it") }
            filters.maxSurface?.let { queryParams.add("maxSurface=$it") }
            filters.minRooms?.let { queryParams.add("minRooms=$it") }
            filters.maxRooms?.let { queryParams.add("maxRooms=$it") }
            filters.bathrooms?.let { queryParams.add("bathrooms=$it") }
            filters.condition?.let { queryParams.add("condition=${Uri.encode(it)}") }

            return if (queryParams.isNotEmpty()) {
                basePathWithArgs + "?" + queryParams.joinToString("&")
            } else {
                basePathWithArgs
            }
        }
    }

    // Funzione generica withArgs modificata per usare Uri.encode
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/${Uri.encode(arg)}")
            }
        }
    }

    object FullScreenMapScreen : Screen("fullscreen_map_screen") {
        fun withPosition(latitude: Double, longitude: Double, zoom: Float): String {
            // Converti i numeri in stringhe e poi esegui l'encode
            val encodedLat = Uri.encode(latitude.toString())
            val encodedLng = Uri.encode(longitude.toString())
            val encodedZoom = Uri.encode(zoom.toString())

            // Costruisci la route con i segmenti encodati
            return "$route/$encodedLat/$encodedLng/$encodedZoom"
        }
    }
}