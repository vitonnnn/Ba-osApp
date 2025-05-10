package com.example.baosapp.data.result

import com.appbanos.data.model.auth.LoginResponse

sealed class ResultLogin {
    data class Success(val data: LoginResponse): ResultLogin()
    data class Error(val message: String, val code: Int?): ResultLogin()
}
