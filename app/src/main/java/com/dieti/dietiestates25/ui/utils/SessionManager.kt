package com.dieti.dietiestates25.ui.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {
    private const val PREF_NAME = "UninaEstatesSession"

    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email" // NUOVO CAMPO
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
        email: String, // Parametro Aggiunto
        ruolo: String,
        token: String,
        rememberMe: Boolean
    ) {
        val editor = getPrefs(context).edit()

        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, nome)
        editor.putString(KEY_USER_EMAIL, email) // Salviamo l'email
        editor.putString(KEY_USER_ROLE, ruolo)
        editor.putString(KEY_AUTH_TOKEN, token)

        if (rememberMe) {
            val expiryTime = System.currentTimeMillis() + SESSION_DURATION_MS
            editor.putLong(KEY_EXPIRY_TIME, expiryTime)
        } else {
            editor.putLong(KEY_EXPIRY_TIME, 0L)
        }

        editor.apply()
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
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USER_NAME)
        editor.remove(KEY_USER_EMAIL)
        editor.remove(KEY_USER_ROLE)
        editor.remove(KEY_AUTH_TOKEN)
        editor.remove(KEY_EXPIRY_TIME)
        editor.apply()
    }

    fun isLoggedIn(context: Context): Boolean = getUserId(context) != null
}