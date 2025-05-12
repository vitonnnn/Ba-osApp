package com.example.baosapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.baosapp.ui.login.LoginScreen
import com.example.baosapp.ui.login.LoginViewModel
import com.example.baosapp.ui.theme.BañosAppTheme

class LoginActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BañosAppTheme {
                LoginScreen(
                    viewModel      = viewModel,
                    onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
