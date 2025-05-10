// app/src/main/java/com/example/baosapp/ui/information/InformationViewModel.kt
package com.example.baosapp.ui.information

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.baosapp.data.model.toilet.Toilet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// InformationViewModel.kt
class InformationViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val selected: Toilet? = savedStateHandle.get<Toilet>("selectedToilet")
    private val _toilet = MutableStateFlow(selected)
    val toilet: StateFlow<Toilet?> = _toilet
}
