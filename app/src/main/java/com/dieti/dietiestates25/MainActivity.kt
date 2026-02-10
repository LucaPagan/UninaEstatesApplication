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
import com.dieti.dietiestates25.data.remote.RetrofitClient
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
                val validSessionId = remember { SessionManager.validateAndRefreshSession(context) }

                LaunchedEffect(isFirstRunState.value, validSessionId) {
                    val isFirstRun = isFirstRunState.value

                    if (isFirstRun != null) {
                        // 1. Sessione Valida
                        if (!validSessionId.isNullOrEmpty()) {

                            // RECUPERO DATI: Token e EMAIL (non nome)
                            val savedToken = SessionManager.getAuthToken(context)
                            val savedEmail = SessionManager.getUserEmail(context) // FIX: Usa getUserEmail
                            val savedRole = SessionManager.getUserRole(context)

                            if (savedToken != null && savedEmail != null) {
                                // RIPRISTINO RETROFIT CLIENT
                                RetrofitClient.authToken = savedToken
                                RetrofitClient.loggedUserEmail = savedEmail // Imposta l'email corretta

                                Log.d("MainActivity", "Sessione ripristinata per $savedEmail ($savedRole)")

                                // Routing
                                startDestination = when (savedRole) {
                                    "ADMIN" -> Screen.AdminDashboardScreen.route
                                    "MANAGER" -> Screen.ManagerScreen.withIdUtente(validSessionId)
                                    else -> Screen.HomeScreen.withIdUtente(validSessionId)
                                }
                            } else {
                                // Dati incompleti -> Login
                                startDestination = Screen.LoginScreen.route
                            }

                        } else if (isFirstRun) {
                            // 2. Primo avvio -> Intro
                            startDestination = "welcome_intro_screen" // Assicurati che questa rotta esista nel tuo Navigation graph
                        } else {
                            // 3. Logout/Scaduto -> Login
                            startDestination = Screen.LoginScreen.route
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