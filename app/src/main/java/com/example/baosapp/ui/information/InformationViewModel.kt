// app/src/main/java/com/example/baosapp/ui/information/InformationViewModel.kt
package com.example.baosapp.ui.information

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.review.ReviewResponse
import com.example.baosapp.data.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InformationViewModel(
    application: Application,
    private val toiletId: Long
) : AndroidViewModel(application) {

    private val repo = ReviewRepository(application)

    private val _uiState = MutableStateFlow(InformationUiState(isLoading = true))
    val uiState: StateFlow<InformationUiState> = _uiState

    init {
        loadReviews()
    }

    private fun loadReviews() {
        viewModelScope.launch {
            _uiState.value = InformationUiState(isLoading = true)
            try {
                val listResponse: List<ReviewResponse> = repo.getReviews(toiletId)

                // Mapeamos ReviewResponse -> ReviewUiModel (ahora incluimos username)
                val uiList: List<ReviewUiModel> = listResponse.map { review ->
                    ReviewUiModel(
                        id = review.id,
                        username = review.username,   // <-- Asignamos el username
                        valoracion = review.valoracion,
                        limpieza = review.limpieza,
                        olor = review.olor,
                        comment = review.comment,
                        createdAt = review.createdAt    // puede ser null
                    )
                }

                _uiState.value = InformationUiState(
                    isLoading = false,
                    reviews = uiList,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = InformationUiState(
                    isLoading = false,
                    reviews = emptyList(),
                    errorMessage = e.localizedMessage ?: "Error desconocido"
                )
            }
        }
    }
}
