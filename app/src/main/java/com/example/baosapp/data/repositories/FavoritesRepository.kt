
package com.example.baosapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")
private val FAVORITES_KEY = stringSetPreferencesKey("favorites_ids")

/**
 * Repositorio para manejar los baños marcados como favoritos usando DataStore.
 */
class FavoritesRepository(private val context: Context) {

    /** Flujo con el set de IDs en favoritos */
    val favoritesFlow: Flow<Set<Long>> = context.dataStore.data
        .map { prefs ->
            prefs[FAVORITES_KEY]
                ?.mapNotNull { it.toLongOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    /**
     * Añade o quita [toiletId] de favoritos.
     */
    suspend fun toggleFavorite(toiletId: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            val strId = toiletId.toString()
            prefs[FAVORITES_KEY] = if (current.contains(strId)) {
                current - strId
            } else {
                current + strId
            }
        }
    }
}
