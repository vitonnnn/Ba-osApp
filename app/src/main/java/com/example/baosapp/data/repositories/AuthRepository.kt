package com.example.baosapp.data.repositories

import com.example.baosapp.data.remote.RetrofitInstance
import com.example.baosapp.data.result.*
import com.miempresa.appbanos.data.model.auth.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    suspend fun login(username: String, password: String): ResultLogin =
        withContext(Dispatchers.IO) {
            val resp = RetrofitInstance
                .api
                .login(LoginRequest(username, password))
                .execute()

            if (resp.isSuccessful) {
                ResultLogin(
                    data    = resp.body(),
                    code    = resp.code()
                )
            } else {
                ResultLogin(
                    data    = null,
                    message = resp.message(),
                    code    = resp.code()
                )
            }
        }
}
