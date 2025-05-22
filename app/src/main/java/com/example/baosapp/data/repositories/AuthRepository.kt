// app/src/main/java/com/example/baosapp/data/repositories/AuthRepository.kt
package com.example.baosapp.data.repositories

import android.content.Context
import com.appbanos.data.model.auth.LoginRequest
import com.example.baosapp.data.remote.RetrofitClient
import com.example.baosapp.data.result.ResultLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {

    // Construimos el ApiService ON-DEMAND usando el contexto
    private val apiService by lazy { RetrofitClient.create(context) }

    suspend fun login(username: String, password: String): ResultLogin =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        ResultLogin.Success(body)
                    } ?: ResultLogin.Error(
                        message = "Respuesta vacía",
                        code    = response.code()
                    )
                } else {
                    ResultLogin.Error(
                        message = response.message(),
                        code    = response.code()
                    )
                }
            } catch (e: Exception) {
                ResultLogin.Error(
                    message = e.localizedMessage ?: "Error de red",
                    code    = null
                )
            }
        }
}
