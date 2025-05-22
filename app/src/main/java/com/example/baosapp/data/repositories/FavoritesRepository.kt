package com.example.baosapp.data.repositories

import android.content.Context
import com.example.baosapp.data.model.favorite.FavoriteRequest
import com.example.baosapp.data.model.favorite.FavoriteResponse
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.remote.RetrofitClient

/**
 * Repositorio de favoritos que persiste en el servidor usando Retrofit.
 * Utiliza Context para construir el ApiService con el interceptor JWT.
 */
class FavoritesRepository(private val context: Context) {

    private val apiService by lazy { RetrofitClient.create(context) }

    /**
     * Obtiene del servidor la lista de favoritos del usuario autenticado.
     * @return Lista de Toilet mapeados desde FavoriteResponse
     * @throws RuntimeException si la respuesta no es exitosa (excepto 422)
     */
    suspend fun getFavorites(): List<Toilet> {
        val resp = apiService.listFavorites()
        if (!resp.isSuccessful) {
            throw RuntimeException("Error fetching favorites: ${resp.code()}")
        }
        return resp.body()
            .orEmpty()
            .map { fr: FavoriteResponse ->
                Toilet(
                    id         = fr.id,
                    name       = fr.name,
                    latitude   = fr.latitude,
                    longitude  = fr.longitude,
                    avgRating  = fr.avgRating,
                    accesible   = false,
                    publico     = false,
                    mixto       = false,
                    cambioBebes = false
                )
            }
    }

    /**
     * Añade un favorito en el servidor.
     * Ignora HTTP 409 (Conflict) y 422 (Unprocessable Entity) como no-op.
     */
    suspend fun addFavorite(toiletId: Long) {
        val resp = apiService.addFavorite(FavoriteRequest(toiletId))
        if (resp.isSuccessful) return
        when (resp.code()) {
            409, 422 -> return
            else -> throw RuntimeException("Error añadiendo favorito: ${resp.code()}")
        }
    }

    /**
     * Elimina un favorito del servidor.
     * Ignora HTTP 404 (Not Found) como no-op.
     */
    suspend fun removeFavorite(toiletId: Long) {
        val resp = apiService.removeFavorite(toiletId)
        if (resp.isSuccessful) return
        when (resp.code()) {
            404 -> return
            else -> throw RuntimeException("Error eliminando favorito: ${resp.code()}")
        }
    }

    /**
     * Alterna el estado de favorito para un Toilet dado.
     */
    suspend fun toggleFavorite(toilet: Toilet, currentlyFavorite: Boolean) {
        if (currentlyFavorite) removeFavorite(toilet.id)
        else addFavorite(toilet.id)
    }
}