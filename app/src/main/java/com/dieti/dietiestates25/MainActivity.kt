package com.dieti.dietiestates25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.data.PreferenceManager
import com.dieti.dietiestates25.ui.screen.HomeScreen
import com.dieti.dietiestates25.ui.screen.WelcomeScreen
import com.dieti.dietiestates25.ui.theme.DietiEstates25Theme
import com.dieti.dietiestates25.viewmodel.SharedViewModel
import java.util.UUID

class MainActivity : ComponentActivity() {
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inizializza il PreferenceManager
        preferenceManager = PreferenceManager(this)

        setContent {
            DietiEstates25Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inizializza il ViewModel
                    val viewModel: SharedViewModel = viewModel()

                    // Configurazione della navigazione
                    val navController = rememberNavController()

                    // Determina lo schermo iniziale
                    val isFirstLaunch = preferenceManager.isFirstTimeLaunch()
                    val startDestination = if (isFirstLaunch) "welcome" else "home"

                    // Ottieni o genera il token utente
                    val userToken = preferenceManager.getUserToken() ?: run {
                        val newToken = UUID.randomUUID().toString()
                        preferenceManager.saveUserToken(newToken)
                        newToken
                    }

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("welcome") {
                            WelcomeScreen(
                                onNavigateToHome = {
                                    // Imposta che non è più il primo avvio
                                    preferenceManager.setFirstTimeLaunchComplete()
                                    // Naviga alla home
                                    navController.navigate("home") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            // Passa solo il token e il ViewModel
                            HomeScreen(
                                userToken = userToken,
                                viewModel = viewModel,
                                onSearchClick = { preferenceManager.saveSearch(it) },
                                onPropertyClick = { /* gestione essenziale */ },
                                onProfileClick = { /* gestione essenziale */ },
                                onNotificationsClick = { /* gestione essenziale */ },
                                onPostAdClick = { /* gestione essenziale */ },
                                onRecentSearchesClick = { /* gestione essenziale */ }
                            )
                        }
                    }
                }
            }
        }
    }
}