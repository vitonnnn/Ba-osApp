// app/src/main/java/com/example/baosapp/data/model/toilet/ToiletResponse.kt
package com.example.baosapp.data.model.toilet

data class ToiletResponse(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val avg_rating: Double,
    val accesible: Boolean,
    val publico: Boolean,
    val mixto: Boolean,
    val cambio_bebes: Boolean
)
