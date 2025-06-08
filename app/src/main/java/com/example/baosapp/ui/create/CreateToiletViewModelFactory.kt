package com.example.baosapp.ui.create

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.baosapp.data.repositories.ToiletRepository

/**
 * Factory para inyectar ToiletRepository en CreateToiletViewModel.
 */
class CreateToiletViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateToiletViewModel::class.java)) {
            val repo = ToiletRepository(context)
            return CreateToiletViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
