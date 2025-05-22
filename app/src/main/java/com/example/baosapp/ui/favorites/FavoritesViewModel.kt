// app/src/main/java/com/example/baosapp/ui/favorites/FavoritesViewModel.kt
package com.example.baosapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.repositories.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class FavoritesViewModel(
    private val repo: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState(isLoading = true))
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState(isLoading = true)
            try {
                val list = repo.getFavorites()
                _uiState.value = FavoritesUiState(toilets = list, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = FavoritesUiState(
                    toilets      = emptyList(),
                    isLoading    = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun onToggleFavorite(toilet: Toilet) {
        viewModelScope.launch {
            try {
                // borra en servidor
                repo.toggleFavorite(toilet, currentlyFavorite = true)
                // filtramos de la lista local
                val updated = _uiState.value.toilets.filterNot { it.id == toilet.id }
                _uiState.value = _uiState.value.copy(toilets = updated)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }
}
