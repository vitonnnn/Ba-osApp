// app/src/main/java/com/example/baosapp/ui/favorites/FavoritesViewModelFactory.kt
package com.example.baosapp.ui.favorites

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baosapp.data.repositories.FavoritesRepository

class FavoritesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            val repo = FavoritesRepository(context)
            return FavoritesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
