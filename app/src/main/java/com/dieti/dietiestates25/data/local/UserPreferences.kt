package com.dieti.dietiestates25.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Estensione per creare il DataStore
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        // Chiave necessaria per il fix dell'autenticazione API (header X-Auth-Email)
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        // Chiave per gestire la schermata di benvenuto
        val IS_FIRST_RUN_KEY = booleanPreferencesKey("is_first_run")
    }

    // --- GESTIONE UTENTE ---

    val userId: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID_KEY] }

    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_EMAIL_KEY] }

    // Salva sia ID che Email (Usato dal Login/Register ViewModel)
    suspend fun saveUserData(id: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
            preferences[USER_EMAIL_KEY] = email
        }
    }

    // Metodo di compatibilità se serve salvare solo l'ID
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { it[USER_ID_KEY] = userId }
    }

    // Rimuove i dati di sessione ma mantiene lo stato "First Run"
    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_EMAIL_KEY)
        }
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