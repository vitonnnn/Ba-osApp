// app/src/main/java/com/example/baosapp/ui/map/MapViewModel.kt
package com.example.baosapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.toilet.Toilet
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        // Simula carga de datos
        viewModelScope.launch {
            try {
                delay(1_000) // simulamos fetch
                val sampleToilets = listOf(
                    Toilet(
                        id = 1L,
                        name = "Baño Plaza",
                        latitude = 40.54321,
                        longitude = -3.12345,
                        avgRating = 4.2,
                        accesible = true,
                        publico = true,
                        mixto = false,
                        cambioBebes = true
                    ),
                    Toilet(
                        id = 2L,
                        name = "WC Centro",
                        latitude = 40.54400,
                        longitude = -3.12200,
                        avgRating = 3.8,
                        accesible = false,
                        publico = true,
                        mixto = true,
                        cambioBebes = false
                    )
                )
                _uiState.value = MapUiState(
                    toilets      = sampleToilets,
                    userLocation = LatLng(40.54350, -3.12300),
                    isLoading    = false
                )
            } catch(e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading    = false,
                    errorMessage = e.message
                )
            }
        }
    }
}
