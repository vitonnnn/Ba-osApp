package com.example.baosapp.data.model.favorite

import com.squareup.moshi.Json

/**
 * Respuesta de GET /favorites: datos de un baño favorito.
 */
data class FavoriteResponse(
    @Json(name = "id")         val id: Long,
    @Json(name = "name")       val name: String,
    @Json(name = "latitude")   val latitude: Double,
    @Json(name = "longitude")  val longitude: Double,
    @Json(name = "avg_rating") val avgRating: Double
)