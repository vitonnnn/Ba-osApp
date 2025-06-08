// LoginResponse.kt
package com.appbanos.data.model.auth


data class LoginResponse(
    val access_token: String,
    val user: User
)
