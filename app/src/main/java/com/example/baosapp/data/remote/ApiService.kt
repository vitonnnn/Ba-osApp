// app/src/main/java/com/example/baosapp/data/remote/ApiService.kt
package com.example.baosapp.data.remote

import com.appbanos.data.model.auth.LoginRequest
import com.appbanos.data.model.auth.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // futuros endpoints:
    // @GET("/toilets") suspend fun listToilets(): Response<List<Toilet>>
    // @POST("/toilets/{id}/reviews") suspend fun postReview(
    //     @Path("id") toiletId: Long,
    //     @Body review: ReviewRequest
    // ): Response<ReviewResponse>
}
