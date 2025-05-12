package com.example.baosapp.data.remote

import com.example.baosapp.data.model.review.ReviewRequest
import com.example.baosapp.data.model.review.ReviewResponse
import com.example.baosapp.data.model.toilet.Toilet
import retrofit2.HttpException

class ToiletsRepository(
    private val api: ApiService = RetrofitClient.apiService
) {
    /** Devuelve la lista completa de baños o lanza excepción en caso de error */
    suspend fun getAllToilets(): List<Toilet> {
        val resp = api.listToilets()
        if (resp.isSuccessful) {
            return resp.body() ?: emptyList()
        }
        throw HttpException(resp)
    }

    /** Devuelve los detalles de un baño concreto */
    suspend fun getToiletById(id: Long): Toilet {
        val resp = api.getToilet(id)
        if (resp.isSuccessful) {
            return resp.body() ?: throw Exception("Baño no encontrado")
        }
        throw HttpException(resp)
    }

    /** Publica una reseña para el baño con id [toiletId] */
    suspend fun submitReview(toiletId: Long, review: ReviewRequest): ReviewResponse {
        val resp = api.postReview(toiletId, review)
        if (resp.isSuccessful) {
            return resp.body()!!
        }
        throw HttpException(resp)
    }
}
