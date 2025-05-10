// app/src/main/java/com/example/baosapp/ui/shared/SharedToiletViewModel.kt
package com.example.baosapp.ui.shared

import androidx.lifecycle.ViewModel
import com.example.baosapp.data.model.toilet.Toilet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedToiletViewModel : ViewModel() {
    private val _selectedToilet = MutableStateFlow<Toilet?>(null)
    val selectedToilet: StateFlow<Toilet?> = _selectedToilet

    fun select(toilet: Toilet) {
        _selectedToilet.value = toilet
    }
}
