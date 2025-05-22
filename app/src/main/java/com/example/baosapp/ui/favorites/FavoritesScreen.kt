// app/src/main/java/com/example/baosapp/ui/favorites/FavoritesScreen.kt
package com.example.baosapp.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.ui.components.ToiletItem

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
    onLocateClick: (Toilet) -> Unit,
    onReviewClick: (Toilet) -> Unit,
    onInfoClick: (Toilet) -> Unit
) {
    val ui by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            ui.isLoading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            ui.errorMessage != null -> {
                Text(
                    text = ui.errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ui.toilets.isEmpty() -> {
                Text(
                    text = "No tienes baños en favoritos.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(ui.toilets) { toilet ->
                        ToiletItem(
                            toilet           = toilet,
                            isFavorite       = true,
                            onToggleFavorite = { viewModel.onToggleFavorite(it) },
                            onLocateClick    = onLocateClick,
                            onReviewClick    = onReviewClick,
                            onInfoClick      = onInfoClick,
                            modifier         = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
