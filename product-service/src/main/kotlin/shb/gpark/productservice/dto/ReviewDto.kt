package shb.gpark.productservice.dto

import java.time.LocalDateTime

data class CreateReviewRequest(
    val userId: Long,
    val rating: Int,
    val content: String
)

data class ReviewResponse(
    val id: Long,
    val productId: Long,
    val userId: Long,
    val rating: Int,
    val content: String,
    val createdAt: LocalDateTime
)

data class ProductDetailResponse(
    val id: Long,
    val name: String,
    val price: java.math.BigDecimal,
    val stock: Int,
    val isActive: Boolean,
    val averageRating: Double,
    val reviewCount: Int,
    val reviews: List<ReviewResponse>
) 