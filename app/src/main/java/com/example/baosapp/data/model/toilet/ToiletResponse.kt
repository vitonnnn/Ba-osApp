// app/src/main/java/com/example/baosapp/data/model/toilet/ToiletResponse.kt
package com.example.baosapp.data.model.toilet

import com.google.gson.annotations.SerializedName

data class ToiletResponse(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("avg_rating")               // ← aquí mapeas "avg_rating"
    val avgrating: Double,
    val accesible: Boolean,
    val publico: Boolean,
    val mixto: Boolean,
    val cambiobebes: Boolean
)
