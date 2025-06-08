// app/src/main/java/com/example/baosapp/ui/login/LoginViewModel.kt
package com.example.baosapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.local.SessionManager
import com.example.baosapp.data.repositories.AuthRepository
import com.example.baosapp.data.result.ResultLogin
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Repositorio de autenticación que usa Retrofit + DataStore
    private val authRepository = AuthRepository(application)

    // Estado del resultado de login
    private val _loginResult = MutableLiveData<ResultLogin>()
    val loginResult: LiveData<ResultLogin> = _loginResult

    // Estado de carga para deshabilitar botones en UI
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Lanza el proceso de login contra la API, guarda token y username
     * en DataStore al tener éxito, y emite el resultado a la UI.
     */
    fun login(username: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            // Petición al servidor
            val result = authRepository.login(username, password)
            if (result is ResultLogin.Success) {
                // 1) Guardamos el JWT para futuras llamadas
                SessionManager.saveToken(
                    getApplication<Application>(),
                    result.data.access_token
                )
                SessionManager.saveUserId(
                    getApplication(),
                    result.data.user.id.toLong()
                )
            }
            // Emitimos el resultado (éxito o error) para que la UI reaccione
            _loginResult.value = result
            _isLoading.value = false
        }
    }
}
