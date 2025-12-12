package com.dieti.dietiestates25.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dieti.dietiestates25.ui.features.property.ApartmentListingScreen
import com.dieti.dietiestates25.ui.features.home.HomeScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerScreen
import com.dieti.dietiestates25.ui.features.notification.NotificationDetailScreen
import com.dieti.dietiestates25.ui.features.notification.NotificationScreen
import com.dieti.dietiestates25.ui.features.profile.ProfileScreen
import com.dieti.dietiestates25.ui.features.property.PropertySellScreen
import com.dieti.dietiestates25.ui.features.appointments.AppointmentBookingScreen
import com.dieti.dietiestates25.ui.features.property.PriceProposalScreen
import com.dieti.dietiestates25.ui.features.property.PropertyScreen
import com.dieti.dietiestates25.ui.features.search.MapSearchScreen
import com.dieti.dietiestates25.ui.features.search.SearchFilterScreen
import com.dieti.dietiestates25.ui.features.auth.WelcomeScreen // Import mancante
import com.dieti.dietiestates25.ui.features.search.SearchScreen // Import mancante
import com.dieti.dietiestates25.ui.features.appointments.AppointmentDetailScreen
import com.dieti.dietiestates25.ui.features.property.EditPropertyScreen
import com.dieti.dietiestates25.ui.features.search.FullScreenMapScreen
import com.dieti.dietiestates25.ui.features.search.SearchTypeSelectionScreen
import com.dieti.dietiestates25.ui.features.property.YourPropertyScreen
import com.dieti.dietiestates25.ui.features.manager.RequestsScreen
import com.dieti.dietiestates25.ui.features.auth.LoginScreenPreviewOnly

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {

        // WelcomeScreen (invariato)
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
        }

        composable(route = Screen.LoginScreen.route) {
            LoginScreenPreviewOnly()
        }

        // HomeScreen (invariato, ma potrebbe usare Screen.HomeScreen.withIdUtente)
        composable(
            route = Screen.HomeScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            HomeScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        // SearchScreen (invariato, ma potrebbe usare Screen.SearchScreen.withIdUtente)
        composable(
            route = Screen.SearchScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            SearchScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        // PropertySellScreen (invariato, ma potrebbe usare Screen.PropertySellScreen.withIdUtente)
        composable(
            route = Screen.PropertySellScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            PropertySellScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        // ManagerScreen - NUOVA ROTTA
        composable(
            route = Screen.ManagerScreen.route + "/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" })
        ) { entry ->
            ManagerScreen(navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente")
        }

        // La rotta di SearchFilterScreen usa i nomi dei path parameters come definiti qui
        composable(
            route = Screen.SearchFilterScreen.route + "/{idUtente}/{comune}/{ricercaQueryText}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType; defaultValue = "utente" },
                navArgument("comune") { type = NavType.StringType; defaultValue = "napoli" },
                navArgument("ricercaQueryText") { type = NavType.StringType; defaultValue = "ricerca" }
            )
        ) { entry ->
            val idUtenteArg = entry.arguments?.getString("idUtente") ?: "utente"
            val comuneArg = entry.arguments?.getString("comune") ?: "napoli"
            val ricercaQueryTextArg = entry.arguments?.getString("ricercaQueryText") ?: "ricerca"

            SearchFilterScreen(
                navController = navController,
                idUtente = idUtenteArg,
                comune = comuneArg,
                ricercaQueryText = ricercaQueryTextArg,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onApplyFilters = { filterModel ->
                    val destinationRoute = Screen.SearchTypeSelectionScreen.buildRoute(
                        idUtentePath = idUtenteArg,
                        comunePath = comuneArg,
                        ricercaPath = ricercaQueryTextArg,
                        filters = filterModel
                    )
                }
            )
        }

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
            ApartmentListingScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                comune = entry.arguments?.getString("comune") ?: "Napoli",
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca"
            )
        }

        // PropertyScreen (verifica se la rotta definita qui corrisponde all'helper in Screen.kt)
        // Se Screen.PropertyScreen.withId("id") produce "property_screen/id", allora:

        //Se PropertyScreen non prende argomenti nel path, allora era corretto:
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
            viewModel = viewModel()
        ) }

        composable(route = Screen.NotificationScreen.route){ NotificationScreen(navController = navController) }

        composable(
            route = Screen.NotificationDetailScreen.route,
            arguments = listOf(navArgument("notificationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId")
            val notificationsViewModel: NotificationsViewModel = viewModel()

            NotificationDetailScreen(
                navController = navController,
                notificationId = notificationId,
                onToggleMasterFavorite = { id -> notificationsViewModel.toggleFavorite(id) }
            )
        }

        composable(route = Screen.ProfileScreen.route){ ProfileScreen(navController = navController) }

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
            MapSearchScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                comune = entry.arguments?.getString("comune") ?: "Napoli",
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca"
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
            SearchTypeSelectionScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                comune = entry.arguments?.getString("comune") ?: "Napoli",
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca"
            )
        }

        composable(
            route = Screen.FullScreenMapScreen.route + "/{lat}" + "/{lng}" + "/{zoom}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType },
                navArgument("zoom") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(700))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(700))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(700))
            }
        ) { navBackStackEntry ->
            val latitudeString = navBackStackEntry.arguments?.getString("lat")
            val longitudeString = navBackStackEntry.arguments?.getString("lng")
            val zoomString = navBackStackEntry.arguments?.getString("zoom")

            if (latitudeString != null && longitudeString != null && zoomString != null) {
                val latitude = latitudeString.toDoubleOrNull()
                val longitude = longitudeString.toDoubleOrNull()
                val zoom = zoomString.toFloatOrNull()

                if (latitude != null && longitude != null && zoom != null) {
                    FullScreenMapScreen(
                        navController = navController,
                        latitude = latitude,
                        longitude = longitude,
                        initialZoom = zoom
                    )
                } else {
                    navController.popBackStack()
                }
            } else {
                navController.popBackStack()
            }
        }

        composable(route = Screen.AppointmentDetailScreen.route){ AppointmentDetailScreen(
            navController = navController,
            appointmentId = ""
        ) }

        composable(route = Screen.YourPropertyScreen.route) {
            YourPropertyScreen(
                navController = navController,
                idUtente = ""
            )
        }

        composable(route = Screen.EditPropertyScreen.route) {
            EditPropertyScreen(
                navController = navController
            )
        }

        composable(route = Screen.RequestsScreen.route + "/{idUtente}") {
            RequestsScreen(
                navController = navController
            )
        }
    }
}

