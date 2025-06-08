// app/src/main/java/com/example/baosapp/ui/information/InformationViewModelFactory.kt
package com.example.baosapp.ui.information

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InformationViewModelFactory(
    private val app: Application,
    private val toiletId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InformationViewModel::class.java)) {
            return InformationViewModel(app, toiletId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
