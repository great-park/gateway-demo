package shb.gpark.productservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import shb.gpark.productservice.entity.Product
import java.math.BigDecimal

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    
    // 기본 검색 메서드들
    fun findByNameContainingIgnoreCase(name: String): List<Product>
    fun findByCategory(category: String): List<Product>
    fun findByIsActive(isActive: Boolean): List<Product>
    
    // 가격 범위 검색
    fun findByPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product>
    
    // 재고가 있는 상품만 조회
    fun findByStockGreaterThan(stock: Int): List<Product>
    
    // 카테고리별 활성 상품 조회
    fun findByCategoryAndIsActive(category: String, isActive: Boolean): List<Product>
    
    // 복합 검색
    @Query("""
        SELECT p FROM Product p 
        WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:category IS NULL OR p.category = :category)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        AND (:isActive IS NULL OR p.isActive = :isActive)
        ORDER BY p.createdAt DESC
    """)
    fun searchProducts(
        @Param("name") name: String?,
        @Param("category") category: String?,
        @Param("minPrice") minPrice: BigDecimal?,
        @Param("maxPrice") maxPrice: BigDecimal?,
        @Param("isActive") isActive: Boolean?
    ): List<Product>
    
    // 카테고리별 상품 수 조회
    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category")
    fun countByCategory(): List<Array<Any>>
    
    // 재고 부족 상품 조회 (재고가 10개 미만)
    fun findByStockLessThanAndIsActive(stock: Int, isActive: Boolean): List<Product>
} 