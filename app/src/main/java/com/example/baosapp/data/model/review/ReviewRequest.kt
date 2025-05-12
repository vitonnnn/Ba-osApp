// data/model/review/ReviewRequest.kt
package com.example.baosapp.data.model.review

import com.squareup.moshi.Json

/**
 * Request para enviar una reseña de un baño.
 */
data class ReviewRequest(
    @Json(name = "rating") val rating: Float,
    @Json(name = "comment") val comment: String
)
