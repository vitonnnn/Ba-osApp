import com.example.baosapp.data.model.review.ReviewResponse
import com.example.baosapp.data.model.toilet.ToiletResponse

// en data/model/ContributionsResponse.kt
data class ContributionsResponse(
    val toilets: List<ToiletResponse>,
    val reviews: List<ReviewResponse>
)
