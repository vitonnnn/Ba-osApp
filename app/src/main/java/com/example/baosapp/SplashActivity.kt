package com.example.baosapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.baosapp.data.local.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import android.util.Base64
import org.json.JSONObject

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Leer token (bloqueante, antes de inflar UI)
        val token = runBlocking {
            SessionManager.getToken(this@SplashActivity).firstOrNull()
        }

        // 2) Decidir destino
        val target = if (token.isNullOrEmpty() || isTokenExpired(token))
            LoginActivity::class.java
        else
            MainActivity::class.java

        startActivity(Intent(this, target))
        finish()
    }

    private fun isTokenExpired(jwt: String): Boolean {
        return try {
            val parts = jwt.split(".")
            if (parts.size < 2) return true
            val payload = parts[1]
                .padEnd(parts[1].length + (4 - parts[1].length % 4) % 4, '=')
            val json    = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP))
            val exp     = JSONObject(json).optLong("exp", 0L)
            (System.currentTimeMillis() / 1000) >= exp
        } catch (_: Exception) {
            true
        }
    }
}
