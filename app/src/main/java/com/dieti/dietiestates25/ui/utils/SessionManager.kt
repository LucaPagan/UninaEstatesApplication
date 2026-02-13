package com.dieti.dietiestates25.ui.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.dieti.dietiestates25.data.remote.FcmTokenRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SessionManager {
    private const val PREF_NAME = "UninaEstatesSession"
    private const val FCM_PREF_NAME = "FCM_PREFS"

    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_AUTH_TOKEN = "auth_token"

    private const val KEY_EXPIRY_TIME = "session_expiry_time"
    private const val SESSION_DURATION_MS = 30L * 24 * 60 * 60 * 1000 // 30 Giorni

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
        val editor = getPrefs(context).edit()

        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, nome)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_ROLE, ruolo)
        editor.putString(KEY_AUTH_TOKEN, token)

        if (rememberMe) {
            val expiryTime = System.currentTimeMillis() + SESSION_DURATION_MS
            editor.putLong(KEY_EXPIRY_TIME, expiryTime)
        } else {
            editor.putLong(KEY_EXPIRY_TIME, 0L)
        }

        editor.apply()

        // --- NUOVO: Sincronizzazione Token FCM al login ---
        syncFcmToken(context, token)
    }

    private fun syncFcmToken(context: Context, authToken: String) {
        // 1. Controlla se c'è un token in sospeso salvato dal Service
        val fcmPrefs = context.getSharedPreferences(FCM_PREF_NAME, Context.MODE_PRIVATE)
        val pendingToken = fcmPrefs.getString("pending_token", null)

        if (pendingToken != null) {
            sendTokenToBackend(pendingToken, authToken)
            fcmPrefs.edit().remove("pending_token").apply()
        } else {
            // 2. Se non c'è, chiedi a Firebase il token attuale
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("SessionManager", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                sendTokenToBackend(token, authToken)
            }
        }
    }

    private fun sendTokenToBackend(fcmToken: String, authToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Assicuriamoci che Retrofit abbia il token di auth
                RetrofitClient.authToken = authToken
                RetrofitClient.notificationService.updateFcmToken(FcmTokenRequest(fcmToken))
                Log.d("SessionManager", "Token FCM sincronizzato dopo il login")
            } catch (e: Exception) {
                Log.e("SessionManager", "Errore sync FCM", e)
            }
        }
    }

    fun saveAuthToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(context: Context): String? = getPrefs(context).getString(KEY_AUTH_TOKEN, null)
    fun getUserEmail(context: Context): String? = getPrefs(context).getString(KEY_USER_EMAIL, null)

    fun validateAndRefreshSession(context: Context): String? {
        val prefs = getPrefs(context)
        val userId = prefs.getString(KEY_USER_ID, null)
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        val expiryTime = prefs.getLong(KEY_EXPIRY_TIME, -1L)

        if (userId == null || token == null) {
            logout(context)
            return null
        }

        if (expiryTime == 0L) {
            logout(context)
            return null
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime > expiryTime) {
            logout(context)
            return null
        }

        val newExpiry = currentTime + SESSION_DURATION_MS
        prefs.edit().putLong(KEY_EXPIRY_TIME, newExpiry).apply()

        return userId
    }

    fun getUserId(context: Context): String? = getPrefs(context).getString(KEY_USER_ID, null)
    fun getUserName(context: Context): String? = getPrefs(context).getString(KEY_USER_NAME, null)
    fun getUserRole(context: Context): String? = getPrefs(context).getString(KEY_USER_ROLE, "UTENTE")

    fun logout(context: Context) {
        val editor = getPrefs(context).edit()
        editor.clear() // Pulisce tutto
        editor.apply()

        // Opzionale: Si potrebbe chiamare un endpoint di logout per rimuovere il token FCM lato server
        // per evitare notifiche dopo il logout, ma Firebase gestisce bene i token invalidi.
    }

    fun isLoggedIn(context: Context): Boolean = getUserId(context) != null
}