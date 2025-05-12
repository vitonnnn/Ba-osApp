// app/src/main/java/com/example/baosapp/ui/favorites/FavoritesViewModelFactory.kt
package com.example.baosapp.ui.favorites

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baosapp.data.local.FavoritesRepository
import com.example.baosapp.data.remote.ToiletsRepository

/**
 * Factory para crear FavoritesViewModel con inyección de Context.
 */
class FavoritesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            val favRepo   = FavoritesRepository(context)
            val toiletRepo = ToiletsRepository()  // tu repositorio de baños
            return FavoritesViewModel(favRepo, toiletRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
