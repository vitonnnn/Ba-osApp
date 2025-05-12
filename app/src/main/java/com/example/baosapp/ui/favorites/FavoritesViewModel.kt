// app/src/main/java/com/example/baosapp/ui/favorites/FavoritesViewModel.kt
package com.example.baosapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.local.FavoritesRepository

import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.remote.ToiletsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de favoritos.
 * Combina el flujo de IDs favoritos con la lista completa de baños.
 */
class FavoritesViewModel(
    private val favoritesRepo: FavoritesRepository,
    private val toiletRepo: ToiletsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        viewModelScope.launch {
            // 1) Carga la lista completa de baños (suspend)
            val allToilets = toiletRepo.getAllToilets()

            // 2) Cada vez que cambian los favoritos, filtra la lista
            favoritesRepo.favoritesFlow
                .map { favIds ->
                    FavoritesUiState(
                        toilets      = allToilets.filter { it.id in favIds },
                        favorites    = favIds,
                        isLoading    = false,
                        errorMessage = null
                    )
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    /** Alterna el estado de favorito de un baño */
    fun onToggleFavorite(toilet: Toilet) {
        viewModelScope.launch {
            favoritesRepo.toggleFavorite(toilet.id)
        }
    }
}
