package com.example.baosapp.ui.favorites

import com.example.baosapp.data.model.toilet.Toilet

data class FavoritesUiState(
    val toilets: List<Toilet> = emptyList(),
    val favorites: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

