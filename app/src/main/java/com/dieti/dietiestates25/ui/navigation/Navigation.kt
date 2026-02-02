package com.dieti.dietiestates25.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.ui.features.property.ApartmentListingScreen
import com.dieti.dietiestates25.ui.features.home.HomeScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerScreen
import com.dieti.dietiestates25.ui.features.notification.NotificationScreen
import com.dieti.dietiestates25.ui.features.profile.ProfileScreen
import com.dieti.dietiestates25.ui.features.property.PropertySellScreen
import com.dieti.dietiestates25.ui.features.appointments.AppointmentBookingScreen
import com.dieti.dietiestates25.ui.features.property.PriceProposalScreen
import com.dieti.dietiestates25.ui.features.property.PropertyScreen
import com.dieti.dietiestates25.ui.features.search.MapSearchScreen
import com.dieti.dietiestates25.ui.features.search.SearchFilterScreen
import com.dieti.dietiestates25.ui.features.auth.WelcomeScreen
import com.dieti.dietiestates25.ui.features.search.SearchScreen
import com.dieti.dietiestates25.ui.features.appointments.AppointmentDetailScreen
import com.dieti.dietiestates25.ui.features.auth.LoginScreen
import com.dieti.dietiestates25.ui.features.property.EditPropertyScreen
import com.dieti.dietiestates25.ui.features.search.FullScreenMapScreen
import com.dieti.dietiestates25.ui.features.search.SearchTypeSelectionScreen
import com.dieti.dietiestates25.ui.features.property.YourPropertyScreen
import com.dieti.dietiestates25.ui.features.manager.RequestsScreen
import com.dieti.dietiestates25.ui.features.auth.RegisterScreen

@Composable
fun Navigation(
    startDestination: String = Screen.LoginScreen.route
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable("welcome_intro_screen") {
            WelcomeScreen(navController = navController)
        }

        composable("welcome_view/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            WelcomeScreen(navController = navController, idUtente = userId)
        }

        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(navController = navController)
        }

        composable(
            route = Screen.HomeScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            HomeScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        composable(
            route = Screen.SearchScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            SearchScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        composable(
            route = Screen.PropertySellScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            PropertySellScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        composable(
            route = Screen.ManagerScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            ManagerScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        composable(
            route = Screen.SearchFilterScreen.route + "/{idUtente}/{comune}/{ricercaQueryText}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" },
                navArgument("comune") { type = NavType.StringType; defaultValue = "napoli" },
                navArgument("ricercaQueryText") { type = NavType.StringType; defaultValue = "ricerca" }
            )
        ) { entry ->
            SearchFilterScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                comune = entry.arguments?.getString("comune") ?: "napoli",
                ricercaQueryText = entry.arguments?.getString("ricercaQueryText") ?: "ricerca",
                onNavigateBack = { navController.popBackStack() },
                onApplyFilters = { /* Logica filtri gestita internamente */ }
            )
        }

        // --- FIX FILTER PASSING HERE ---
        composable(
            route = Screen.ApartmentListingScreen.route + "/{idUtente}/{comune}/{ricerca}" +
                    "?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}" +
                    "&minSurface={minSurface}&maxSurface={maxSurface}&minRooms={minRooms}" +
                    "&maxRooms={maxRooms}&bathrooms={bathrooms}&condition={condition}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType },
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                navArgument("purchaseType") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("minPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("minSurface") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxSurface") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("minRooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("maxRooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("bathrooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("condition") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { entry ->
            // Ricostruiamo l'oggetto FilterModel dagli argomenti per passarlo alla Screen
            val purchaseType = entry.arguments?.getString("purchaseType")
            val minPrice = entry.arguments?.getFloat("minPrice")?.takeIf { it != -1f }
            val maxPrice = entry.arguments?.getFloat("maxPrice")?.takeIf { it != -1f }
            val minSurface = entry.arguments?.getFloat("minSurface")?.takeIf { it != -1f }
            val maxSurface = entry.arguments?.getFloat("maxSurface")?.takeIf { it != -1f }
            val minRooms = entry.arguments?.getInt("minRooms")?.takeIf { it != -1 }
            val maxRooms = entry.arguments?.getInt("maxRooms")?.takeIf { it != -1 }
            val bathrooms = entry.arguments?.getInt("bathrooms")?.takeIf { it != -1 }
            val condition = entry.arguments?.getString("condition")

            val filters = if (purchaseType != null || minPrice != null || maxPrice != null) {
                FilterModel(
                    purchaseType = purchaseType,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    minSurface = minSurface,
                    maxSurface = maxSurface,
                    minRooms = minRooms,
                    maxRooms = maxRooms,
                    bathrooms = bathrooms,
                    condition = condition
                )
            } else null

            ApartmentListingScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                comune = entry.arguments?.getString("comune") ?: "Napoli",
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca",
                filters = filters // Passiamo i filtri ricostruiti
            )
        }

        composable(
            route = Screen.PropertyScreen.route + "{idProperty}",
            arguments = listOf(
                navArgument("idProperty") { type = NavType.StringType }
            )
        ) { entry ->
            PropertyScreen(navController = navController, idProperty = entry.arguments?.getString("idProperty"))
        }

        composable(route = Screen.PriceProposalScreen.route){ PriceProposalScreen(navController = navController) }

        composable(route = Screen.AppointmentBookingScreen.route){ AppointmentBookingScreen(
            navController = navController,
            idUtente = "",
            idImmobile = "",
        ) }

        composable(route = Screen.NotificationScreen.route){ NotificationScreen(navController = navController) }

        composable(
            route = Screen.ProfileScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) {
            ProfileScreen(navController = navController)
        }

        composable(
            route = Screen.MapSearchScreen.route + "/{idUtente}/{comune}/{ricerca}" +
                    "?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}" +
                    "&minSurface={minSurface}&maxSurface={maxSurface}&minRooms={minRooms}" +
                    "&maxRooms={maxRooms}&bathrooms={bathrooms}&condition={condition}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType },
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                navArgument("purchaseType") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("minPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("minSurface") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxSurface") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("minRooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("maxRooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("bathrooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("condition") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { entry ->
            // Ricostruzione filtri anche per la mappa
            val purchaseType = entry.arguments?.getString("purchaseType")
            val minPrice = entry.arguments?.getFloat("minPrice")?.takeIf { it != -1f }
            val maxPrice = entry.arguments?.getFloat("maxPrice")?.takeIf { it != -1f }
            // ... (gli altri parametri mappa li gestisci già nel ViewModel se necessario)

            // Qui passo null o un oggetto FilterModel semplificato se serve
            val filters = if (purchaseType != null) FilterModel(purchaseType = purchaseType, minPrice = minPrice, maxPrice = maxPrice) else null

            MapSearchScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                comune = entry.arguments?.getString("comune") ?: "Napoli",
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca",
                // filters = filters (se MapSearchScreen li accetta nel costruttore)
            )
        }

        composable(
            route = Screen.SearchTypeSelectionScreen.route + "/{idUtente}/{comune}/{ricerca}" +
                    "?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}" +
                    "&minSurface={minSurface}&maxSurface={maxSurface}&minRooms={minRooms}" +
                    "&maxRooms={maxRooms}&bathrooms={bathrooms}&condition={condition}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType },
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                navArgument("purchaseType") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("minPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("minSurface") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxSurface") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("minRooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("maxRooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("bathrooms") { type = NavType.IntType; defaultValue = -1 },
                navArgument("condition") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { entry ->
            // Anche qui ricostruzione filtri per passarli avanti se necessario
            val purchaseType = entry.arguments?.getString("purchaseType")
            val minPrice = entry.arguments?.getFloat("minPrice")?.takeIf { it != -1f }
            val maxPrice = entry.arguments?.getFloat("maxPrice")?.takeIf { it != -1f }
            val filters = if (purchaseType != null) FilterModel(purchaseType = purchaseType, minPrice = minPrice, maxPrice = maxPrice) else null

            SearchTypeSelectionScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                comune = entry.arguments?.getString("comune") ?: "Napoli",
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca",
                filters = filters
            )
        }

        composable(
            route = Screen.FullScreenMapScreen.route + "/{lat}" + "/{lng}" + "/{zoom}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType },
                navArgument("zoom") { type = NavType.StringType }
            ),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(700)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(700)) },
            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(700)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(700)) }
        ) { navBackStackEntry ->
            val latitudeString = navBackStackEntry.arguments?.getString("lat")
            val longitudeString = navBackStackEntry.arguments?.getString("lng")
            val zoomString = navBackStackEntry.arguments?.getString("zoom")
            if (latitudeString != null && longitudeString != null && zoomString != null) {
                val latitude = latitudeString.toDoubleOrNull()
                val longitude = longitudeString.toDoubleOrNull()
                val zoom = zoomString.toFloatOrNull()
                if (latitude != null && longitude != null && zoom != null) {
                    FullScreenMapScreen(navController = navController, latitude = latitude, longitude = longitude, initialZoom = zoom)
                } else { navController.popBackStack() }
            } else { navController.popBackStack() }
        }

        composable(route = Screen.AppointmentDetailScreen.route){ AppointmentDetailScreen(navController = navController, appointmentId = "") }
        composable(route = Screen.YourPropertyScreen.route) { YourPropertyScreen(navController = navController, idUtente = "") }
        composable(route = Screen.EditPropertyScreen.route) { EditPropertyScreen(navController = navController) }
        composable(route = Screen.RequestsScreen.route + "/{idUtente}") { RequestsScreen(navController = navController) }
    }
}