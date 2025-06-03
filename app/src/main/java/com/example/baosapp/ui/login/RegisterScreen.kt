package com.example.baosapp.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baosapp.data.model.auth.ResultRegister

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context    = LocalContext.current
    val scope      = rememberCoroutineScope()
    var username   by remember { mutableStateOf("") }
    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    val isLoading  by viewModel.isLoading.observeAsState(false)
    val result     by viewModel.registerResult.observeAsState()

    // Validación de email: debe contener '@' y '.com'
    val emailValid = remember(email) { email.contains("@") && email.contains(".com") }

    LaunchedEffect(result) {
        result?.let { res ->
            when (res) {
                is ResultRegister.Success -> {
                    Toast.makeText(context, "Usuario creado: ${res.data.username}", Toast.LENGTH_LONG).show()
                    onRegisterSuccess()
                }
                is ResultRegister.Error -> {
                    Toast.makeText(context, "Error (${res.code}): ${res.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Crear cuenta",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                singleLine = true,
                colors = outlinedTextFieldColors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                    cursorColor          = MaterialTheme.colorScheme.onSecondary,
                    containerColor       = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                isError = email.isNotBlank() && !emailValid,
                colors = outlinedTextFieldColors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                    cursorColor          = MaterialTheme.colorScheme.onSecondary,
                    containerColor       = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            )
            if (email.isNotBlank() && !emailValid) {
                Text(
                    text = "Correo inválido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = outlinedTextFieldColors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                    cursorColor          = MaterialTheme.colorScheme.onSecondary,
                    containerColor       = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.register(username.trim(), password.trim(), email.trim()) },
                enabled = username.isNotBlank() && password.isNotBlank() && emailValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Registrarse")
                }
            }

            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("< Volver al login")
            }
        }
    }
}
