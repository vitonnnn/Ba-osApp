// app/src/main/java/com/example/baosapp/data/repositories/ReviewRepository.kt
package com.example.baosapp.data.repositories

import android.content.Context
import com.example.baosapp.data.model.review.ReviewResponse
import com.example.baosapp.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ReviewRepository(private val context: Context) {

    private val apiService by lazy { RetrofitClient.create(context) }

    /**
     * Devuelve una lista de ReviewResponse directamente desde el endpoint.
     */
    suspend fun getReviews(toiletId: Long): List<ReviewResponse> =
        withContext(Dispatchers.IO) {
            val resp = apiService.listReviews(toiletId)
            if (resp.isSuccessful) {
                resp.body() ?: emptyList()
            } else {
                throw HttpException(resp)
            }
        }



}
