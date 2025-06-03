// app/src/main/java/com/example/baosapp/data/model/review/ReviewResponse.kt
package com.example.baosapp.data.model.review

import com.squareup.moshi.Json

data class ReviewResponse(
    val id: Long,

    @Json(name = "user_id")
    val userId: Long,

    val valoracion: Int,
    val limpieza: Int,
    val olor: Int,
    val comment: String,

    @Json(name = "created_at")
    val createdAt: String?   // <- lo convertimos en nullable
)
