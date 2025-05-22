// app/src/main/java/com/example/baosapp/data/local/SessionManager.kt
package com.example.baosapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Importa la extensión que acabamos de crear
import com.example.baosapp.data.local.userPrefsDataStore

object SessionManager {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    /** Guarda el token de autenticación en DataStore */
    suspend fun saveToken(context: Context, token: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    /** Flujo con el token (o null si no existe) */
    fun getToken(context: Context): Flow<String?> =
        context.userPrefsDataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }

    /** Flujo que indica si el usuario está logueado */
    fun isLoggedIn(context: Context): Flow<Boolean> =
        getToken(context).map { token ->
            !token.isNullOrEmpty()
        }

    /** Elimina el token de autenticación */
    suspend fun clearToken(context: Context) {
        context.userPrefsDataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }
}
