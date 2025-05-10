// app/src/main/java/com/example/baosapp/ui/map/MapScreen.kt
package com.example.baosapp.ui.map

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baosapp.data.model.toilet.Toilet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier,
    onReviewClick: (Toilet) -> Unit,
    onInfoClick: (Toilet) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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

    val cameraPositionState = rememberCameraPositionState()
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f)
            )
        }
    }

    Column(modifier = modifier) {
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
                    snippet = "★ ${toilet.avgRating}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                        }
                    }
                }

                Divider()
            }
        }
    }
}

@Composable
private fun ToiletItem(
    toilet: Toilet,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(toilet.name, style = MaterialTheme.typography.titleMedium)
            Text(
                "(${String.format("%.5f", toilet.latitude)}, ${String.format("%.5f", toilet.longitude)})",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            "${String.format("%.1f", toilet.avgRating)} ★",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
