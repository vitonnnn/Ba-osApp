// app/src/main/java/com/example/baosapp/ui/review/RateBathroomScreen.kt
package com.example.baosapp.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import com.example.baosapp.data.model.review.ReviewRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateBathroomScreen(
    toiletId: Long,                          // <-- ahora recibimos el ID
    toiletName: String,
    viewModel: RateBathroomViewModel = viewModel(),
    onSubmitSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados del ViewModel
    val rating       by viewModel.rating.collectAsState()
    val limpieza     by viewModel.limpieza.collectAsState()
    val olor         by viewModel.olor.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    // Comentario lo guardamos localmente y luego lo pasamos al request
    var comentario by remember { mutableStateOf("") }

    // Observa el éxito de la petición y navega atrás
    LaunchedEffect(Unit) {
        viewModel.submissionSuccess.collectLatest {
            onSubmitSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Valorar baño: $toiletName", style = MaterialTheme.typography.titleLarge)

        Text("Valoración general", style = MaterialTheme.typography.titleMedium)
        RatingRow(current = rating, onRatingSelected = viewModel::setRating)

        Text("Limpieza", style = MaterialTheme.typography.titleMedium)
        RatingRow(current = limpieza, onRatingSelected = viewModel::setLimpieza)

        Text("Olor", style = MaterialTheme.typography.titleMedium)
        RatingRow(current = olor, onRatingSelected = viewModel::setOlor)

        OutlinedTextField(
            value = comentario,
            onValueChange = { comentario = it },
            label = { Text("Comentario (opcional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                // Construye el request y pasa ambos parámetros
                val request = ReviewRequest(
                    rating  = rating.toFloat(),
                    comment = comentario
                )
                viewModel.submitReview(toiletId, request)
            },
            enabled = rating > 0 && limpieza > 0 && olor > 0 && !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Enviar valoración")
            }
        }
    }
}

@Composable
private fun RatingRow(
    current: Int,
    onRatingSelected: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        (1..5).forEach { star ->
            IconButton(onClick = { onRatingSelected(star) }) {
                Icon(
                    imageVector = if (star <= current) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null
                )
            }
        }
    }
}
