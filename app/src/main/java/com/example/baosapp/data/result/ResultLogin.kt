package com.example.baosapp.data.result

import com.miempresa.appbanos.data.model.auth.LoginResponse

data class ResultLogin(
    val data: LoginResponse?,
    val message: String? = null,
    val code: Int
)
