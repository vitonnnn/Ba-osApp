// app/src/main/java/com/example/baosapp/ui/information/InformationScreen.kt
package com.example.baosapp.ui.information

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

    // Crear ViewModel con Factory
    val context = LocalContext.current.applicationContext as android.app.Application
    val factory = InformationViewModelFactory(app = context, toiletId = toilet.id)
    val infoVm: InformationViewModel = viewModel(factory = factory)

    // Observar uiState
    val uiState by infoVm.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ----- Información estática del baño -----
        Text(
            text = "Información del baño",
            style = MaterialTheme.typography.titleLarge
        )
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

        Spacer(modifier = Modifier.height(16.dp))

        // ----- Encabezado “Reseñas” -----
        Text(
            text = "Reseñas",
            style = MaterialTheme.typography.headlineSmall
        )

        // ----- Contenido dinámico de reseñas -----
        when {
            uiState.isLoading -> {
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
                Text(
                    text = "Error al cargar reseñas: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            uiState.reviews.isEmpty() -> {
                Text(
                    text = "No hay reseñas para este baño.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {
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
