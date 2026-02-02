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

                // Stato per la destinazione
                var startDestination by remember { mutableStateOf<String?>(null) }

                // Raccogliamo i dati
                val isFirstRunState = userPrefs.isFirstRun.collectAsState(initial = null)

                // NOTA: Leggiamo la sessione direttamente qui per assicurarci che sia aggiornata
                // Non usiamo 'remember' statico per poter loggare ogni cambiamento se necessario
                val currentSessionId = SessionManager.getUserId(context)

                LaunchedEffect(isFirstRunState.value, currentSessionId) {
                    val isFirstRun = isFirstRunState.value

                    // Log per il Debug: Controlla il Logcat con tag "MAIN_DEBUG"
                    Log.d("MAIN_DEBUG", "--------------------------------------")
                    Log.d("MAIN_DEBUG", "Controllo Avvio App:")
                    Log.d("MAIN_DEBUG", "1. Session ID: '$currentSessionId'")
                    Log.d("MAIN_DEBUG", "2. Is First Run: $isFirstRun")

                    if (isFirstRun != null) {
                        if (!currentSessionId.isNullOrEmpty()) {
                            // CASO 1: Utente Loggato -> HOME
                            Log.d("MAIN_DEBUG", "DECISIONE: Vado alla HOME")
                            startDestination = Screen.HomeScreen.route + "/$currentSessionId"
                        } else {
                            // CASO 2: Nessuna Sessione.
                            // Se isFirstRun è true, andrebbe all'Intro.
                            // MA se continui ad avere il problema del loop, forza il Login qui cambiando la logica.
                            if (isFirstRun) {
                                Log.d("MAIN_DEBUG", "DECISIONE: Vado alla INTRO")
                                startDestination = "welcome_intro_screen"
                            } else {
                                Log.d("MAIN_DEBUG", "DECISIONE: Vado al LOGIN")
                                startDestination = Screen.LoginScreen.route
                            }
                        }
                    }
                }

                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Passiamo la destinazione calcolata alla Navigation
                    Navigation(startDestination = startDestination!!)
                }
            }
        }
    }
}