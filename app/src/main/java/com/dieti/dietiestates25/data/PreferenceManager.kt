package com.dieti.dietiestates25.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_FIRST_TIME_LAUNCH = "is_first_time_launch"
        private const val KEY_USER_TOKEN = "user_token"
        private const val KEY_RECENT_SEARCHES = "recent_searches"
        private const val MAX_RECENT_SEARCHES = 10
    }

    // Controlla se è la prima volta che l'app viene avviata
    fun isFirstTimeLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_LAUNCH, true)
    }

    // Imposta che l'app è stata già avviata
    fun setFirstTimeLaunchComplete() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME_LAUNCH, false).apply()
    }

    // Ottiene il token dell'utente
    fun getUserToken(): String? {
        return sharedPreferences.getString(KEY_USER_TOKEN, null)
    }

    // Salva il token dell'utente
    fun saveUserToken(token: String) {
        sharedPreferences.edit().putString(KEY_USER_TOKEN, token).apply()
    }

    // Ottiene le ricerche recenti
    fun getRecentSearches(): List<String> {
        val json = sharedPreferences.getString(KEY_RECENT_SEARCHES, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    // Salva una nuova ricerca
    fun saveSearch(query: String) {
        val searches = getRecentSearches().toMutableList()

        // Rimuovi la query se già presente (per evitare duplicati)
        searches.remove(query)

        // Aggiungi la nuova query all'inizio
        searches.add(0, query)

        // Mantieni solo le ultime MAX_RECENT_SEARCHES ricerche
        val trimmedSearches = searches.take(MAX_RECENT_SEARCHES)

        // Salva la lista aggiornata
        val json = gson.toJson(trimmedSearches)
        sharedPreferences.edit().putString(KEY_RECENT_SEARCHES, json).apply()
    }

    // Cancella tutte le ricerche recenti
    fun clearRecentSearches() {
        sharedPreferences.edit().remove(KEY_RECENT_SEARCHES).apply()
    }
}