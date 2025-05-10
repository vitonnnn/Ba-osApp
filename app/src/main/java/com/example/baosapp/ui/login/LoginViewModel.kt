// app/src/main/java/com/example/baosapp/ui/login/LoginViewModel.kt
package com.example.baosapp.ui.login

// app/src/main/java/com/example/baosapp/ui/login/LoginViewModel.kt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.repositories.AuthRepository
import com.example.baosapp.data.result.ResultLogin
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

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
