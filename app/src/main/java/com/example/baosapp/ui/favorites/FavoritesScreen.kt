package com.example.baosapp.ui.favorites

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.baosapp.data.model.toilet.Toilet
import com.example.baosapp.ui.components.ToiletItem

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
    onReviewClick: (Toilet) -> Unit,
    onInfoClick: (Toilet) -> Unit
) {
    val ui by viewModel.uiState.collectAsState()

    if (ui.isLoading) {
        // Mostrar un loader...
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(ui.toilets) { toilet ->
            ToiletItem(
                toilet = toilet,
                isFavorite = toilet.id in ui.favorites,
                onToggleFavorite = { viewModel.onToggleFavorite(it) },
                onReviewClick = onReviewClick,
                onInfoClick = onInfoClick
            )
        }
    }
}
