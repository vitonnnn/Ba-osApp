
package com.example.baosapp.data.model.toilet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Toilet(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val avgRating: Double,
    val accesible: Boolean,
    val publico: Boolean,
    val mixto: Boolean,
    val cambioBebes: Boolean
) : Parcelable
