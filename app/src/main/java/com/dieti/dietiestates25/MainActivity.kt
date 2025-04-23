package com.dieti.dietiestates25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.data.PreferenceManager
import com.dieti.dietiestates25.ui.screen.HomeScreen
import com.dieti.dietiestates25.ui.screen.WelcomeScreen
import com.dieti.dietiestates25.ui.theme.DietiEstates25Theme

class MainActivity : ComponentActivity() {
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)

        setContent {
            DietiEstates25Theme {
                AppNavigation(
                    preferenceManager = preferenceManager,
                    isFirstLaunch = preferenceManager.isFirstTimeLaunch()
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    preferenceManager: PreferenceManager,
    isFirstLaunch: Boolean,
    navController: NavHostController = rememberNavController()
) {
    // Determina lo schermo iniziale in base al fatto che sia il primo avvio o meno
    val startDestination = remember { if (isFirstLaunch) "welcome" else "home" }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToHome = {
                    // Imposta che non è più il primo avvio
                    preferenceManager.setFirstTimeLaunchComplete()
                    // Naviga alla HomeScreen e pulisce il backstack
                    navController.navigate("home") {
                        // Rimuove tutte le destinazioni precedenti dal backstack
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}