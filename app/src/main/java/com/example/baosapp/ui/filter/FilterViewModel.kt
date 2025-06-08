// app/src/main/java/com/example/baosapp/ui/filter/FilterViewModel.kt
package com.example.baosapp.ui.filter

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.repositories.FavoritesRepository
import com.example.baosapp.data.repositories.ToiletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class FilterViewModel(
    private val context: Context
) : ViewModel() {
    private val toiletRepo = ToiletRepository(context)
    private val favoritesRepo = FavoritesRepository(context)

    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState

    init {
        // Cargar IDs de favoritos al inicio
        viewModelScope.launch {
            val favs = favoritesRepo.getFavorites().map { it.id }.toSet()
            _uiState.value = _uiState.value.copy(favoriteIds = favs)
        }
    }

    /**
     * Rota el filtro booleano entre null ↔ true únicamente.
     */
    fun toggleFiltroBoolean(campo: String) {
        val current = _uiState.value
        val nuevaUi = when (campo) {
            "mixto" -> {
                // Si era true -> pasamos a null, si era null -> pasamos a true
                current.copy(mixto = if (current.mixto == true) null else true)
            }
            "accesible" -> {
                current.copy(accesible = if (current.accesible == true) null else true)
            }
            "publico" -> {
                current.copy(publico = if (current.publico == true) null else true)
            }
            "cambioBebes" -> {
                current.copy(cambioBebes = if (current.cambioBebes == true) null else true)
            }
            else -> current
        }
        _uiState.value = nuevaUi
    }

    fun setRadius(value: Float) {
        _uiState.value = _uiState.value.copy(radius = value)
    }

    fun toggleSortOrder() {
        _uiState.value = _uiState.value.copy(sortAsc = !_uiState.value.sortAsc)
    }

    fun buscarCercanos(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val current = _uiState.value
                val sortParam = if (current.sortAsc) "asc" else "desc"
                val list: List<Toilet> = toiletRepo.getNearbyToilets(
                    lat = lat,
                    lon = lon,
                    radius = current.radius.toDouble(),
                    mixto = current.mixto,
                    accesible = current.accesible,
                    publico = current.publico,
                    cambioBebes = current.cambioBebes,
                    sort = sortParam
                )
                _uiState.value = current.copy(isLoading = false, toilets = list)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error desconocido"
                )
            }
        }
    }

    fun autoSearch(lat: Double, lon: Double) {
        buscarCercanos(lat, lon)
    }

    fun toggleFavorite(toilet: Toilet) {
        viewModelScope.launch {
            val currentlyFav = _uiState.value.favoriteIds.contains(toilet.id)
            if (currentlyFav) {
                favoritesRepo.removeFavorite(toilet.id)
            } else {
                favoritesRepo.addFavorite(toilet.id)
            }
            val newSet = _uiState.value.favoriteIds.toMutableSet().apply {
                if (currentlyFav) remove(toilet.id) else add(toilet.id)
            }
            _uiState.value = _uiState.value.copy(favoriteIds = newSet)
        }
    }
}
