// data/model/review/ReviewResponse.kt
package com.example.baosapp.data.model.review

import com.squareup.moshi.Json

/**
 * Respuesta que devuelve el servidor al crear o consultar una reseña.
 */
data class ReviewResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "toilet_id") val toiletId: Long,
    @Json(name = "user_id") val userId: Long,
    @Json(name = "rating") val rating: Float,
    @Json(name = "comment") val comment: String,
    @Json(name = "created_at") val createdAt: String
)
