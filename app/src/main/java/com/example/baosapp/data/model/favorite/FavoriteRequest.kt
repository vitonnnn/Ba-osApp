// app/src/main/java/com/example/baosapp/data/model/favorites/FavoriteRequest.kt
package com.example.baosapp.data.model.favorite

import com.squareup.moshi.Json

/**
 * Request para añadir un baño a favoritos.
 */
data class FavoriteRequest(
    @Json(name = "toilet_id") val toiletId: Long
)
