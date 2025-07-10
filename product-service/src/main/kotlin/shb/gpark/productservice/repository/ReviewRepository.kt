package shb.gpark.productservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import shb.gpark.productservice.model.Review

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByProductId(productId: Long): List<Review>
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    fun getAverageRatingByProductId(@Param("productId") productId: Long): Double?
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    fun getReviewCountByProductId(@Param("productId") productId: Long): Long
} 