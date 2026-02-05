package com.dieti.dietiestates25

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.ui.navigation.Navigation
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.utils.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val userPrefs = UserPreferences(applicationContext)

        setContent {
            DietiEstatesTheme {
                val context = LocalContext.current

                // Stato per la destinazione iniziale
                var startDestination by remember { mutableStateOf<String?>(null) }

                // Osserviamo se è la prima apertura
                val isFirstRunState = userPrefs.isFirstRun.collectAsState(initial = null)

                // Recuperiamo la sessione corrente
                val currentSessionId = SessionManager.getUserId(context)

                LaunchedEffect(isFirstRunState.value, currentSessionId) {
                    val isFirstRun = isFirstRunState.value

                    Log.d("MAIN_DEBUG", "--------------------------------------")
                    Log.d("MAIN_DEBUG", "Check Avvio App:")
                    Log.d("MAIN_DEBUG", "1. Session ID: '$currentSessionId'")
                    Log.d("MAIN_DEBUG", "2. Is First Run: $isFirstRun")

                    if (isFirstRun != null) {
                        // CASO 1: C'è una sessione attiva (Utente o Admin)
                        if (!currentSessionId.isNullOrEmpty()) {

                            // *** FIX CRITICO: Controllo se è l'Admin ***
                            if (currentSessionId == "ADMIN_SESSION") {
                                Log.d("MAIN_DEBUG", "DECISIONE: Admin rilevato -> Dashboard Admin")
                                startDestination = Screen.AdminDashboardScreen.route
                            } else {
                                Log.d("MAIN_DEBUG", "DECISIONE: Utente rilevato -> Home")
                                startDestination = Screen.HomeScreen.withIdUtente(currentSessionId)
                            }

                        } else {
                            // CASO 2: Nessuna sessione attiva
                            if (isFirstRun) {
                                Log.d("MAIN_DEBUG", "DECISIONE: Prima volta -> Intro (WelcomeScreen)")
                                startDestination = "welcome_intro_screen"
                            } else {
                                Log.d("MAIN_DEBUG", "DECISIONE: Non loggato -> Login")
                                startDestination = Screen.LoginScreen.route
                            }
                        }
                    }
                }

                // Mostra un caricamento finché non decidiamo la destinazione
                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Avvia la navigazione con la destinazione calcolata corretta
                    Navigation(startDestination = startDestination!!)
                }
            }
        }
    }
}