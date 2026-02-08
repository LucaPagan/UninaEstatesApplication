package com.dieti.dietiestates25.ui.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {
    private const val PREF_NAME = "UninaEstatesSession"

    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_FIRST_RUN = "is_first_run" // Non cancellare mai

    // --- NUOVA CHIAVE FONDAMENTALE PER IL RIPRISTINO API ---
    private const val KEY_AUTH_TOKEN = "auth_token"

    // Nuove chiavi per la gestione scadenza
    private const val KEY_EXPIRY_TIME = "session_expiry_time"
    private const val SESSION_DURATION_MS = 30L * 24 * 60 * 60 * 1000 // 30 Giorni

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Abbiamo aggiunto il parametro 'rememberMe'
    fun saveUserSession(context: Context, userId: String, nome: String, ruolo: String, rememberMe: Boolean) {
        val editor = getPrefs(context).edit()

        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, nome)
        editor.putString(KEY_USER_ROLE, ruolo)

        if (rememberMe) {
            // Se "Ricordami" è attivo: Scadenza tra 30 giorni
            val expiryTime = System.currentTimeMillis() + SESSION_DURATION_MS
            editor.putLong(KEY_EXPIRY_TIME, expiryTime)
            Log.d("SessionManager", "Sessione salvata per 30 giorni. Scadenza: $expiryTime")
        } else {
            // Se "Ricordami" è spento: Mettiamo 0 per indicare "Sessione volatile" (valida finché l'app è aperta)
            editor.putLong(KEY_EXPIRY_TIME, 0L)
            Log.d("SessionManager", "Sessione volatile salvata (No Remember Me).")
        }

        editor.apply()
    }

    // --- NUOVI METODI PER IL TOKEN (Usati da AuthViewModel) ---
    fun saveAuthToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(context: Context): String? {
        return getPrefs(context).getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Questa funzione viene chiamata all'avvio dell'App (MainActivity).
     * Controlla se la sessione è valida e applica le regole richieste.
     */
    fun validateAndRefreshSession(context: Context): String? {
        val prefs = getPrefs(context)
        val userId = prefs.getString(KEY_USER_ID, null)
        val token = prefs.getString(KEY_AUTH_TOKEN, null) // Controllo anche il token
        val expiryTime = prefs.getLong(KEY_EXPIRY_TIME, -1L)

        // 1. Se non c'è utente O TOKEN, la sessione è invalida -> ritorna null
        if (userId == null || token == null) return null

        // 2. CASO: Checkbox NON spuntata (expiryTime == 0)
        // Poiché siamo all'avvio dell'app (MainActivity), se era volatile, dobbiamo uscire.
        if (expiryTime == 0L) {
            Log.d("SessionManager", "Trovata sessione volatile all'avvio. Richiedo nuovo login.")
            logout(context) // Pulisce i dati utente
            return null
        }

        // 3. CASO: Checkbox Spuntata (expiryTime > 0)
        val currentTime = System.currentTimeMillis()
        if (currentTime > expiryTime) {
            // Sessione scaduta (passati più di 30 giorni dall'ultimo avvio)
            Log.d("SessionManager", "Sessione scaduta (30gg passati). Richiedo nuovo login.")
            logout(context)
            return null
        } else {
            // Sessione valida: RESETTIAMO IL COUNTDOWN (altri 30 giorni da oggi)
            val newExpiry = currentTime + SESSION_DURATION_MS
            prefs.edit().putLong(KEY_EXPIRY_TIME, newExpiry).apply()
            Log.d("SessionManager", "Sessione valida. Countdown resettato per altri 30gg.")
            return userId
        }
    }

    fun getUserId(context: Context): String? = getPrefs(context).getString(KEY_USER_ID, null)
    fun getUserName(context: Context): String? = getPrefs(context).getString(KEY_USER_NAME, null)
    fun getUserRole(context: Context): String? = getPrefs(context).getString(KEY_USER_ROLE, "UTENTE")

    fun logout(context: Context) {
        val editor = getPrefs(context).edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USER_NAME)
        editor.remove(KEY_USER_ROLE)
        editor.remove(KEY_EXPIRY_TIME) // Rimuoviamo anche la scadenza
        editor.remove(KEY_AUTH_TOKEN) // Rimuoviamo il token
        editor.apply()
    }

    fun isLoggedIn(context: Context): Boolean = getUserId(context) != null
}