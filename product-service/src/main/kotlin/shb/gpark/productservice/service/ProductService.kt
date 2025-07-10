package shb.gpark.productservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.productservice.model.Product
import shb.gpark.productservice.model.ProductCategory
import shb.gpark.productservice.repository.ProductRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun getAllProducts(): List<Product> {
        return productRepository.findByIsActiveTrue()
    }
    
    fun getProductById(id: Long): Product? {
        return productRepository.findById(id).orElse(null)?.takeIf { it.isActive }
    }
    
    fun getProductsByCategory(category: ProductCategory): List<Product> {
        return productRepository.findByCategoryAndIsActiveTrue(category)
    }
    
    fun searchProducts(keyword: String): List<Product> {
        return productRepository.searchByKeyword(keyword)
    }
    
    fun getLowStockProducts(threshold: Int = 10): List<Product> {
        return productRepository.findByStockLessThanAndIsActiveTrue(threshold)
    }
    
    fun getProductsByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product> {
        return productRepository.findByPriceRange(minPrice, maxPrice)
    }
    
    fun createProduct(product: Product): Product {
        return productRepository.save(product.copy(
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        ))
    }
    
    fun updateProduct(id: Long, product: Product): Product? {
        val existingProduct = productRepository.findById(id).orElse(null) ?: return null
        
        val updatedProduct = existingProduct.copy(
            name = product.name,
            description = product.description,
            price = product.price,
            stock = product.stock,
            category = product.category,
            isActive = product.isActive,
            updatedAt = LocalDateTime.now()
        )
        
        return productRepository.save(updatedProduct)
    }
    
    fun deleteProduct(id: Long): Boolean {
        val product = productRepository.findById(id).orElse(null) ?: return false
        
        val softDeletedProduct = product.copy(
            isActive = false,
            updatedAt = LocalDateTime.now()
        )
        
        productRepository.save(softDeletedProduct)
        return true
    }
    
    fun updateStock(id: Long, quantity: Int): Product? {
        val product = productRepository.findById(id).orElse(null) ?: return null
        
        if (product.stock + quantity < 0) {
            throw IllegalArgumentException("재고가 부족합니다")
        }
        
        val updatedProduct = product.copy(
            stock = product.stock + quantity,
            updatedAt = LocalDateTime.now()
        )
        
        return productRepository.save(updatedProduct)
    }
} 