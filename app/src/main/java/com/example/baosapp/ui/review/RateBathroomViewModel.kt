package com.example.baosapp.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.review.ReviewRequest
import com.example.baosapp.data.repositories.ToiletRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de envío de reseñas.
 */
class RateBathroomViewModel(
    private val repo: ToiletRepository
) : ViewModel() {

    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating

    private val _limpieza = MutableStateFlow(0)
    val limpieza: StateFlow<Int> = _limpieza

    private val _olor = MutableStateFlow(0)
    val olor: StateFlow<Int> = _olor

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _submissionSuccess = MutableSharedFlow<Unit>()
    val submissionSuccess: SharedFlow<Unit> = _submissionSuccess

    fun setRating(value: Int) {
        if (value in 1..5) _rating.value = value
    }

    fun setLimpieza(value: Int) {
        if (value in 1..5) _limpieza.value = value
    }

    fun setOlor(value: Int) {
        if (value in 1..5) _olor.value = value
    }

    fun setComment(text: String) {
        _comment.value = text
    }

    /**
     * Envía una reseña al servidor usando un ReviewRequest.
     * @param toiletId Id del baño a reseñar.
     * @param review    Request que contiene rating, limpieza, olor y comentario.
     */
    fun submitReview(toiletId: Long, review: ReviewRequest) {
        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                repo.submitReview(toiletId, review)
                _submissionSuccess.emit(Unit)
            } catch (e: Exception) {
                // manejar error si lo deseas
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
