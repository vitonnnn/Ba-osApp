package com.example.baosapp.ui.favorites

import com.example.baosapp.data.model.toilet.Toilet

data class FavoritesUiState(
    val toilets: List<Toilet> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)