// app/src/main/java/com/example/baosapp/ui/review/RateBathroomViewModelFactory.kt
package com.example.baosapp.ui.review

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baosapp.data.repositories.ToiletRepository

class RateBathroomViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RateBathroomViewModel::class.java)) {
            // construimos el repo con el contexto
            val repo = ToiletRepository(context)
            return RateBathroomViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
