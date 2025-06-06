// app/src/main/java/com/example/baosapp/ui/create/CreateToiletScreen.kt
package com.example.baosapp.ui.create

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // 1) Pedir permisos de ubicación
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

    // 2) FusedLocationProviderClient para última ubicación
    val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // 3) Estado para la posición de la cámara (mapa)
    var cameraPositionState by remember {
        mutableStateOf(
            CameraPositionState(
                position = CameraPosition.fromLatLngZoom(
                    LatLng(0.0, 0.0), 15f
                )
            )
        )
    }

    // 4) Cuando tenemos permiso, obtenemos última ubicación y centramos el mapa
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && uiState.latitude == null) {
            fusedClient.lastLocation
                .addOnSuccessListener { loc: Location? ->
                    loc?.let {
                        viewModel.onLocationObtained(it.latitude, it.longitude)
                        val pos = LatLng(it.latitude, it.longitude)
                        cameraPositionState = CameraPositionState(
                            position = CameraPosition.fromLatLngZoom(pos, 15f)
                        )
                    }
                }
        }
    }

    // 5) Detectar creación exitosa
    if (uiState.success) {
        LaunchedEffect(Unit) {
            viewModel.resetSuccess()
            Toast.makeText(context, "Baño creado correctamente", Toast.LENGTH_SHORT).show()
            onCreated()
            onCloseSheet()
        }
    }

    // 6) Mostrar Toast de error si existe
    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // 7) Composición principal sin usar LazyColumn, para que el mapa no capture scroll externo
    Column(
        modifier = modifier
            .fillMaxWidth()
             // Solo section abajo, el mapa no se mueve con esto
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Arrastrador visual

        // Campo de nombre
        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = { viewModel.onNombreChange(it) },
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

        // -------------------------
        // 8) Mapa con tamaño fijo (no dentro del scroll)
        // -------------------------
        if (hasLocationPermission) {
            val currentLatLng = uiState.latitude?.let { lat ->
                uiState.longitude?.let { lng ->
                    LatLng(lat, lng)
                }
            }

            // Box de altura fija para el mapa, sin que el scroll mueva el mapa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = false
                    ),
                    properties = MapProperties(isMyLocationEnabled = true),
                    onMapClick = { latLng ->
                        viewModel.onLocationObtained(latLng.latitude, latLng.longitude)
                        // Animate cámara en coroutine
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

            // Mostrar coords debajo del mapa
            currentLatLng?.let { pos ->
                Text(
                    text = "Ubicación: ${"%.5f".format(pos.latitude)}, ${"%.5f".format(pos.longitude)}",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } ?: run {
                Text(
                    text = "Toca el mapa para seleccionar ubicación.",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        } else {
            Text(
                text = "Permite ubicación para seleccionar posición en mapa",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // 9) Switches de atributos (estas filas quedan dentro del scroll)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Accesible", modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.accesible,
                onCheckedChange = { viewModel.onAccesibleChange(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Público", modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.publico,
                onCheckedChange = { viewModel.onPublicoChange(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Mixto", modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.mixto,
                onCheckedChange = { viewModel.onMixtoChange(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Cambio bebés", modifier = Modifier.weight(1f))
            Switch(
                checked = uiState.cambioBebes,
                onCheckedChange = { viewModel.onCambioBebesChange(it) },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 10) Botón Crear baño
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

        // 11) Botón Cerrar sheet
        TextButton(
            onClick = onCloseSheet,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("< Cancelar")
        }
    }
}
