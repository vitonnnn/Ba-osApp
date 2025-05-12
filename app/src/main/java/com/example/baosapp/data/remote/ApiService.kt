package com.example.baosapp.data.remote

import com.appbanos.data.model.auth.LoginRequest
import com.appbanos.data.model.auth.LoginResponse
import com.example.baosapp.data.model.review.ReviewRequest
import com.example.baosapp.data.model.review.ReviewResponse

import com.example.baosapp.data.model.toilet.Toilet
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("toilets")
    suspend fun listToilets(): Response<List<Toilet>>

    @GET("toilets/{id}")
    suspend fun getToilet(@Path("id") id: Long): Response<Toilet>

    @POST("toilets/{id}/reviews")
    suspend fun postReview(
        @Path("id") toiletId: Long,
        @Body review: ReviewRequest
    ): Response<ReviewResponse>
}
