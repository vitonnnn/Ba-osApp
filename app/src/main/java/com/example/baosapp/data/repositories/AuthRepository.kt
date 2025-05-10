package com.example.baosapp.data.repositories

import com.example.baosapp.data.remote.RetrofitClient
import com.example.baosapp.data.result.ResultLogin
import com.appbanos.data.model.auth.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    suspend fun login(username: String, password: String): ResultLogin =
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        ResultLogin.Success(body)
                    } ?: ResultLogin.Error("Respuesta vacía", response.code())
                } else {
                    ResultLogin.Error(response.message(), response.code())
                }
            } catch (e: Exception) {
                ResultLogin.Error(e.localizedMessage ?: "Error de red", null)
            }
        }
}
