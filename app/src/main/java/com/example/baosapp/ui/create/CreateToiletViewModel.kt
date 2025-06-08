// app/src/main/java/com/example/baosapp/ui/create/CreateToiletViewModel.kt
package com.example.baosapp.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.repositories.ToiletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateToiletViewModel(
    private val repo: ToiletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateToiletUiState())
    val uiState: StateFlow<CreateToiletUiState> = _uiState

    /** Actualiza el nombre del baño */
    fun onNombreChange(new: String) {
        _uiState.value = _uiState.value.copy(nombre = new)
    }

    /** Actualiza latitud/longitud obtenidas */
    fun onLocationObtained(lat: Double, lng: Double) {
        _uiState.value = _uiState.value.copy(latitude = lat, longitude = lng)
    }

    fun onAccesibleChange(v: Boolean) {
        _uiState.value = _uiState.value.copy(accesible = v)
    }
    fun onPublicoChange(v: Boolean) {
        _uiState.value = _uiState.value.copy(publico = v)
    }
    fun onMixtoChange(v: Boolean) {
        _uiState.value = _uiState.value.copy(mixto = v)
    }
    fun onCambioBebesChange(v: Boolean) {
        _uiState.value = _uiState.value.copy(cambioBebes = v)
    }

    /** Lanza la petición para crear un nuevo baño */
    fun createToilet() {
        val s = _uiState.value
        if (s.nombre.isBlank() || s.latitude == null || s.longitude == null) {
            _uiState.value = s.copy(errorMessage = "Nombre y ubicación son obligatorios")
            return
        }

        _uiState.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repo.createToilet(
                    name = s.nombre,
                    latitude = s.latitude,
                    longitude = s.longitude,
                    accesible = s.accesible,
                    publico = s.publico,
                    mixto = s.mixto,
                    cambioBebes = s.cambioBebes
                )
                _uiState.value = _uiState.value.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error desconocido"
                )
            }
        }
    }

    /** Reinicia el flag de éxito para volver a crear si es necesario */
    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }

    /** Limpia cualquier mensaje de error previo */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}


