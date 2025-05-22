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
import androidx.navigation.compose.rememberNavController                     // ← importar
import androidx.navigation.NavHostController                              // ← importar
import com.example.baosapp.data.local.SessionManager
import com.example.baosapp.navigation.AppNavGraph
import com.example.baosapp.navigation.Destinations                        // ← importar tus constantes de ruta
import com.example.baosapp.ui.theme.BañosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BañosAppTheme {
                // Creamos el NavController una sola vez
                val navController = rememberNavController()

                Scaffold(
                    topBar = {
                        TopBarView(onClickSignOut = {
                            lifecycleScope.launch {
                                SessionManager.clearToken(this@MainActivity)
                                startActivity(
                                    Intent(this@MainActivity, LoginActivity::class.java)
                                        .apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                )
                                finish()
                            }
                        })
                    },
                    bottomBar = {
                        BottomBarView(
                            onMapClick = {
                                navController.navigate(Destinations.MAP)
                            },
                            onAddClick = {
                                // si tienes ruta para "añadir baño", por ejemplo:
                                // navController.navigate(Destinations.ADD_BATHROOM)
                            },
                            onFavoritesClick = {
                                navController.navigate(Destinations.FAVORITES)
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Le pasamos el controller a nuestro NavGraph
                        AppNavGraph(navController = navController)
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
fun BottomBarView(
    onMapClick: () -> Unit,
    onAddClick: () -> Unit,
    onFavoritesClick: () -> Unit
) {
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
            IconButton(onClick = onMapClick) {
                Icon(Icons.Default.Place, contentDescription = "Mapa")
            }
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir baño")
            }
            IconButton(onClick = onFavoritesClick) {
                Icon(Icons.Default.Star, contentDescription = "Favoritos")
            }
        }
    }
}
