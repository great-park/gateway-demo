package shb.gpark.productservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.productservice.dto.*
import shb.gpark.productservice.entity.Product
import shb.gpark.productservice.repository.ProductRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun createProduct(request: CreateProductRequest): ProductResponse {
        val product = Product(
            name = request.name,
            description = request.description,
            price = request.price,
            stock = request.stock,
            category = request.category,
            imageUrl = request.imageUrl
        )
        
        val savedProduct = productRepository.save(product)
        return savedProduct.toResponse()
    }
    
    @Transactional(readOnly = true)
    fun getAllProducts(): List<ProductResponse> {
        return productRepository.findAll().map { it.toResponse() }
    }
    
    @Transactional(readOnly = true)
    fun getProductById(id: Long): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("상품을 찾을 수 없습니다. ID: $id") }
        return product.toResponse()
    }
    
    fun updateProduct(id: Long, request: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("상품을 찾을 수 없습니다. ID: $id") }
        
        val updatedProduct = product.copy(
            name = request.name ?: product.name,
            description = request.description ?: product.description,
            price = request.price ?: product.price,
            stock = request.stock ?: product.stock,
            category = request.category ?: product.category,
            imageUrl = request.imageUrl ?: product.imageUrl,
            isActive = request.isActive ?: product.isActive,
            updatedAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toResponse()
    }
    
    fun deleteProduct(id: Long) {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("상품을 찾을 수 없습니다. ID: $id") }
        
        productRepository.delete(product)
    }
    
    fun updateStock(id: Long, request: ProductStockUpdateRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { RuntimeException("상품을 찾을 수 없습니다. ID: $id") }
        
        val updatedProduct = product.copy(
            stock = request.stock,
            updatedAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toResponse()
    }
    
    @Transactional(readOnly = true)
    fun searchProducts(request: ProductSearchRequest): List<ProductResponse> {
        val products = productRepository.searchProducts(
            name = request.name,
            category = request.category,
            minPrice = request.minPrice,
            maxPrice = request.maxPrice,
            isActive = request.isActive
        )
        return products.map { it.toResponse() }
    }
    
    @Transactional(readOnly = true)
    fun getProductsByCategory(category: String): List<ProductResponse> {
        return productRepository.findByCategoryAndIsActive(category, true)
            .map { it.toResponse() }
    }
    
    @Transactional(readOnly = true)
    fun getProductsByName(name: String): List<ProductResponse> {
        return productRepository.findByNameContainingIgnoreCase(name)
            .map { it.toResponse() }
    }
    
    @Transactional(readOnly = true)
    fun getProductsByPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<ProductResponse> {
        return productRepository.findByPriceBetween(minPrice, maxPrice)
            .map { it.toResponse() }
    }
    
    @Transactional(readOnly = true)
    fun getLowStockProducts(threshold: Int = 10): List<ProductResponse> {
        return productRepository.findByStockLessThanAndIsActive(threshold, true)
            .map { it.toResponse() }
    }
    
    @Transactional(readOnly = true)
    fun getActiveProducts(): List<ProductResponse> {
        return productRepository.findByIsActive(true)
            .map { it.toResponse() }
    }
    
    private fun Product.toResponse(): ProductResponse {
        return ProductResponse(
            id = this.id!!,
            name = this.name,
            description = this.description,
            price = this.price,
            stock = this.stock,
            category = this.category,
            imageUrl = this.imageUrl,
            isActive = this.isActive,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
} 