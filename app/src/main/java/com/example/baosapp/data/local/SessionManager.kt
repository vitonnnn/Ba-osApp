// app/src/main/java/com/example/baosapp/data/local/SessionManager.kt
package com.example.baosapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.baosapp.data.local.userPrefsDataStore

object SessionManager {
    private val TOKEN_KEY   = stringPreferencesKey("auth_token")
    private val USER_ID_KEY = longPreferencesKey("user_id")

    /** Guarda el token de autenticación */
    suspend fun saveToken(context: Context, token: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    /** Recupera el token de autenticación */
    fun getToken(context: Context): Flow<String?> =
        context.userPrefsDataStore.data.map { prefs -> prefs[TOKEN_KEY] }

    /** Guarda el ID del usuario */
    suspend fun saveUserId(context: Context, userId: Long) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    /** Recupera el ID del usuario (o null si no existe) */
    fun getUserId(context: Context): Flow<Long> =
        context.userPrefsDataStore.data
            .map { it[USER_ID_KEY] ?: 0L }

    /** Indica si hay sesión iniciada (token no vacío) */
    fun isLoggedIn(context: Context): Flow<Boolean> =
        getToken(context).map { !it.isNullOrEmpty() }

    /** Limpia token y ID de usuario */
    suspend fun clearToken(context: Context) {
        context.userPrefsDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}
