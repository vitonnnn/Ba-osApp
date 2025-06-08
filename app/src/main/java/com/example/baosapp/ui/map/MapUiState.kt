package com.example.baosapp.ui.map

import com.example.baosapp.data.model.toilet.Toilet
import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val toilets: List<Toilet> = emptyList(),
    val userLocation: LatLng? = null,
    val favoriteIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)