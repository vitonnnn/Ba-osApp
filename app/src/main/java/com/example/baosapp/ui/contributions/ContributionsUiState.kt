// en ui/contributions/ContributionsUiState.kt
package com.example.baosapp.ui.contributions

import com.example.baosapp.data.model.toilet.ToiletResponse
import com.example.baosapp.data.model.review.ReviewResponse

data class ContributionsUiState(
    val toilets: List<ToiletResponse> = emptyList(),
    val reviews: List<ReviewResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
