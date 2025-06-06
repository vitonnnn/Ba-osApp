// app/src/main/java/com/example/baosapp/MainActivity.kt
package com.example.baosapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.baosapp.data.local.SessionManager
import com.example.baosapp.navigation.AppNavGraph
import com.example.baosapp.navigation.Destinations
import com.example.baosapp.ui.theme.BañosAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BañosAppTheme {
                // 1) Creamos NavController
                val navController = rememberNavController()

                // 2) Observamos el back stack para extraer la ruta actual
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

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
                            selectedRoute    = currentRoute,
                            onMapClick       = { navController.navigate(Destinations.MAP) },
                            onAddClick       = { navController.navigate(Destinations.CREATE) },
                            onFavoritesClick = { navController.navigate(Destinations.FAVORITES) },
                            onFilterClick    = { navController.navigate(Destinations.FILTER) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarView(
    onClickSignOut: () -> Unit,
    onSearchClick: () -> Unit = {} // Por defecto no hace nada
) {
    var menuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.imagien_sin_fondo),
                contentDescription = "Logo BanosApp",
                modifier = Modifier.height(120.dp),
                contentScale = ContentScale.Fit
            )
        },
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun BottomBarView(
    selectedRoute: String?,
    onMapClick: () -> Unit,
    onAddClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    // Colores para iconos
    val activeIconTint   = Color.White
    val inactiveIconTint = Color.Black
    // Colores para fondo
    val activeBgColor   = MaterialTheme.colorScheme.secondary  // Medium blue
    val inactiveBgColor = Color.Transparent

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
            // Lista de pares (icon, destino, click)
            listOf(
                Triple(Icons.Default.Place, Destinations.MAP, onMapClick),
                Triple(Icons.Default.Add, Destinations.CREATE, onAddClick),
                Triple(Icons.Default.Star, Destinations.FAVORITES, onFavoritesClick),
                Triple(Icons.Outlined.Search, Destinations.FILTER, onFilterClick)
            ).forEach { (icon, destination, onClick) ->
                val isSelected = (selectedRoute == destination)
                // Cada Icono dentro de un Box con fondo redondeado
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) activeBgColor else inactiveBgColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(6.dp)
                ) {
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = icon,
                            contentDescription = destination,
                            tint = if (isSelected) activeIconTint else inactiveIconTint
                        )
                    }
                }
            }
        }
    }
}
