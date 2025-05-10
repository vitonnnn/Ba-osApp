package com.miempresa.appbanos.data.remote

import com.miempresa.appbanos.data.model.auth.LoginRequest
import com.miempresa.appbanos.data.model.auth.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ToiletApiService {
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    // futuros endpoints:
    // @GET("toilets") fun listToilets(): Call<List<Toilet>>
    // @POST("reserve") fun reserve(@Body reservation: ReservationRequest): Call<ReservationResponse>
}
