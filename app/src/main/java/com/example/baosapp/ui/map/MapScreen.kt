// app/src/main/java/com/example/baosapp/ui/map/MapScreen.kt
package com.example.baosapp.ui.map

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baosapp.data.model.toilet.Toilet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier,
    onReviewClick: (Toilet) -> Unit,
    onInfoClick: (Toilet) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Coroutine scope for camera animations
    val coroutineScope = rememberCoroutineScope()

    // Solicitar permisos de ubicación
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }
    val hasLocationPermission = permissionsState.allPermissionsGranted

    // Estado de la cámara del mapa
    val cameraPositionState = rememberCameraPositionState()
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f)
            )
        }
    }

    Column(modifier = modifier) {
        // Mapa
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            uiState.toilets.forEach { toilet ->
                Marker(
                    state = MarkerState(LatLng(toilet.latitude, toilet.longitude)),
                    title = toilet.name,
                    snippet = "★ ${"%.1f".format(toilet.avgRating)}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de baños
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(uiState.toilets) { toilet ->
                var expanded by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(toilet.name, style = MaterialTheme.typography.titleMedium)
                        Text("${"%.1f".format(toilet.avgRating)} ★", style = MaterialTheme.typography.bodyMedium)
                    }

                    if (expanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onReviewClick(toilet) }) {
                                Text("Reseña")
                            }
                            Button(onClick = { onInfoClick(toilet) }) {
                                Text("Información")
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngZoom(
                                            LatLng(toilet.latitude, toilet.longitude),
                                            14f
                                        )
                                    )
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Ubicar en el mapa"
                                )
                            }
                        }
                    }
                }

                Divider()
            }
        }
    }
}
