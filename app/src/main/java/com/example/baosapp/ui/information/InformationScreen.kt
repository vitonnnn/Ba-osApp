// app/src/main/java/com/example/baosapp/ui/information/InformationScreen.kt
package com.example.baosapp.ui.information

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baosapp.data.model.toilet.Toilet

@Composable
fun InformationScreen(
    toilet: Toilet?,
    modifier: Modifier = Modifier
) {
    // Si no se ha seleccionado ningún baño, mostramos un mensaje y salimos
    if (toilet == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No se ha seleccionado ningún baño.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    // Obtenemos el ViewModel pasándole el toilet.id como parámetro
    val context = LocalContext.current
    val viewModel: InformationViewModel = viewModel(
        factory = InformationViewModelFactory(
            context.applicationContext as Application,
            toilet.id
        )
    )

    // Observamos el estado UI desde el ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Información básica del baño
        Text(
            text = "• Nombre: ${toilet.name}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "• Accesible: ${if (toilet.accesible) "Sí" else "No"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "• Público: ${if (toilet.publico) "Sí" else "No"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "• Mixto: ${if (toilet.mixto) "Sí" else "No"}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "• Cambio bebés: ${if (toilet.cambioBebes) "Sí" else "No"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Título de la sección de reseñas
        Text(
            text = "Reseñas:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mostramos estado de carga, error o lista de reseñas
        when {
            uiState.isLoading -> {
                // Indicador de carga centrado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                // Mostrar mensaje de error
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            uiState.reviews.isEmpty() -> {
                // No hay reseñas aún
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay reseñas para este baño.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            else -> {
                // Lista de reseñas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.reviews) { reviewUi ->
                        ReviewItem(uiModel = reviewUi)
                    }
                }
            }
        }
    }
}
