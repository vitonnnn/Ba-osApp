// app/src/main/java/com/example/baosapp/ui/map/MapViewModel.kt
package com.example.baosapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.data.remote.ToiletsRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val repo: ToiletsRepository = ToiletsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                // Carga real de la API
                val toilets = repo.getAllToilets()
                _uiState.value = MapUiState(
                    toilets      = toilets,
                    userLocation = LatLng(40.5667584, -3.9280926), // o desde FusedLocation
                    isLoading    = false
                )
            } catch(e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading    = false,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }
}

