package com.dieti.dietiestates25.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dieti.dietiestates25.data.model.FilterModel
import com.dieti.dietiestates25.ui.features.admin.*
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

        // --- AUTH ---
        composable(Screen.LoginScreen.route) { LoginScreen(navController) }
        composable(Screen.RegisterScreen.route) { RegisterScreen(navController) }
        composable("welcome_intro_screen") { WelcomeScreen(navController) }
        composable("welcome_view/{userId}") { backStackEntry ->
            WelcomeScreen(navController, idUtente = backStackEntry.arguments?.getString("userId"))
        }

        // --- ADMIN FLOW ---
        composable(Screen.AdminDashboardScreen.route) {
            AdminDashboardScreen(
                navController = navController,
                onNavigateToCreateAdmin = { navController.navigate(Screen.AdminCreateAdminScreen.route) },
                onNavigateToCreateAgent = { navController.navigate(Screen.AdminCreateAgentScreen.route) },
                onNavigateToCreateAgency = { navController.navigate(Screen.AdminCreateAgencyScreen.route) }, // AGGIUNTO
                onNavigateToChangePassword = { navController.navigate(Screen.AdminChangePasswordScreen.route) },
                onLogout = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AdminCreateAdminScreen.route) { AdminCreateAdminScreen(navController) }
        composable(Screen.AdminCreateAgentScreen.route) { AdminCreateAgentScreen(navController) }
        composable(Screen.AdminChangePasswordScreen.route) { AdminChangePasswordScreen(navController) }
        composable(Screen.AdminCreateAgencyScreen.route) { AdminCreateAgencyScreen(navController) } // AGGIUNTO

        // --- HOME & LISTINGS ---
        composable(
            route = "${Screen.HomeScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            HomeScreen(navController, idUtente = entry.arguments?.getString("idUtente") ?: "")
        }

        composable(
            route = "${Screen.ProfileScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            ProfileScreen(navController, idUtente = entry.arguments?.getString("idUtente"))
        }

        composable(Screen.NotificationScreen.route) { NotificationScreen(navController) }

        composable(
            route = "${Screen.SearchScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            SearchScreen(navController, idUtente = entry.arguments?.getString("idUtente") ?: "")
        }

        composable(
            route = "${Screen.SearchFilterScreen.route}/{idUtente}/{comune}/{ricercaQueryText}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType },
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricercaQueryText") { type = NavType.StringType }
            )
        ) { entry ->
            SearchFilterScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "",
                comune = entry.arguments?.getString("comune") ?: "",
                ricercaQueryText = entry.arguments?.getString("ricercaQueryText") ?: "",
                onNavigateBack = { navController.popBackStack() },
                onApplyFilters = { }
            )
        }

        composable(
            route = "${Screen.SearchTypeSelectionScreen.route}/{idUtente}/{comune}/{ricerca}?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType },
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                navArgument("purchaseType") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("minPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxPrice") { type = NavType.FloatType; defaultValue = -1f }
            )
        ) { entry ->
            SearchTypeSelectionScreen(
                navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "",
                comune = entry.arguments?.getString("comune") ?: "",
                ricerca = entry.arguments?.getString("ricerca") ?: "",
                filters = null
            )
        }

        composable(
            route = "${Screen.ApartmentListingScreen.route}/{idUtente}/{comune}/{ricerca}?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType },
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                navArgument("purchaseType") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("minPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxPrice") { type = NavType.FloatType; defaultValue = -1f }
            )
        ) { entry ->
            ApartmentListingScreen(
                navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "",
                comune = entry.arguments?.getString("comune") ?: "",
                ricerca = entry.arguments?.getString("ricerca") ?: "",
                filters = null
            )
        }

        composable(
            route = "${Screen.MapSearchScreen.route}/{idUtente}/{comune}/{ricerca}?purchaseType={purchaseType}&minPrice={minPrice}&maxPrice={maxPrice}",
            arguments = listOf(
                navArgument("idUtente") { type = NavType.StringType },
                navArgument("comune") { type = NavType.StringType },
                navArgument("ricerca") { type = NavType.StringType },
                navArgument("purchaseType") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("minPrice") { type = NavType.FloatType; defaultValue = -1f },
                navArgument("maxPrice") { type = NavType.FloatType; defaultValue = -1f }
            )
        ) { entry ->
            MapSearchScreen(
                navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "",
                comune = entry.arguments?.getString("comune") ?: "",
                ricerca = entry.arguments?.getString("ricerca") ?: ""
            )
        }

        composable(
            route = "${Screen.PropertyScreen.route}/{idProperty}",
            arguments = listOf(navArgument("idProperty") { type = NavType.StringType })
        ) { entry ->
            PropertyScreen(
                navController = navController,
                idProperty = entry.arguments?.getString("idProperty")
            )
        }

        composable(
            route = "${Screen.AppointmentBookingScreen.route}/{idProperty}",
            arguments = listOf(navArgument("idProperty") { type = NavType.StringType })
        ) { entry ->
            AppointmentBookingScreen(
                navController = navController,
                idUtente = "",
                idImmobile = entry.arguments?.getString("idProperty") ?: ""
            )
        }

        composable(Screen.PriceProposalScreen.route) { PriceProposalScreen(navController) }

        composable(
            route = "${Screen.FullScreenMapScreen.route}/{lat}/{lng}/{zoom}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType },
                navArgument("zoom") { type = NavType.StringType }
            )
        ) { entry ->
            val lat = entry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lng = entry.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0
            val zoom = entry.arguments?.getString("zoom")?.toFloatOrNull() ?: 10f
            FullScreenMapScreen(navController, lat, lng, zoom)
        }

        composable(
            route = "${Screen.ManagerScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            ManagerScreen(navController, idUtente = entry.arguments?.getString("idUtente") ?: "")
        }

        composable(
            route = "${Screen.PropertySellScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            PropertySellScreen(navController, idUtente = entry.arguments?.getString("idUtente") ?: "")
        }

        composable(Screen.AppointmentDetailScreen.route) { AppointmentDetailScreen(navController, appointmentId = "") }
        composable(Screen.YourPropertyScreen.route) { YourPropertyScreen(navController, idUtente = "") }
        composable(Screen.EditPropertyScreen.route) { EditPropertyScreen(navController) }
        composable("${Screen.RequestsScreen.route}/{idUtente}") { RequestsScreen(navController) }
    }
}