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
                var startDestination by remember { mutableStateOf<String?>(null) }
                val isFirstRunState = userPrefs.isFirstRun.collectAsState(initial = null)

                // --- MODIFICA FONDAMENTALE ---
                // Invece di getUserId(), usiamo validateAndRefreshSession().
                // Se rememberMe era false, questa funzione restituirà NULL (e farà logout).
                // Se rememberMe era true ed è valido, restituirà l'ID e resetterà i 30 giorni.
                val validSessionId = remember { SessionManager.validateAndRefreshSession(context) }

                LaunchedEffect(isFirstRunState.value, validSessionId) {
                    val isFirstRun = isFirstRunState.value

                    Log.d("MAIN_DEBUG", "Check Avvio: FirstRun=$isFirstRun, Session=$validSessionId")

                    if (isFirstRun != null) {
                        startDestination = if (!validSessionId.isNullOrEmpty()) {
                            // Utente loggato e sessione valida -> Home
                            Screen.HomeScreen.withIdUtente(validSessionId)
                        } else if (isFirstRun) {
                            // Primo avvio assoluto -> Intro
                            "welcome_intro_screen"
                        } else {
                            // Sessione scaduta o logout -> Login
                            Screen.LoginScreen.route
                        }
                    }
                }

                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Navigation(startDestination = startDestination!!)
                }
            }
        }
    }
}