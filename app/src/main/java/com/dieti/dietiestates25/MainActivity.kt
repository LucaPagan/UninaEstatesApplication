package com.dieti.dietiestates25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.ui.navigation.Navigation
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Inizializza le preferenze
        val userPrefs = UserPreferences(applicationContext)

        setContent {
            DietiEstatesTheme {
                // Variabile che conterrà la rotta di partenza calcolata
                var startDestination by remember { mutableStateOf<String?>(null) }

                // Raccogliamo i dati in modo asincrono
                // NOTA: Usiamo collectAsState con un valore iniziale fittizio per capire quando è pronto
                val isFirstRunState = userPrefs.isFirstRun.collectAsState(initial = null)
                val userIdState = userPrefs.userId.collectAsState(initial = null) // Può essere null davvero

                // Calcolo della logica
                LaunchedEffect(isFirstRunState.value, userIdState.value) {
                    val isFirstRun = isFirstRunState.value
                    val userId = userIdState.value

                    // Se i dati non sono ancora stati caricati dal disco, aspettiamo (rimaniamo null)
                    if (isFirstRun != null) { // userId può essere null, quindi controlliamo solo firstRun che è booleano

                        startDestination = if (userId != null && userId.isNotEmpty()) {
                            // CASO 1: C'è un utente loggato con "Ricordami"
                            // Vai diretto alla Home con l'ID
                            Screen.HomeScreen.route + "/$userId"
                        } else if (isFirstRun) {
                            // CASO 2: Prima volta assoluta (o dati cancellati)
                            // Vai alla Welcome Screen (uso una stringa fissa o aggiungo la route a Screen)
                            "welcome_intro_screen"
                        } else {
                            // CASO 3: App già aperta in passato, ma utente non loggato
                            // Vai al Login
                            Screen.LoginScreen.route
                        }
                    }
                }

                // UI
                if (startDestination == null) {
                    // Loading (Splash screen momentaneo)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // App pronta con la destinazione giusta
                    Navigation(startDestination = startDestination!!)
                }
            }
        }
    }
}