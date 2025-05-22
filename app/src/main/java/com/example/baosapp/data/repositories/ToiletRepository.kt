package com.example.baosapp.data.repositories

import android.content.Context
import com.example.baosapp.data.model.review.ReviewRequest
import com.example.baosapp.data.model.review.ReviewResponse
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.remote.RetrofitClient
import retrofit2.HttpException

class ToiletRepository(
    private val context: Context
) {
    private val apiService by lazy { RetrofitClient.create(context) }
    /** Devuelve la lista completa de baños o lanza excepción en caso de error */
    suspend fun getAllToilets(): List<Toilet> {
        val resp = apiService.listToilets()
        if (resp.isSuccessful) {
            return resp.body() ?: emptyList()
        }
        throw HttpException(resp)
    }

    /** Devuelve los detalles de un baño concreto */
    suspend fun getToiletById(id: Long): Toilet {
        val resp = apiService.getToilet(id)
        if (resp.isSuccessful) {
            return resp.body() ?: throw Exception("Baño no encontrado")
        }
        throw HttpException(resp)
    }

    /** Publica una reseña para el baño con id [toiletId] */
    suspend fun submitReview(toiletId: Long, review: ReviewRequest): ReviewResponse {
        val resp = apiService.postReview(toiletId, review)
        if (resp.isSuccessful) {
            return resp.body()!!
        }
        throw HttpException(resp)
    }
}
