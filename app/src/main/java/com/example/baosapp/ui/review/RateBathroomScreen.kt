// app/src/main/java/com/example/baosapp/ui/review/RateBathroomScreen.kt
package com.example.baosapp.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateBathroomScreen(
    nombreBano: String,
    viewModel: RateBathroomViewModel,
    onSubmitSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rating       by viewModel.rating.collectAsState()
    val limpieza     by viewModel.limpieza.collectAsState()
    val olor         by viewModel.olor.collectAsState()
    val comment      by viewModel.comment.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    // Observa el evento de éxito
    LaunchedEffect(Unit) {
        viewModel.submissionSuccess.collectLatest {
            onSubmitSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text("Valorar baño: $nombreBano", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))

        Text("Valoración general", style = MaterialTheme.typography.titleMedium)
        RatingRow(current = rating, onRatingSelected = viewModel::setRating)

        Text("Limpieza", style = MaterialTheme.typography.titleMedium)
        RatingRow(current = limpieza, onRatingSelected = viewModel::setLimpieza)

        Text("Olor", style = MaterialTheme.typography.titleMedium)
        RatingRow(current = olor, onRatingSelected = viewModel::setOlor)

        Text("Comentario", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = comment,
            onValueChange = viewModel::setComment,
            placeholder = { Text("Escribe tu opinión...", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            textStyle = TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor          = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = false
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.submitReview(nombreBano) },
            enabled = rating > 0 && limpieza > 0 && olor > 0 && !isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
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
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (1..5).forEach { star ->
            IconButton(onClick = { onRatingSelected(star) }) {
                if (star <= current) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "$star estrellas",
                        tint = Color.Black
                    )
                } else {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = "$star estrellas",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
