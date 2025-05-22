// app/src/main/java/com/example/baosapp/ui/login/LoginViewModel.kt
package com.example.baosapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.repositories.AuthRepository
import com.example.baosapp.data.result.ResultLogin
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Ahora usa directamente el Application como Context
    private val authRepository = AuthRepository(application)

    private val _loginResult = MutableLiveData<ResultLogin>()
    val loginResult: LiveData<ResultLogin> = _loginResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Lanza el proceso de login contra la API.
     * @param username Nombre de usuario
     * @param password Contraseña en texto plano
     */
    fun login(username: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(username, password)
            _loginResult.value = result
            _isLoading.value = false
        }
    }
}
