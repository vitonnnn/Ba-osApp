package com.example.baosapp.data.local

// app/src/main/java/com/example/baosapp/data/local/UserPrefs.kt


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

object UserPrefs {
    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    suspend fun saveToken(ctx: Context, token: String) {
        ctx.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    fun getToken(ctx: Context): Flow<String?> =
        ctx.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]
        }
}
