package com.dieti.dietiestates25.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private val USER_ID_KEY = stringPreferencesKey("user_id")
    // NUOVA CHIAVE: Per sapere se è la prima apertura assoluta
    private val IS_FIRST_RUN_KEY = booleanPreferencesKey("is_first_run")

    // --- GESTIONE UTENTE ---
    val userId: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID_KEY] }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { it[USER_ID_KEY] = userId }
    }

    suspend fun clearUser() {
        context.dataStore.edit { it.remove(USER_ID_KEY) } // Rimuove solo l'ID, non il first run
    }

    // --- GESTIONE PRIMA APERTURA ---
    // Di default restituisce TRUE (se non trova la chiave, vuol dire che è la prima volta)
    val isFirstRun: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[IS_FIRST_RUN_KEY] ?: true }

    // Chiameremo questa funzione quando premi "Inizia ora" nella WelcomeScreen
    suspend fun setFirstRunCompleted() {
        context.dataStore.edit { it[IS_FIRST_RUN_KEY] = false }
    }
}