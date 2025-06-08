package com.example.baosapp.data.repositories

import android.content.Context
import com.example.baosapp.data.model.review.ReviewRequest
import com.example.baosapp.data.model.review.ReviewResponse
import com.example.baosapp.data.model.toilet.CreateToiletRequest
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.model.toilet.ToiletResponse
import com.example.baosapp.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ToiletRepository(
    private val context: Context
) {
    private val apiService by lazy { RetrofitClient.create(context) }

    suspend fun getAllToilets(): List<Toilet> {
        val resp = apiService.listToilets()
        if (resp.isSuccessful) {
            return resp.body() ?: emptyList()
        }
        throw HttpException(resp)
    }

    suspend fun getToiletById(id: Long): Toilet {
        val resp = apiService.getToilet(id)
        if (resp.isSuccessful) {
            return resp.body() ?: throw Exception("Baño no encontrado")
        }
        throw HttpException(resp)
    }

    suspend fun submitReview(toiletId: Long, review: ReviewRequest): ReviewResponse {
        val resp = apiService.postReview(toiletId, review)
        if (resp.isSuccessful) {
            return resp.body()!!
        }
        throw HttpException(resp)
    }

    suspend fun createToilet(
        name: String,
        latitude: Double,
        longitude: Double,
        accesible: Boolean,
        publico: Boolean,
        mixto: Boolean,
        cambioBebes: Boolean
    ): ToiletResponse = withContext(Dispatchers.IO) {
        val requestBody = CreateToiletRequest(
            name = name,
            latitude = latitude,
            longitude = longitude,
            accesible = accesible,
            publico = publico,
            mixto = mixto,
            cambio_bebes = cambioBebes
        )

        val resp = apiService.createToilet(requestBody)
        if (resp.isSuccessful) {
            resp.body() ?: throw IllegalStateException("Respuesta vacía al crear toilet")
        } else {
            throw HttpException(resp)
        }
    }





    suspend fun getNearbyToilets(
        lat: Double,
        lon: Double,
        radius: Double,
        mixto: Boolean?,
        accesible: Boolean?,
        publico: Boolean?,
        cambioBebes: Boolean?,
        sort: String
    ): List<Toilet> = withContext(Dispatchers.IO) {
        val resp = apiService.listNearbyToilets(lat, lon, radius, mixto, accesible, publico, cambioBebes, sort)
        if (resp.isSuccessful) return@withContext resp.body() ?: emptyList()
        throw HttpException(resp)
    }
}
