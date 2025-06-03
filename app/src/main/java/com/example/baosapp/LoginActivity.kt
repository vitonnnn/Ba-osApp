// app/src/main/java/com/example/baosapp/LoginActivity.kt
package com.example.baosapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baosapp.ui.login.*
import com.example.baosapp.ui.theme.BañosAppTheme

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BañosAppTheme {
                // Controla si mostramos login o register
                var showRegister by remember { mutableStateOf(false) }

                if (showRegister) {
                    // Pantalla de registro
                    val registerVm: RegisterViewModel = viewModel()
                    RegisterScreen(
                        viewModel         = registerVm,
                        onRegisterSuccess = {
                            // Tras registrarse, volvemos al login
                            showRegister = false
                        },
                        onBackToLogin     = {
                            showRegister = false
                        }
                    )
                } else {
                    // Pantalla de login
                    val loginVm: LoginViewModel = viewModel()
                    LoginScreen(
                        viewModel            = loginVm,
                        onLoginSuccess       = {
                            // Al hacer login correcto, lanzamos MainActivity
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        },
                        onNavigateToRegister = {
                            showRegister = true
                        }
                    )
                }
            }
        }
    }
}
