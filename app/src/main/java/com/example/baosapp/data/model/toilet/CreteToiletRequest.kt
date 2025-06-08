// app/src/main/java/com/example/baosapp/data/model/toilet/CreateToiletRequest.kt
package com.example.baosapp.data.model.toilet

data class CreateToiletRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val accesible: Boolean = false,
    val publico: Boolean = false,
    val mixto: Boolean = false,
    val cambio_bebes: Boolean = false
)
