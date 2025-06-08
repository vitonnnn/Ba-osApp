// en data/repositories/ContributionsRepository.kt
package com.example.baosapp.data.repositories

import ContributionsResponse
import android.content.Context
import com.example.baosapp.data.model.favorite.MessageResponse
import com.example.baosapp.data.remote.RetrofitClient
import retrofit2.HttpException

class ContributionsRepository(context: Context) {
    private val api = RetrofitClient.create(context)

    suspend fun getContributions(userId: Long): ContributionsResponse {
        val resp = api.getContributions(userId)
        if (resp.isSuccessful) {
            return resp.body()!!
        }
        throw HttpException(resp)
    }

    suspend fun deleteToilet(toiletId: Long): MessageResponse {
        val resp = api.deleteToilet(toiletId)
        if (resp.isSuccessful) {
            return resp.body()!!
        }
        throw HttpException(resp)
    }

    suspend fun deleteReview(toiletId: Long, reviewId: Long): MessageResponse {
        val resp = api.deleteReview(toiletId, reviewId)
        if (resp.isSuccessful) {
            return resp.body()!!
        }
        throw HttpException(resp)
    }
}
