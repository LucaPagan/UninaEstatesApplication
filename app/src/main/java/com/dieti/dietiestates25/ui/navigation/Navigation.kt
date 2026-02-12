package com.dieti.dietiestates25.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dieti.dietiestates25.ui.features.admin.*
import com.dieti.dietiestates25.ui.features.auth.LoginScreen
import com.dieti.dietiestates25.ui.features.auth.RegisterScreen
import com.dieti.dietiestates25.ui.features.auth.WelcomeScreen
import com.dieti.dietiestates25.ui.features.home.HomeScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerNegotiationChatScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerOffersScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerPendingPropertyScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerRequestDetailScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerScreen
import com.dieti.dietiestates25.ui.features.manager.ManagerNotificationsScreen
import com.dieti.dietiestates25.ui.features.notification.NegotiationDetailScreen
import com.dieti.dietiestates25.ui.features.notification.NotificationDetailGenericScreen
import com.dieti.dietiestates25.ui.features.notification.NotificationScreen
import com.dieti.dietiestates25.ui.features.profile.ProfileScreen
import com.dieti.dietiestates25.ui.features.property.*
import com.dieti.dietiestates25.ui.features.search.*

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

        // --- ADMIN ---
        composable(Screen.AdminDashboardScreen.route) {
            AdminDashboardScreen(
                navController = navController,
                onNavigateToCreateAdmin = { navController.navigate(Screen.AdminCreateAdminScreen.route) },
                onNavigateToCreateAgent = { navController.navigate(Screen.AdminCreateAgentScreen.route) },
                onNavigateToCreateAgency = { navController.navigate(Screen.AdminCreateAgencyScreen.route) },
                onNavigateToChangePassword = { navController.navigate(Screen.AdminChangePasswordScreen.route) },
                onLogout = { navController.navigate(Screen.LoginScreen.route) { popUpTo(0) { inclusive = true } } }
            )
        }
        composable(Screen.AdminCreateAdminScreen.route) { AdminCreateAdminScreen(navController) }
        composable(Screen.AdminCreateAgentScreen.route) { AdminCreateAgentScreen(navController) }
        composable(Screen.AdminChangePasswordScreen.route) { AdminChangePasswordScreen(navController) }
        composable(Screen.AdminCreateAgencyScreen.route) { AdminCreateAgencyScreen(navController) }

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

        // --- MANAGER ROUTES ---

        // 1. Dashboard Manager
        composable(
            route = "${Screen.ManagerScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            ManagerScreen(navController, idUtente = entry.arguments?.getString("idUtente") ?: "")
        }

        // 2. Notifiche (Appartamenti/Richieste da approvare)
        composable(Screen.ManagerNotificationScreen.route) {
            // FIX: Ora usa il nome corretto al plurale per evitare conflitti
            ManagerNotificationsScreen(navController)
        }

        // 3. Preview per Accettare/Rifiutare un immobile pendente (chiamata dalla card Notifiche)
        composable(
            route = "${Screen.ManagerPendingPropertyScreen.route}/{immobileId}",
            arguments = listOf(navArgument("immobileId") { type = NavType.StringType })
        ) { entry ->
            val immobileId = entry.arguments?.getString("immobileId") ?: ""
            ManagerPendingPropertyScreen(navController, immobileId)
        }

        // 4. Proposte (Offerte economiche ricevute)
        composable(
            route = "${Screen.RequestsScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            ManagerOffersScreen(
                navController,
                // Assicurati che il ManagerOffersScreen accetti l'argomento se necessario,
                // altrimenti ignora il passaggio o modificalo in base alla tua implementazione
            )
        }

        // 5. Dettaglio Offerta/Trattativa (chiamata dalla card Proposte)
        composable(
            route = "${Screen.ManagerRequestDetailScreen.route}/{immobileId}",
            arguments = listOf(navArgument("immobileId") { type = NavType.StringType })
        ) { entry ->
            val immobileId = entry.arguments?.getString("immobileId") ?: ""
            ManagerRequestDetailScreen(navController, immobileId)
        }

        // --- SEARCH & PROPERTY ---
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
            route = "${Screen.PriceProposalScreen.route}/{immobileId}",
            arguments = listOf(navArgument("immobileId") { type = NavType.StringType })
        ) { backStackEntry ->
            val immobileId = backStackEntry.arguments?.getString("immobileId")
            PriceProposalScreen(navController = navController, immobileId = immobileId)
        }

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
            route = "${Screen.PropertySellScreen.route}/{idUtente}",
            arguments = listOf(navArgument("idUtente") { type = NavType.StringType })
        ) { entry ->
            PropertySellScreen(navController, idUtente = entry.arguments?.getString("idUtente") ?: "")
        }

        composable(route = Screen.YourPropertyScreen.route) { YourPropertyScreen(navController = navController, idUtente = "") }

        composable(
            route = Screen.EditPropertyScreen.route + "/{${Screen.EditPropertyScreen.argId}}",
            arguments = listOf(
                navArgument(Screen.EditPropertyScreen.argId) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val immobileId = backStackEntry.arguments?.getString(Screen.EditPropertyScreen.argId) ?: ""
            EditPropertyScreen(navController = navController, immobileId = immobileId)
        }

        composable(
            route = "negotiation_detail_screen/{offertaId}",
            arguments = listOf(navArgument("offertaId") { type = NavType.StringType })
        ) { entry ->
            val offertaId = entry.arguments?.getString("offertaId") ?: ""
            NegotiationDetailScreen(navController, offertaId)
        }

        // 7. Dettaglio Notifica Generica (Tab "Notifiche")
        // Passiamo titolo e corpo come argomenti semplici
        composable(
            route = "notification_detail_generic/{title}/{body}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("body") { type = NavType.StringType }
            )
        ) { entry ->
            val title = entry.arguments?.getString("title") ?: ""
            val body = entry.arguments?.getString("body") ?: ""
            NotificationDetailGenericScreen(navController, title, body)
        }

        // ... ALTRI COMPOSABLE ESISTENTI ...

        // 8. Chat Trattativa MANAGER (chiamata dalla lista Proposte)
        composable(
            route = "manager_negotiation_chat/{offertaId}",
            arguments = listOf(navArgument("offertaId") { type = NavType.StringType })
        ) { entry ->
            val offertaId = entry.arguments?.getString("offertaId") ?: ""
            ManagerNegotiationChatScreen(navController, offertaId)
        }
    }
}