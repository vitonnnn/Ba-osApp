package com.example.baosapp.data.repositories

import android.content.Context
import com.example.baosapp.data.model.favorite.FavoriteRequest
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.remote.RetrofitClient
import retrofit2.HttpException

class FavoritesRepository(private val context: Context) {

    private val apiService by lazy { RetrofitClient.create(context) }

    /**
     * Devuelve directamente la lista de favoritos como objetos Toilet, sin transformar.
     */
    suspend fun getFavorites(): List<Toilet> {
        val resp = apiService.listFavorites()
        if (resp.isSuccessful) {
            return resp.body() ?: emptyList()
        }
        throw HttpException(resp)
    }

    suspend fun addFavorite(toiletId: Long) {
        val resp = apiService.addFavorite(FavoriteRequest(toiletId))
        if (resp.isSuccessful) return
        when (resp.code()) {
            409, 422 -> return
            else -> throw RuntimeException("Error añadiendo favorito: ${resp.code()}")
        }
    }

    suspend fun removeFavorite(toiletId: Long) {
        val resp = apiService.removeFavorite(toiletId)
        if (resp.isSuccessful) return
        when (resp.code()) {
            404 -> return
            else -> throw RuntimeException("Error eliminando favorito: ${resp.code()}")
        }
    }

    suspend fun toggleFavorite(toilet: Toilet, currentlyFavorite: Boolean) {
        if (currentlyFavorite) removeFavorite(toilet.id)
        else addFavorite(toilet.id)
    }
}
