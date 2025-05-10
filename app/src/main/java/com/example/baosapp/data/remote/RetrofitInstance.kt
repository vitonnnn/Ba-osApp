package com.example.baosapp.data.remote

import com.miempresa.appbanos.data.remote.ToiletApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ToiletApiService by lazy {
        retrofit.create(ToiletApiService::class.java)
    }
}
