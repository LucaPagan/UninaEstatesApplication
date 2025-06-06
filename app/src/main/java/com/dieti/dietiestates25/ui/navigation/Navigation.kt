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
import com.dieti.dietiestates25.ui.model.NotificationsViewModel
import com.dieti.dietiestates25.ui.navigation.Screen.EditPropertyScreen
import com.dieti.dietiestates25.ui.screen.ApartmentListingScreen
import com.dieti.dietiestates25.ui.screen.HomeScreen
// ... (altri import delle tue schermate)
import com.dieti.dietiestates25.ui.screen.SearchFilterScreen
import com.dieti.dietiestates25.ui.screen.WelcomeScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.SearchScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.PropertySellScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.PropertyScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.PriceProposalScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.AppointmentBookingScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.AppointmentDetailScreen
import com.dieti.dietiestates25.ui.screen.EditPropertyScreen
import com.dieti.dietiestates25.ui.screen.FullScreenMapScreen
import com.dieti.dietiestates25.ui.screen.MapSearchScreen
import com.dieti.dietiestates25.ui.screen.NotificationScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.NotificationDetailScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.ProfileScreen // Import mancante
import com.dieti.dietiestates25.ui.screen.SearchTypeSelectionScreen
import com.dieti.dietiestates25.ui.screen.YourPropertyScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {

        // WelcomeScreen (invariato)
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
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

        composable(
            // La rotta di SearchFilterScreen usa i nomi dei path parameters come definiti qui
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
                    // Utilizza la nuova funzione helper specifica con nomi di parametri chiari
                    val destinationRoute = Screen.SearchTypeSelectionScreen.buildRoute(
                        idUtentePath = idUtenteArg,       // Corrisponde a idUtentePath in buildRoute
                        comunePath = comuneArg,          // Corrisponde a comunePath in buildRoute
                        ricercaPath = ricercaQueryTextArg, // Corrisponde a ricercaPath in buildRoute
                        filters = filterModel
                    )
                }
            )
        }

        composable(
            // La rotta di ApartmentListingScreen DEVE corrispondere a come viene costruita
            // e i nomi dei path parameters qui ({idUtente}, {comune}, {ricerca})
            // devono corrispondere a quelli usati in buildRoute per i path.
            route = Screen.ApartmentListingScreen.route + "/{idUtente}/{comune}/{ricerca}" +
                    "?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}" +
                    "&minSurface={minSurface}&maxSurface={maxSurface}&minRooms={minRooms}" +
                    "&maxRooms={maxRooms}&bathrooms={bathrooms}&condition={condition}",
            arguments = listOf(
                // Path arguments (DEVONO corrispondere ai nomi nella stringa route qui sopra)
                navArgument("idUtente") { type = NavType.StringType }, // Rimosso defaultValue se sempre fornito
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                // Query arguments (opzionali)
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
                idUtente = entry.arguments?.getString("idUtente") ?: "utente", // Fallback
                comune = entry.arguments?.getString("comune") ?: "Napoli",     // Fallback
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca"   // Fallback
                // ApartmentListingScreen leggerà gli altri filtri da entry.arguments al suo interno
            )
        }

        // PropertyScreen (verifica se la rotta definita qui corrisponde all'helper in Screen.kt)
        // Se Screen.PropertyScreen.withId("id") produce "property_screen/id", allora:

        //Se PropertyScreen non prende argomenti nel path, allora era corretto:
        composable(route = Screen.PropertyScreen.route) {
             PropertyScreen(navController = navController)
        }


        composable(route = Screen.PriceProposalScreen.route){ PriceProposalScreen(navController = navController) }

        composable(route = Screen.AppointmentBookingScreen.route){ AppointmentBookingScreen(navController = navController) }

        composable(route = Screen.NotificationScreen.route){ NotificationScreen(navController = navController) }

        composable(
            route = Screen.NotificationDetailScreen.route, // Usa la route base "notification_detail_screen/{notificationId}"
            arguments = listOf(navArgument("notificationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getInt("notificationId")
            val notificationsViewModel: NotificationsViewModel = viewModel() // O come ottieni il ViewModel principale

            NotificationDetailScreen(
                navController = navController,
                notificationId = notificationId,
                onToggleMasterFavorite = { id -> notificationsViewModel.toggleFavorite(id) }
            )
        }

        composable(route = Screen.ProfileScreen.route){ ProfileScreen(navController = navController) }

        composable(
            // La rotta di MapSearchScreen DEVE corrispondere a come viene costruita
            // e i nomi dei path parameters qui ({idUtente}, {comune}, {ricerca})
            // devono corrispondere a quelli usati in buildRoute per i path.
            route = Screen.MapSearchScreen.route + "/{idUtente}/{comune}/{ricerca}" +
                    "?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}" +
                    "&minSurface={minSurface}&maxSurface={maxSurface}&minRooms={minRooms}" +
                    "&maxRooms={maxRooms}&bathrooms={bathrooms}&condition={condition}",
            arguments = listOf(
                // Path arguments (DEVONO corrispondere ai nomi nella stringa route qui sopra)
                navArgument("idUtente") { type = NavType.StringType }, // Rimosso defaultValue se sempre fornito
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                // Query arguments (opzionali)
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
                idUtente = entry.arguments?.getString("idUtente") ?: "utente", // Fallback
                comune = entry.arguments?.getString("comune") ?: "Napoli",     // Fallback
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca"   // Fallback
                // MapSearchScreen leggerà gli altri filtri da entry.arguments al suo interno
            )
        }

        composable(
            // La rotta di SearchTypeSelectionScreen DEVE corrispondere a come viene costruita
            // e i nomi dei path parameters qui ({idUtente}, {comune}, {ricerca})
            // devono corrispondere a quelli usati in buildRoute per i path.
            route = Screen.SearchTypeSelectionScreen.route + "/{idUtente}/{comune}/{ricerca}" +
                    "?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}" +
                    "&minSurface={minSurface}&maxSurface={maxSurface}&minRooms={minRooms}" +
                    "&maxRooms={maxRooms}&bathrooms={bathrooms}&condition={condition}",
            arguments = listOf(
                // Path arguments (DEVONO corrispondere ai nomi nella stringa route qui sopra)
                navArgument("idUtente") { type = NavType.StringType }, // Rimosso defaultValue se sempre fornito
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                // Query arguments (opzionali)
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
                idUtente = entry.arguments?.getString("idUtente") ?: "utente", // Fallback
                comune = entry.arguments?.getString("comune") ?: "Napoli",     // Fallback
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca"   // Fallback
                // SearchTypeSelectionScreen leggerà gli altri filtri da entry.arguments al suo interno
            )
        }

        composable(
            route = Screen.FullScreenMapScreen.route + "/{lat}" + "/{lng}" + "/{zoom}", // Es: "fullscreen_map_screen/{lat}/{lng}/{zoom}"
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
            popEnterTransition = { // Animazione quando si torna indietro ALLA SCHERMATA PRECEDENTE (non a questa)
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(700))
            },
            popExitTransition = { // Animazione quando si esce da QUESTA schermata tornando indietro
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
    }
}



