package shb.gpark.productservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.productservice.dto.*
import shb.gpark.productservice.entity.Product
import shb.gpark.productservice.exception.*
import shb.gpark.productservice.repository.ProductRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun createProduct(request: CreateProductRequest): ProductResponse {
        // 중복 상품명 검사
        if (productRepository.findByNameContainingIgnoreCase(request.name).isNotEmpty()) {
            throw ProductAlreadyExistsException(request.name)
        }
        
        // 가격 검증
        if (request.price <= BigDecimal.ZERO) {
            throw InvalidPriceException(request.price.toString())
        }
        
        // 재고 검증
        if (request.stock < 0) {
            throw InvalidStockException(request.stock)
        }
        
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
            .orElseThrow { ProductNotFoundException(id) }
        return product.toResponse()
    }
    
    fun updateProduct(id: Long, request: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }
        
        // 상품명 변경 시 중복 검사
        if (request.name != null && request.name != product.name) {
            val existingProducts = productRepository.findByNameContainingIgnoreCase(request.name)
            if (existingProducts.isNotEmpty()) {
                throw ProductAlreadyExistsException(request.name)
            }
        }
        
        // 가격 검증
        if (request.price != null && request.price <= BigDecimal.ZERO) {
            throw InvalidPriceException(request.price.toString())
        }
        
        // 재고 검증
        if (request.stock != null && request.stock < 0) {
            throw InvalidStockException(request.stock)
        }
        
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
            .orElseThrow { ProductNotFoundException(id) }
        
        productRepository.delete(product)
    }
    
    fun updateStock(id: Long, request: ProductStockUpdateRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }
        
        // 재고 검증
        if (request.stock < 0) {
            throw InvalidStockException(request.stock)
        }
        
        val updatedProduct = product.copy(
            stock = request.stock,
            updatedAt = LocalDateTime.now()
        )
        
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toResponse()
    }
    
    fun decreaseStock(id: Long, quantity: Int): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }
        
        if (product.stock < quantity) {
            throw InsufficientStockException(id, quantity, product.stock)
        }
        
        val updatedProduct = product.copy(
            stock = product.stock - quantity,
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
        if (minPrice > maxPrice) {
            throw InvalidProductDataException("최소 가격이 최대 가격보다 클 수 없습니다.")
        }
        return productRepository.findByPriceBetween(minPrice, maxPrice)
            .map { it.toResponse() }
    }
    
    @Transactional(readOnly = true)
    fun getLowStockProducts(threshold: Int = 10): List<ProductResponse> {
        if (threshold < 0) {
            throw InvalidStockException(threshold)
        }
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