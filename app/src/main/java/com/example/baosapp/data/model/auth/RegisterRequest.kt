package com.example.baosapp.data.model.auth

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)