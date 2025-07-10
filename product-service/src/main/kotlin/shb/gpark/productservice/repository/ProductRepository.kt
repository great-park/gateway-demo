package shb.gpark.productservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import shb.gpark.productservice.model.Product
import shb.gpark.productservice.model.ProductCategory

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    
    fun findByIsActiveTrue(): List<Product>
    
    fun findByCategoryAndIsActiveTrue(category: ProductCategory): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    fun searchByKeyword(@Param("keyword") keyword: String): List<Product>
    
    fun findByStockLessThanAndIsActiveTrue(stock: Int): List<Product>
    
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice")
    fun findByPriceRange(@Param("minPrice") minPrice: java.math.BigDecimal, @Param("maxPrice") maxPrice: java.math.BigDecimal): List<Product>
} 