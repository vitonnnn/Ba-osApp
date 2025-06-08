// app/src/main/java/com/example/baosapp/ui/create/CreateToiletScreen.kt
package com.example.baosapp.ui.create

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baosapp.ui.map.MapViewModel
import com.example.baosapp.ui.map.MapViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateToiletScreen(

    viewModel: CreateToiletViewModel,
    onCreated: () -> Unit,
    onCloseSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mapViewModel: MapViewModel = viewModel(factory = MapViewModelFactory(LocalContext.current))

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Permisos de ubicación
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

    val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var cameraPositionState by remember {
        mutableStateOf(
            CameraPositionState(
                position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
            )
        )
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && uiState.latitude == null) {
            fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                loc?.let {
                    viewModel.onLocationObtained(it.latitude, it.longitude)
                    cameraPositionState = CameraPositionState(
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15f
                        )
                    )
                }
            }
        }
    }

    if (uiState.success) {
        LaunchedEffect(Unit) {
            viewModel.resetSuccess()
            Toast.makeText(context, "Baño creado correctamente", Toast.LENGTH_SHORT).show()
            mapViewModel.loadAll()  // ✅ ejecuta la recarga directamente
            onCreated()             // ✅ notifica que fue creado (si quieres hacer algo más)
            onCloseSheet()          // ✅ cierra el sheet
        }
    }

    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = viewModel::onNombreChange,
            label = { Text("Nombre del baño") },
            singleLine = true,
            colors = outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary,
                cursorColor = MaterialTheme.colorScheme.onSecondary,
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        )

        if (hasLocationPermission) {
            val currentLatLng = uiState.latitude?.let { lat ->
                uiState.longitude?.let { lng -> LatLng(lat, lng) }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = true),
                    properties = MapProperties(isMyLocationEnabled = true),
                    onMapClick = { latLng ->
                        viewModel.onLocationObtained(latLng.latitude, latLng.longitude)
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLng(latLng),
                                durationMs = 800
                            )
                        }
                    }
                ) {
                    currentLatLng?.let { pos ->
                        Marker(
                            state = MarkerState(pos),
                            title = uiState.nombre.ifBlank { "Ubicación seleccionada" }
                        )
                    }
                }
            }

            currentLatLng?.let { pos ->
                Text(
                    text = "Ubicación: ${"%.5f".format(pos.latitude)}, ${"%.5f".format(pos.longitude)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } ?: Text(
                text = "Toca el mapa para seleccionar ubicación.",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else {
            Text(
                text = "Permite ubicación para seleccionar posición en mapa",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        AttributeSwitch("Accesible", uiState.accesible, viewModel::onAccesibleChange)
        AttributeSwitch("Público", uiState.publico, viewModel::onPublicoChange)
        AttributeSwitch("Mixto", uiState.mixto, viewModel::onMixtoChange)
        AttributeSwitch("Cambio bebés", uiState.cambioBebes, viewModel::onCambioBebesChange)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.createToilet() },
            enabled = uiState.nombre.isNotBlank()
                    && uiState.latitude != null
                    && uiState.longitude != null
                    && !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Crear baño")
            }
        }

        TextButton(
            onClick = onCloseSheet,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("< Cancelar")
        }
    }
}

@Composable
private fun AttributeSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
