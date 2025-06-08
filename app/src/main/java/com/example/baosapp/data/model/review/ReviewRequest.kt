// data/model/review/ReviewRequest.kt
// app/src/main/java/com/example/baosapp/data/model/review/ReviewRequest.kt
package com.example.baosapp.data.model.review

import com.squareup.moshi.Json

/**
 * Request para enviar una reseña de un baño.
 */
data class ReviewRequest(
    @Json(name = "valoracion") val valoracion: Int,
    @Json(name = "limpieza")   val limpieza: Int,
    @Json(name = "olor")       val olor: Int,
    @Json(name = "comment")    val comment: String
)
