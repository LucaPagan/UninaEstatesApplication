package com.dieti.dietiestates25.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.screen.HomeScreen
import com.dieti.dietiestates25.ui.screen.WelcomeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
        }
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

    }
}