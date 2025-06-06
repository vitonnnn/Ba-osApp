// app/src/main/java/com/example/baosapp/data/model/toilet/Toilet.kt
package com.example.baosapp.data.model.toilet

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Toilet(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,

    @SerializedName("avg_rating")
    val avgRating: Double,    // <-- ahora sí lee "avg_rating" del JSON

    val accesible: Boolean,
    val publico: Boolean,
    val mixto: Boolean,
    @SerializedName("cambio_bebes")
    val cambioBebes: Boolean,

    @SerializedName("distance_km")
    val distanceKm: Double? = null  // opcional, si la API la devuelve
) : Parcelable
