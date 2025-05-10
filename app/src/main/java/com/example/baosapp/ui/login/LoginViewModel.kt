// app/src/main/java/com/example/baosapp/ui/login/LoginViewModel.kt
package com.example.baosapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miempresa.appbanos.data.model.auth.LoginResponse
import com.miempresa.appbanos.data.model.auth.User
import com.example.baosapp.data.result.ResultLogin

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<ResultLogin>()
    val loginResult: LiveData<ResultLogin> = _loginResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        // Simulamos siempre éxito inmediato
        _isLoading.value = false

        // Creamos un usuario de prueba
        val dummyUser = User(
            id = 1,
            name = username,
            email = "$username@example.com"
        )

        // Construimos la respuesta simulada
        val response = LoginResponse(user = dummyUser)

        // Publicamos el resultado como exitoso
        _loginResult.value = ResultLogin(
            data = response,
            code = 200,
            message = "Login simulado exitoso"
        )
    }
}
