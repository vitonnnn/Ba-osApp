package com.example.baosapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.model.auth.ResultRegister
import com.example.baosapp.data.repositories.AuthRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para el flujo de registro.
 * Sigue el mismo patrón que LoginViewModel, usando AndroidViewModel.
 */
class RegisterViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Repositorio de autenticación que expone el método register
    private val authRepository = AuthRepository(application)

    // Estado del resultado de registro
    private val _registerResult = MutableLiveData<ResultRegister>()
    val registerResult: LiveData<ResultRegister> = _registerResult

    // Indicador de carga
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Lanza el proceso de registro contra la API.
     * @param username Nombre de usuario
     * @param password Contraseña en texto plano
     * @param email    Email del usuario
     */
    fun register(username: String, password: String, email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            // Llama al repositorio y publica el resultado
            val result = authRepository.register(username, password, email)
            _registerResult.value = result
            _isLoading.value = false
        }
    }
}
