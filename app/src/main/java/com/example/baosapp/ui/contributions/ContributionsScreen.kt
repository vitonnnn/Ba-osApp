// app/src/main/java/com/example/baosapp/ui/contributions/ContributionsScreen.kt
package com.example.baosapp.ui.contributions

import ReviewContributionItem
import ToiletContributionItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContributionsScreen(
    viewModel: ContributionsViewModel,
    modifier: Modifier = Modifier,
    onDeleteToilet: (Long) -> Unit,
    onDeleteReview: (Long, Long) -> Unit
) {
    val ui by viewModel.uiState.collectAsState(initial = ContributionsUiState())

    Box(modifier = modifier.fillMaxSize()) {
        when {
            ui.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ui.errorMessage != null -> {
                Text(
                    text = ui.errorMessage ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            ui.toilets.isEmpty() && ui.reviews.isEmpty() -> {
                Text(
                    text = "No tienes contribuciones.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // — Baños publicados —
                    item {
                        Text(
                            text = "Baños publicados",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                    items(ui.toilets) { toilet ->
                        ToiletContributionItem(
                            toilet = toilet,
                            onDelete = onDeleteToilet,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // — Divider y sección reseñas —
                    item {
                        Divider(
                            color = MaterialTheme.colorScheme.secondary,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        Text(
                            text = "Reseñas publicadas",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                    items(ui.reviews) { review ->
                        ReviewContributionItem(
                            review = review,
                            onDelete = onDeleteReview,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
