// app/src/main/java/com/example/baosapp/data/local/SessionManager.kt
package com.example.baosapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1) Extensión para obtener el DataStore de prefs
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object SessionManager {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    /** Guarda el token en DataStore */
    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    /** Flujo del token o null si no existe */
    fun getToken(context: Context): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }

    /** Flujo booleano: true si hay token no vacío */
    fun isLoggedIn(context: Context): Flow<Boolean> =
        getToken(context).map { token ->
            !token.isNullOrEmpty()
        }

    /** Borra el token (cierra sesión) */
    suspend fun clearToken(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}
