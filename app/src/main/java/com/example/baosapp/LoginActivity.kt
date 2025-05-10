// app/src/main/java/com/example/baosapp/ui/login/LoginActivity.kt
package com.example.baosapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.baosapp.MainActivity
import com.example.baosapp.ui.login.LoginScreen
import com.example.baosapp.ui.theme.BañosAppTheme

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BañosAppTheme  {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LoginScreen(
                        onLoginSuccess = { navigateToMain() }
                    )
                }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()  // Evita volver al login con el botón atrás
    }
}
