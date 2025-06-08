// app/src/main/java/com/example/baosapp/ui/filter/FilterUiState.kt
package com.example.baosapp.ui.filter

import com.example.baosapp.data.model.toilet.Toilet

data class FilterUiState(
    val isLoading: Boolean = false,
    val toilets: List<Toilet> = emptyList(),
    val errorMessage: String? = null,

    // Filtros actuales
    val radius: Float = 5f,          // valor inicial en 5 km
    val mixto: Boolean? = null,      // null = “no filtrar”
    val accesible: Boolean? = null,
    val publico: Boolean? = null,
    val cambioBebes: Boolean? = null,

    // Orden actual (ascendente o descendente)
    val sortAsc: Boolean = false,
    val favoriteIds: Set<Long> = emptySet() // para almacenar favoritos
)
