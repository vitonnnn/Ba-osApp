// app/src/main/java/com/example/baosapp/ui/filter/FilterViewModelFactory.kt
package com.example.baosapp.ui.filter

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FilterViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
            return FilterViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
