package com.dieti.dietiestates25.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.screen.HomeScreen
import com.dieti.dietiestates25.ui.screen.PriceProposalScreen
import com.dieti.dietiestates25.ui.screen.SearchFilterScreen
import com.dieti.dietiestates25.ui.screen.SearchScreen
import com.dieti.dietiestates25.ui.screen.WelcomeScreen
import com.dieti.dietiestates25.ui.screen.PropertyScreen
import com.dieti.dietiestates25.ui.screen.AppointmentBookingScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {

        // WelcomeScreen
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
        }

        // HomeScreen
        composable(
            route = Screen.HomeScreen.route + "/{idUtente}",
            arguments = listOf(
                navArgument("idUtente") {
                    type = NavType.StringType
                    defaultValue = "utente"
                }
            )
        ) { entry ->
            HomeScreen(
                navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente"
            )
        }

        // SearchScreen
        composable(
            route = Screen.SearchScreen.route + "/{idUtente}",
            arguments = listOf(
                navArgument("idUtente") {
                    type = NavType.StringType
                    defaultValue = "utente"
                }
            )
        ) { entry ->
            SearchScreen(
                navController = navController, idUtente = entry.arguments?.getString("idUtente") ?: "utente"
            )
        }

        composable(
            route = Screen.SearchFilterScreen.route + "/{idUtente}/{ricerca}",
            arguments = listOf(
                navArgument("idUtente") {
                    type = NavType.StringType
                    defaultValue = "utente"
                },
                navArgument("ricerca") {
                    type = NavType.StringType
                    defaultValue = "ricerca"
                }
            )
        ) { entry ->
            SearchFilterScreen(
                navController = navController,
                idUtente = entry.arguments?.getString("idUtente") ?: "utente",
                ricerca = entry.arguments?.getString("ricerca") ?: "ricerca"
            )
        }

        // PropertyScreen
        composable(route = Screen.PropertyScreen.route) {
            PropertyScreen(navController = navController)
        }

        composable(route = Screen.PriceProposalScreen.route){
            PriceProposalScreen(navController = navController)
        }

        composable(route = Screen.AppointmentBookingScreen.route){
            AppointmentBookingScreen(navController = navController)
        }
    }
}