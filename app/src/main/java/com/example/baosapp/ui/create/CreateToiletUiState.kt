package com.example.baosapp.ui.create

data class CreateToiletUiState(
    val nombre: String             = "",
    val latitude: Double?          = null,
    val longitude: Double?         = null,
    val accesible: Boolean         = false,
    val publico: Boolean           = false,
    val mixto: Boolean             = false,
    val cambioBebes: Boolean       = false,
    val isLoading: Boolean         = false,
    val errorMessage: String?      = null,
    val success: Boolean           = false
)