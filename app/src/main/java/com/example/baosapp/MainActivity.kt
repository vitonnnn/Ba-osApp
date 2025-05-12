package com.example.baosapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.baosapp.data.local.SessionManager
import com.example.baosapp.navigation.AppNavGraph
import com.example.baosapp.ui.theme.BañosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BañosAppTheme {
                Scaffold(
                    topBar = {
                        TopBarView(onClickSignOut = {
                            lifecycleScope.launch {
                                // Borra el token de sesión
                                SessionManager.clearToken(this@MainActivity)
                                // Inicia LoginActivity y limpia el back stack
                                startActivity(
                                    Intent(this@MainActivity, LoginActivity::class.java)
                                        .apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                )
                                // Cierra MainActivity
                                finish()
                            }
                        })
                    },
                    bottomBar = {
                        BottomBarView(onClickItem = {
                            // TODO: manejar pestañas si hiciera falta
                        })
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavGraph()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarView(onClickSignOut: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("BanosApp") },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Ajustes")
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Cerrar sesión") },
                    onClick = {
                        menuExpanded = false
                        onClickSignOut()
                    }
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun BottomBarView(onClickItem: () -> Unit) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = onClickItem) {
                Icon(Icons.Default.Place, contentDescription = "Mapa")
            }
            IconButton(onClick = onClickItem) {
                Icon(Icons.Default.Add, contentDescription = "Añadir baño")
            }
            IconButton(onClick = onClickItem) {
                Icon(Icons.Default.Star, contentDescription = "Reseñas")
            }
        }
    }
}
