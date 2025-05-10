package com.example.baosapp.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.review.Review
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RateBathroomViewModel : ViewModel() {

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

    /** Establece la valoración global (1..5) */
    fun setRating(value: Int) {
        if (value in 1..5) _rating.value = value
    }

    /** Establece la limpieza (1..5) */
    fun setLimpieza(value: Int) {
        if (value in 1..5) _limpieza.value = value
    }

    /** Establece el olor (1..5) */
    fun setOlor(value: Int) {
        if (value in 1..5) _olor.value = value
    }

    /** Establece el comentario */
    fun setComment(text: String) {
        _comment.value = text
    }

    /**
     * Simula el envío de una reseña.
     * @param nombreBano Nombre del baño a reseñar.
     */
    fun submitReview(nombreBano: String) {
        // Validar que haya puntuaciones
        if (_rating.value == 0 || _limpieza.value == 0 || _olor.value == 0) return

        viewModelScope.launch {
            _isSubmitting.value = true
            delay(1_000) // Simula llamada a red/repositorio

            // Construir el objeto Review
            val review = Review(
                id = System.currentTimeMillis(),
                nombreBano = nombreBano,
                valoracion = _rating.value,
                limpieza   = _limpieza.value,
                olor       = _olor.value
            )
            // TODO: enviar `review` al repositorio

            _isSubmitting.value = false
            _submissionSuccess.emit(Unit)
        }
    }
}