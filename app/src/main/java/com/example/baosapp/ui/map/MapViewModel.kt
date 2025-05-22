// app/src/main/java/com/example/baosapp/ui/map/MapViewModel.kt
package com.example.baosapp.ui.map

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.repositories.FavoritesRepository
import com.example.baosapp.data.repositories.ToiletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    context: Context
) : ViewModel() {

    private val toiletRepo    = ToiletRepository(context)               // tu repo de listToilets()
    private val favoritesRepo = FavoritesRepository(context)

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        loadAll()
    }

    // MapViewModel.kt, añade import android.util.Log
    // app/src/main/java/com/example/baosapp/ui/map/MapViewModel.kt
    private fun loadAll() {
        viewModelScope.launch {
            _uiState.value = MapUiState(isLoading = true)
            try {
                // 1) Cargo todos los baños sí o sí
                val toilets = toiletRepo.getAllToilets()

                // 2) Intento cargar favoritos, pero si falla, asumo lista vacía
                val favIds = try {
                    favoritesRepo.getFavorites().map { it.id }.toSet()
                } catch (e: Exception) {
                    // Solo registro el error, no abortamos la carga de toilets
                    Log.w("MapViewModel", "No se pudieron cargar favoritos, usando vacío", e)
                    emptySet<Long>()
                }

                // 3) Actualizo el estado combinando ambos resultados
                _uiState.value = MapUiState(
                    toilets     = toilets,
                    favoriteIds = favIds,
                    isLoading   = false
                )
            } catch (t: Throwable) {
                // Aquí solo fallará si getAllToilets() lanza, que es más grave
                Log.e("MapViewModel", "Error cargando toilets", t)
                _uiState.value = MapUiState(
                    toilets      = emptyList(),
                    favoriteIds  = emptySet(),
                    errorMessage = t.message,
                    isLoading    = false
                )
            }
        }
    }



    /** Alterna favorito en servidor y en el StateFlow */
    fun toggleFavorite(toilet: Toilet) {
        viewModelScope.launch {
            val currentlyFav = _uiState.value.favoriteIds.contains(toilet.id)
            if (currentlyFav) {
                favoritesRepo.removeFavorite(toilet.id)
            } else {
                favoritesRepo.addFavorite(toilet.id)
            }
            // refrescamos solo el set de IDs en memoria
            val newSet = _uiState.value.favoriteIds.toMutableSet().apply {
                if (currentlyFav) remove(toilet.id) else add(toilet.id)
            }
            _uiState.value = _uiState.value.copy(favoriteIds = newSet)
        }
    }
}
