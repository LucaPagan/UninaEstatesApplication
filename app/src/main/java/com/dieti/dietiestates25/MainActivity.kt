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
import com.dieti.dietiestates25.data.remote.RetrofitClient // Importante per ripristinare il token
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

                // Raccoglie lo stato di "Primo Avvio" dalle preferenze
                val isFirstRunState = userPrefs.isFirstRun.collectAsState(initial = null)

                // Validazione Sessione: controlla scadenza e restituisce ID se valido
                val validSessionId = remember { SessionManager.validateAndRefreshSession(context) }

                LaunchedEffect(isFirstRunState.value, validSessionId) {
                    val isFirstRun = isFirstRunState.value

                    Log.d("MAIN_DEBUG", "Check Avvio: FirstRun=$isFirstRun, Session=$validSessionId")

                    // Attendiamo che isFirstRun sia caricato (non null)
                    if (isFirstRun != null) {

                        // 1. Se la sessione è valida (ID recuperato e non scaduta)
                        if (!validSessionId.isNullOrEmpty()) {

                            // RECUPERO DATI CRITICI: Token e Ruolo
                            val savedToken = SessionManager.getAuthToken(context)
                            val savedRole = SessionManager.getUserRole(context) // Default "UTENTE"

                            if (savedToken != null) {
                                // --- PUNTO FONDAMENTALE ---
                                // Ripristiniamo il token nel Client HTTP.
                                // Senza questo, le chiamate API fallirebbero (es. assegnazione immobile).
                                RetrofitClient.authToken = savedToken
                                RetrofitClient.loggedUserEmail = SessionManager.getUserName(context) // O recupera email se salvata

                                // ROUTING BASATO SUL RUOLO
                                startDestination = when (savedRole) {
                                    "ADMIN" -> Screen.AdminDashboardScreen.route
                                    "MANAGER" -> Screen.ManagerScreen.withIdUtente(validSessionId)
                                    else -> Screen.HomeScreen.withIdUtente(validSessionId)
                                }
                            } else {
                                // Se per assurdo abbiamo l'ID ma non il Token, forziamo il login
                                startDestination = Screen.LoginScreen.route
                            }

                        } else if (isFirstRun) {
                            // 2. Primo avvio assoluto -> Intro
                            startDestination = "welcome_intro_screen"
                        } else {
                            // 3. Sessione scaduta, logout o rememberMe=false -> Login
                            startDestination = Screen.LoginScreen.route
                        }
                    }
                }

                // UI di caricamento mentre decidiamo la rotta
                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Avvia la navigazione verso la destinazione calcolata
                    Navigation(startDestination = startDestination!!)
                }
            }
        }
    }
}