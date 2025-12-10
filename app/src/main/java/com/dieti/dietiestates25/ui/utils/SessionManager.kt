package com.dieti.dietiestates25.ui.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "UninaEstatesSession"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserSession(context: Context, userId: String, nome: String) {
        val editor = getPrefs(context).edit()
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_USER_NAME, nome)
        editor.apply()
    }

    fun getUserId(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_ID, null)
    }

    fun getUserName(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_NAME, null)
    }

    fun logout(context: Context) {
        val editor = getPrefs(context).edit()
        editor.clear()
        editor.apply()
    }
    
    fun isLoggedIn(context: Context): Boolean {
        return getUserId(context) != null
    }
}