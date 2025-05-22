// app/src/main/java/com/example/baosapp/ui/review/RateBathroomScreen.kt
package com.example.baosapp.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baosapp.data.model.review.ReviewRequest
import kotlinx.coroutines.flow.collectLatest
import java.lang.reflect.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateBathroomScreen(
    toiletId: Long,
    toiletName: String,
    onSubmitSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RateBathroomViewModel = viewModel(
        factory = RateBathroomViewModelFactory(LocalContext.current)
    )
) {
    val rating       by viewModel.rating.collectAsState()
    val limpieza     by viewModel.limpieza.collectAsState()
    val olor         by viewModel.olor.collectAsState()
    val comentario   by viewModel.comment.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()

    // Cuando la review se sube con éxito, volvemos atrás
    LaunchedEffect(Unit) {
        viewModel.submissionSuccess.collectLatest { onSubmitSuccess() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text  = "Valorar baño: $toiletName",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text  = "Valoración general",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        RatingRow(current = rating, onRatingSelected = viewModel::setRating)

        Text(
            text  = "Limpieza",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        RatingRow(current = limpieza, onRatingSelected = viewModel::setLimpieza)

        Text(
            text  = "Olor",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        RatingRow(current = olor, onRatingSelected = viewModel::setOlor)

        OutlinedTextField(
            value         = comentario,
            onValueChange = viewModel::setComment,
            textStyle     = TextStyle(color = Color.Black),
            placeholder   = {
                Text(
                    text  = "Comentario (opcional)",
                    color = Color.Black.copy(alpha = ContentAlpha.medium)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color.Black
            )
        )

        Button(
            onClick = {
                viewModel.submitReview(
                    toiletId,
                    ReviewRequest(
                        valoracion = rating,
                        limpieza   = limpieza,
                        olor       = olor,
                        comment    = comentario
                    )
                )
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
                    contentDescription = null,
                    tint = if (star <= current)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
