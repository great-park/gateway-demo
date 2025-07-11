package shb.gpark.productservice.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.beans.factory.annotation.Autowired
import shb.gpark.productservice.repository.ProductRepository
import shb.gpark.productservice.repository.ReviewRepository
import shb.gpark.productservice.dto.CreateReviewRequest
import shb.gpark.productservice.dto.ReviewResponse
import shb.gpark.productservice.dto.ProductDetailResponse
import shb.gpark.productservice.model.Product
import shb.gpark.productservice.model.Review
import java.math.BigDecimal
import java.time.LocalDateTime

@SpringBootTest
class ProductServiceTest {
    @Autowired
    lateinit var productService: ProductService

    @MockBean
    lateinit var productRepository: ProductRepository

    @MockBean
    lateinit var reviewRepository: ReviewRepository

    @Test
    fun `리뷰 생성 성공`() {
        // given
        val productId = 1L
        val product = Product(
            id = productId,
            name = "테스트 상품",
            price = BigDecimal.TEN,
            stock = 10,
            isActive = true
        )
        val request = CreateReviewRequest(
            userId = 1L,
            rating = 5,
            content = "좋은 상품입니다!"
        )
        val review = Review(
            id = 1L,
            product = product,
            userId = request.userId,
            rating = request.rating,
            content = request.content,
            createdAt = LocalDateTime.now()
        )
        
        `when`(productRepository.findById(productId)).thenReturn(java.util.Optional.of(product))
        `when`(reviewRepository.save(any())).thenReturn(review)

        // when
        val result = productService.createReview(productId, request)

        // then
        assertNotNull(result)
        assertEquals(productId, result.productId)
        assertEquals(request.userId, result.userId)
        assertEquals(request.rating, result.rating)
        assertEquals(request.content, result.content)
    }

    @Test
    fun `리뷰 생성 실패 - 잘못된 평점`() {
        // given
        val productId = 1L
        val product = Product(
            id = productId,
            name = "테스트 상품",
            price = BigDecimal.TEN,
            stock = 10,
            isActive = true
        )
        val request = CreateReviewRequest(
            userId = 1L,
            rating = 6, // 잘못된 평점
            content = "좋은 상품입니다!"
        )
        
        `when`(productRepository.findById(productId)).thenReturn(java.util.Optional.of(product))

        // when & then
        val exception = org.junit.jupiter.api.assertThrows<RuntimeException> {
            productService.createReview(productId, request)
        }
        assertTrue(exception.message!!.contains("평점은 1-5점 사이여야 합니다"))
    }

    @Test
    fun `상품 상세 조회 성공`() {
        // given
        val productId = 1L
        val product = Product(
            id = productId,
            name = "테스트 상품",
            price = BigDecimal.TEN,
            stock = 10,
            isActive = true
        )
        val reviews = listOf(
            Review(
                id = 1L,
                product = product,
                userId = 1L,
                rating = 5,
                content = "좋은 상품입니다!",
                createdAt = LocalDateTime.now()
            ),
            Review(
                id = 2L,
                product = product,
                userId = 2L,
                rating = 4,
                content = "괜찮은 상품입니다.",
                createdAt = LocalDateTime.now()
            )
        )
        
        `when`(productRepository.findById(productId)).thenReturn(java.util.Optional.of(product))
        `when`(reviewRepository.findByProductId(productId)).thenReturn(reviews)
        `when`(reviewRepository.getAverageRatingByProductId(productId)).thenReturn(4.5)
        `when`(reviewRepository.getReviewCountByProductId(productId)).thenReturn(2L)

        // when
        val result = productService.getProductDetail(productId)

        // then
        assertNotNull(result)
        assertEquals(productId, result.id)
        assertEquals(product.name, result.name)
        assertEquals(4.5, result.averageRating)
        assertEquals(2, result.reviewCount)
        assertEquals(2, result.reviews.size)
    }
} 