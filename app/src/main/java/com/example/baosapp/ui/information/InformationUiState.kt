// app/src/main/java/com/example/baosapp/ui/information/InformationUiState.kt
package com.example.baosapp.ui.information



data class ReviewUiModel(
    val id: Long,
    val valoracion: Int,
    val limpieza: Int,
    val olor: Int,
    val comment: String,
    val createdAt: String?   // <- aquí permitimos que sea nulo
)

data class InformationUiState(
    val isLoading: Boolean = false,
    val reviews: List<ReviewUiModel> = emptyList(),
    val errorMessage: String? = null
)