// app/src/main/java/com/example/baosapp/data/repositories/AuthRepository.kt
package com.example.baosapp.data.repositories

import android.content.Context
import com.appbanos.data.model.auth.LoginRequest
import com.example.baosapp.data.model.auth.RegisterRequest
import com.example.baosapp.data.model.auth.ResultRegister
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
    suspend fun register(username: String, password: String, email: String): ResultRegister =
        withContext(Dispatchers.IO) {
            try {
                val resp = apiService.register(RegisterRequest(username, password, email))
                if (resp.isSuccessful) {
                    resp.body()?.let { ResultRegister.Success(it) }
                        ?: ResultRegister.Error("Respuesta vacía", resp.code())
                } else {
                    ResultRegister.Error(resp.message(), resp.code())
                }
            } catch (e: Exception) {
                ResultRegister.Error(e.localizedMessage ?: "Error de red", null)
            }
        }
}
