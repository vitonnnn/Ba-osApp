// app/src/main/java/com/example/baosapp/ui/review/RateBathroomViewModel.kt
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

    fun setRating(value: Int) { if (value in 1..5) _rating.value = value }
    fun setLimpieza(value: Int) { if (value in 1..5) _limpieza.value = value }
    fun setOlor(value: Int)     { if (value in 1..5) _olor.value = value }
    fun setComment(text: String){ _comment.value = text }

    fun submitReview(toiletId: Long) {
        if (_rating.value == 0 || _limpieza.value == 0 || _olor.value == 0) return

        viewModelScope.launch {
            _isSubmitting.value = true
            try {
                repo.submitReview(toiletId, review)
                _submissionSuccess.emit(Unit)
            } catch (e: Exception) {
                // aquí podrías exponer otro flujo de error
            } finally {
                _isSubmitting.value = false
            }
        }
    }
}
