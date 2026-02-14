package com.dieti.dietiestates25.ui.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.dieti.dietiestates25.data.remote.FcmTokenRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SessionManager {
    private const val PREF_NAME = "UninaEstatesSession"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_EXPIRY_TIME = "session_expiry_time"
    private const val SESSION_DURATION_MS = 30L * 24 * 60 * 60 * 1000 // 30 Giorni

    // Tag speciale per il debug delle notifiche
    private const val TAG = "FCM_DEBUG"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserSession(
        context: Context,
        userId: String,
        nome: String,
        email: String,
        ruolo: String,
        token: String,
        rememberMe: Boolean
    ) {
        Log.d(TAG, "Salvataggio sessione utente: $userId")
        val editor = getPrefs(context).edit()
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, nome)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_ROLE, ruolo)
        editor.putString(KEY_AUTH_TOKEN, token)

        val expiryTime = if (rememberMe) System.currentTimeMillis() + SESSION_DURATION_MS else 0L
        editor.putLong(KEY_EXPIRY_TIME, expiryTime)
        editor.apply()

        // Avvio immediato sync
        Log.d(TAG, "Sessione salvata. Avvio sync FCM...")
        syncFcmToken(context, token)
    }

    fun validateAndRefreshSession(context: Context): String? {
        val prefs = getPrefs(context)
        val userId = prefs.getString(KEY_USER_ID, null)
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        val expiryTime = prefs.getLong(KEY_EXPIRY_TIME, -1L)

        if (userId == null || token == null) {
            Log.d(TAG, "Sessione non valida: userId o token null")
            logout(context)
            return null
        }

        if (expiryTime != 0L && System.currentTimeMillis() > expiryTime) {
            Log.d(TAG, "Sessione scaduta")
            logout(context)
            return null
        }

        if (expiryTime != 0L) {
            prefs.edit().putLong(KEY_EXPIRY_TIME, System.currentTimeMillis() + SESSION_DURATION_MS).apply()
        }

        // Sync token anche al refresh
        Log.d(TAG, "Sessione valida. Controllo aggiornamento FCM...")
        syncFcmToken(context, token)

        return userId
    }

    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    // --- LOGICA FCM REVISIONATA E PROTETTA DA CRASH ---

    private fun syncFcmToken(context: Context, authToken: String) {
        try {
            // DIAGNOSTICA: Verifica se il plugin google-services ha funzionato
            val resourceId = context.resources.getIdentifier("google_app_id", "string", context.packageName)
            if (resourceId == 0) {
                Log.e(TAG, "⚠️ ERRORE CONFIGURAZIONE: Il file google-services.json non è stato letto.")
                Log.e(TAG, "⚠️ SOLUZIONE: Assicurati di aver applicato il plugin 'com.google.gms.google-services' nel build.gradle :app")
                return
            }

            // 1. Inizializzazione sicura di Firebase (previene crash su alcuni device)
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
                Log.d(TAG, "Firebase inizializzato manualmente.")
            }

            // 2. Richiesta Token (Ora protetta dal try-catch generale)
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "FALLITO recupero token FCM", task.exception)
                    return@addOnCompleteListener
                }

                // 3. Ottenimento Token
                val token = task.result
                if (token.isNullOrBlank()) {
                    Log.w(TAG, "Token FCM restituito vuoto!")
                    return@addOnCompleteListener
                }

                Log.d(TAG, "Token FCM generato dal dispositivo: ${token.take(10)}...") // Log parziale per sicurezza

                // 4. Invio al Backend
                sendTokenToBackend(token, authToken)
            }
        } catch (e: Exception) {
            // CATTURA TUTTO: Evita che l'app crashi all'avvio se Firebase non è configurato o fallisce
            Log.e(TAG, "Errore critico durante inizializzazione/recupero FCM - L'app continua a funzionare", e)
        }
    }

    private fun sendTokenToBackend(fcmToken: String, authToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Impostiamo il token di auth esplicitamente per questa chiamata
                RetrofitClient.authToken = authToken

                Log.d(TAG, "Invio richiesta al backend per salvare il token...")
                val response = RetrofitClient.notificationService.updateFcmToken(FcmTokenRequest(fcmToken))

                if (response.isSuccessful) {
                    Log.d(TAG, "✅ SUCCESSO: Token FCM salvato nel backend!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "❌ ERRORE SERVER: Codice ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ ECCEZIONE DI RETE durante invio FCM: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun getUserEmail(context: Context): String? = getPrefs(context).getString(KEY_USER_EMAIL, null)

    // Metodi getter di utilità
    fun getUserId(context: Context): String? = getPrefs(context).getString(KEY_USER_ID, null)
    fun getUserName(context: Context): String? = getPrefs(context).getString(KEY_USER_NAME, null)
    fun getUserRole(context: Context): String? = getPrefs(context).getString(KEY_USER_ROLE, "UTENTE")
    fun getAuthToken(context: Context): String? = getPrefs(context).getString(KEY_AUTH_TOKEN, null)
}