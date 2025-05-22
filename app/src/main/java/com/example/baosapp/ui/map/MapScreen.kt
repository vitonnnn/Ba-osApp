// app/src/main/java/com/example/baosapp/ui/map/MapScreen.kt
package com.example.baosapp.ui.map

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.ui.components.ToiletItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier,
    onToggleFavorite: (Toilet) -> Unit,
    onReviewClick: (Toilet) -> Unit,
    onInfoClick: (Toilet) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Permisos y estado del mapa
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) { permissionsState.launchMultiplePermissionRequest() }
    val hasLocation = permissionsState.allPermissionsGranted

    // Posición inicial
    val cameraPositionState = rememberCameraPositionState()
    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 14f)
            )
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Mapa con marcadores
        GoogleMap(
            modifier            = Modifier
                .fillMaxWidth()
                .height(250.dp),
            cameraPositionState = cameraPositionState,
            properties          = MapProperties(isMyLocationEnabled = hasLocation),
            uiSettings          = MapUiSettings(zoomControlsEnabled = true)
        ) {
            uiState.toilets.forEach { toilet ->
                Marker(
                    state   = MarkerState(LatLng(toilet.latitude, toilet.longitude)),
                    title   = toilet.name,
                    snippet = "★ ${"%.1f".format(toilet.avgRating)}"
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Lista de baños
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(uiState.toilets) { toilet ->
                val isFav = uiState.favoriteIds.contains(toilet.id)
                ToiletItem(
                    toilet           = toilet,
                    isFavorite       = isFav,
                    onToggleFavorite = onToggleFavorite,
                    onLocateClick    = {
                        // centra la cámara en este baño
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(
                                    LatLng(toilet.latitude, toilet.longitude),
                                    14f
                                )
                            )
                        }
                    },
                    onReviewClick    = onReviewClick,
                    onInfoClick      = onInfoClick,
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}
