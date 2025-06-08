// app/src/main/java/com/example/baosapp/data/remote/RetrofitClient.kt
package com.example.baosapp.data.remote

import android.content.Context
import com.example.baosapp.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.47:5000/"

    fun create(context: Context): ApiService {
        // Interceptor que añade el JWT en Authorization
        val authInterceptor = Interceptor { chain ->
            // Se bloquea brevemente para obtener el token
            val token = runBlocking {
                SessionManager.getToken(context).first()
            }
            val request = chain.request()
                .newBuilder()
                .apply {
                    token?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                }
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
