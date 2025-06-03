// app/src/main/java/com/example/baosapp/ui/create/CreateToiletScreen.kt
package com.example.baosapp.ui.create

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

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

    // 2) FusedLocationProviderClient para obtener coords
    val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission && uiState.latitude == null) {
            fusedClient.lastLocation
                .addOnSuccessListener { loc ->
                    loc?.let {
                        viewModel.onLocationObtained(it.latitude, it.longitude)
                    }
                }
        }
    }

    // 3) Detectar éxito y cerrar bottom sheet
    if (uiState.success) {
        LaunchedEffect(Unit) {
            viewModel.resetSuccess()
            Toast.makeText(context, "Baño creado correctamente", Toast.LENGTH_SHORT).show()
            onCreated()
            onCloseSheet()
        }
    }

    // 4) Mostrar Toast en caso de error
    uiState.errorMessage?.let { msg ->
        LaunchedEffect(msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // 5) UI dentro del bottom sheet
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Arrastrador visual
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 4.dp)
                .size(width = 40.dp, height = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Text(
            text = "Crear nuevo baño",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Nombre
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

        // Coordenadas o mensaje
        if (uiState.latitude != null && uiState.longitude != null) {
            Text(
                text = "Ubicación: ${"%.5f".format(uiState.latitude)}, ${"%.5f".format(uiState.longitude)}",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else {
            Text(
                text = if (hasLocationPermission)
                    "Obteniendo ubicación..."
                else
                    "Permite ubicación para crear baño",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Switch Accesible
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

        // Switch Público
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

        // Switch Mixto
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

        // Switch Cambio bebés
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

        // Botón Crear baño
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

        // Botón Cerrar Sheet
        TextButton(
            onClick = onCloseSheet,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("< Cancelar")
        }
    }
}
