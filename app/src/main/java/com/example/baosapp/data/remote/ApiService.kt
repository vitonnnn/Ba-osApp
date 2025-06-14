package com.example.baosapp.data.remote

import ContributionsResponse
import com.appbanos.data.model.auth.LoginRequest
import com.appbanos.data.model.auth.LoginResponse
import com.example.baosapp.data.model.auth.RegisterRequest
import com.example.baosapp.data.model.auth.RegisterResponse
import com.example.baosapp.data.model.auth.UserResponse
import com.example.baosapp.data.model.favorite.FavoriteRequest
import com.example.baosapp.data.model.favorite.FavoriteResponse
import com.example.baosapp.data.model.favorite.MessageResponse
import com.example.baosapp.data.model.review.ReviewRequest
import com.example.baosapp.data.model.review.ReviewResponse
import com.example.baosapp.data.model.toilet.CreateToiletRequest

import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.model.toilet.ToiletResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("users/{u_id}")
    suspend fun getUserById(
        @Path("u_id") userId: Long
    ): Response<UserResponse>

    @POST("toilets")
    suspend fun createToilet(
        @Body request: CreateToiletRequest
    ): Response<ToiletResponse>

    @GET("toilets")
    suspend fun listToilets(): Response<List<Toilet>>

    @GET("toilets/{id}")
    suspend fun getToilet(@Path("id") id: Long): Response<Toilet>

    @GET("toilets/nearby")
    suspend fun listNearbyToilets(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Double,
        @Query("mixto") mixto: Boolean? = null,
        @Query("accesible") accesible: Boolean? = null,
        @Query("publico") publico: Boolean? = null,
        @Query("cambio_bebes") cambioBebes: Boolean? = null,
        @Query("sort") sort: String = "desc"
    ): Response<List<Toilet>>

    @POST("toilets/{id}/reviews")
    suspend fun postReview(
        @Path("id") toiletId: Long,
        @Body review: ReviewRequest
    ): Response<ReviewResponse>

    @GET("toilets/{t_id}/reviews")
    suspend fun listReviews(
        @Path("t_id") toiletId: Long
    ): Response<List<ReviewResponse>>

    @GET("favorites")
    suspend fun listFavorites(): Response<List<Toilet>>

    @POST("favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): Response<MessageResponse>

    @DELETE("favorites/{toilet_id}")
    suspend fun removeFavorite(@Path("toilet_id") toiletId: Long): Response<MessageResponse>

    @GET("users/{u_id}/contributions")
    suspend fun getContributions(
        @Path("u_id") userId: Long
    ): Response<ContributionsResponse>

    @DELETE("toilets/{id}")
    suspend fun deleteToilet(
        @Path("id") toiletId: Long
    ): Response<MessageResponse>

    @DELETE("toilets/{t_id}/reviews/{r_id}")
    suspend fun deleteReview(
        @Path("t_id") toiletId: Long,
        @Path("r_id") reviewId: Long
    ): Response<MessageResponse>
}
