package com.example.baosapp.data.model.auth


sealed class ResultRegister {
    data class Success(val data: RegisterResponse): ResultRegister()
    data class Error(val message: String, val code: Int?): ResultRegister()
}