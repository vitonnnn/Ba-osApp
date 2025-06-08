// en ui/contributions/ContributionsViewModel.kt
package com.example.baosapp.ui.contributions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.baosapp.data.repositories.ContributionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContributionsViewModel(
    private val repo: ContributionsRepository,
    private val userId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContributionsUiState(isLoading = true))
    val uiState: StateFlow<ContributionsUiState> = _uiState

    init {
        loadContributions()
    }

    private fun loadContributions() {
        viewModelScope.launch {
            _uiState.value = ContributionsUiState(isLoading = true)
            try {
                val resp = repo.getContributions(userId)
                _uiState.value = ContributionsUiState(
                    toilets = resp.toilets,
                    reviews = resp.reviews,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = ContributionsUiState(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun deleteToilet(id: Long) = viewModelScope.launch {
        try {
            repo.deleteToilet(id)
            loadContributions()
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = e.message)
        }
    }

    fun deleteReview(toiletId: Long, reviewId: Long) = viewModelScope.launch {
        try {
            repo.deleteReview(toiletId, reviewId)
            loadContributions()
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = e.message)
        }
    }
}

class ContributionsViewModelFactory(
    private val context: Context,
    private val userId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContributionsViewModel::class.java)) {
            val repo = ContributionsRepository(context)
            return ContributionsViewModel(repo, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
