package com.dieti.dietiestates25.ui.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SessionManager {
    private const val PREF_NAME = "UninaEstatesSession"

    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ROLE = "user_role" // Nuova chiave
    private const val KEY_IS_FIRST_RUN = "is_first_run"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Aggiornato per salvare anche il ruolo
    fun saveUserSession(context: Context, userId: String, nome: String, ruolo: String) {
        Log.d("SessionManager", "Salvataggio: ID=$userId, Ruolo=$ruolo")
        val editor = getPrefs(context).edit()
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, nome)
        editor.putString(KEY_USER_ROLE, ruolo)
        editor.apply()
    }

    fun getUserId(context: Context): String? = getPrefs(context).getString(KEY_USER_ID, null)
    fun getUserName(context: Context): String? = getPrefs(context).getString(KEY_USER_NAME, null)

    // Getter per il ruolo (lo userai nella Home)
    fun getUserRole(context: Context): String? = getPrefs(context).getString(KEY_USER_ROLE, "UTENTE")

    fun logout(context: Context) {
        val editor = getPrefs(context).edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USER_NAME)
        editor.remove(KEY_USER_ROLE)
        editor.apply()
    }

    fun isLoggedIn(context: Context): Boolean = getUserId(context) != null
}