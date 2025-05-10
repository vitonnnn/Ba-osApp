package com.example.baosapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.baosapp.navigation.AppNavGraph
import com.example.baosapp.ui.map.MapScreen
import com.example.baosapp.ui.map.MapViewModel
import com.example.baosapp.ui.theme.BañosAppTheme

class MainActivity : ComponentActivity() {
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            BañosAppTheme {
                Scaffold(
                    topBar = { TopBarView(onClickSignOut = { /* TODO: limpiar sesión */ }) },
                    bottomBar = { BottomBarView(onClickItem = { /* TODO: manejar pestañas */ }) }
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
        title = { Text("BañosApp") },
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
    // Usamos MaterialTheme.colorScheme.primary para el fondo
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