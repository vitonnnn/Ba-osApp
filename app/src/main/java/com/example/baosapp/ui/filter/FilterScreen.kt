// app/src/main/java/com/example/baosapp/ui/filter/FilterScreen.kt
package com.example.baosapp.ui.filter

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.ui.components.ToiletItem
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun FilterScreen(
    viewModel: FilterViewModel,
    onSelectToilet: (Toilet) -> Unit,
    onBack: () -> Unit,
    onToggleFavorite: (Toilet) -> Unit,
    onReviewClick: (Toilet) -> Unit,
    onInfoClick: (Toilet) -> Unit,
    onLocateClick: (Toilet) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estados para latitud y longitud del usuario
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLon by remember { mutableStateOf<Double?>(null) }
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Cuando la pantalla se monta, pedimos ubicación y lanzamos autoSearch
    LaunchedEffect(Unit) {
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                userLat = it.latitude
                userLon = it.longitude
                // Lanzar búsqueda automática con filtros por defecto (todos null excepto radius)
                viewModel.autoSearch(it.latitude, it.longitude)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1) Cabecera con botón “Atrás”
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
                Text(
                    text = "Filtrar Baños",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // 2) Slider de radio
        item {
            Text(text = "Radio: ${uiState.radius.toInt()} km")
            Slider(
                value = uiState.radius,
                onValueChange = { viewModel.setRadius(it) },
                valueRange = 5f..100f,
                steps = (100 - 5 - 1),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3) Filtros booleanos (solo null vs true)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.mixto == true,
                        onCheckedChange = { viewModel.toggleFiltroBoolean("mixto") }
                    )
                    Text(text = "Mixto", modifier = Modifier.padding(start = 4.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.accesible == true,
                        onCheckedChange = { viewModel.toggleFiltroBoolean("accesible") }
                    )
                    Text(text = "Accesible", modifier = Modifier.padding(start = 4.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.publico == true,
                        onCheckedChange = { viewModel.toggleFiltroBoolean("publico") }
                    )
                    Text(text = "Público", modifier = Modifier.padding(start = 4.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = uiState.cambioBebes == true,
                        onCheckedChange = { viewModel.toggleFiltroBoolean("cambioBebes") }
                    )
                    Text(text = "Cambio bebés", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        // 4) Fila “Ordenar” con flecha ↑/↓
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleSortOrder() }
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Ordenar", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (uiState.sortAsc) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (uiState.sortAsc) "Ascendente" else "Descendente"
                )
            }
        }

        // 5) Botón “Buscar” (ya no estrictamente necesario, pero mantenido por si el usuario cambia filtros)
        item {
            Button(
                onClick = {
                    userLat?.let { lat ->
                        userLon?.let { lon ->
                            viewModel.buscarCercanos(lat, lon)
                        }
                    }
                },
                enabled = (userLat != null && userLon != null),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar")
            }
        }

        // 6) Estado de carga, error o “No hay resultados”
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.errorMessage != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else if (uiState.toilets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron baños con esos filtros.")
                }
            }
        }

        // 7) Lista de resultados (usa ToiletItem y sabrá si es favorito o no)
        items(uiState.toilets) { toilet ->
            val esFavorito = uiState.favoriteIds.contains(toilet.id)
            ToiletItem(
                toilet = toilet,
                isFavorite = esFavorito,
                onToggleFavorite = { onToggleFavorite(toilet) },
                onReviewClick = { onReviewClick(toilet) },
                onInfoClick = { onSelectToilet(toilet) },
                onLocateClick = { onLocateClick(toilet) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
