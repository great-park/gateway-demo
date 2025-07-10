package shb.gpark.productservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.productservice.dto.*
import shb.gpark.productservice.entity.Product
import shb.gpark.productservice.exception.*
import shb.gpark.productservice.repository.ProductRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import shb.gpark.productservice.dto.ProductCreateRequest
import shb.gpark.productservice.dto.ProductResponse
import shb.gpark.productservice.model.Product

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun createProduct(request: ProductCreateRequest): ProductResponse {
        val product = Product(
            name = request.name,
            price = request.price,
            stock = request.stock
        )
        val saved = productRepository.save(product)
        return ProductResponse(
            id = saved.id,
            name = saved.name,
            price = saved.price,
            stock = saved.stock
        )
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
    
    fun updateProduct(id: Long, request: ProductCreateRequest): ProductResponse {
        val product = productRepository.findById(id).orElseThrow { RuntimeException("존재하지 않는 상품입니다.") }
        product.name = request.name
        product.price = request.price
        product.stock = request.stock
        val saved = productRepository.save(product)
        return ProductResponse(
            id = saved.id,
            name = saved.name,
            price = saved.price,
            stock = saved.stock
        )
    }
    
    fun deleteProduct(id: Long) {
        if (!productRepository.existsById(id)) throw RuntimeException("존재하지 않는 상품입니다.")
        productRepository.deleteById(id)
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
    
    fun searchProducts(name: String?, minPrice: BigDecimal?, maxPrice: BigDecimal?, minStock: Int?, sort: String): List<ProductResponse> {
        val all = productRepository.findAll().filter {
            (name == null || it.name.contains(name, ignoreCase = true)) &&
            (minPrice == null || it.price >= minPrice) &&
            (maxPrice == null || it.price <= maxPrice) &&
            (minStock == null || it.stock >= minStock)
        }
        val sorted = when(sort) {
            "price,asc" -> all.sortedBy { it.price }
            "price,desc" -> all.sortedByDescending { it.price }
            "stock,asc" -> all.sortedBy { it.stock }
            "stock,desc" -> all.sortedByDescending { it.stock }
            else -> all.sortedBy { it.id }
        }
        return sorted.map {
            ProductResponse(
                id = it.id,
                name = it.name,
                price = it.price,
                stock = it.stock
            )
        }
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
    
    fun getProduct(id: Long): ProductResponse {
        val product = productRepository.findById(id).orElseThrow { RuntimeException("존재하지 않는 상품입니다.") }
        return ProductResponse(
            id = product.id,
            name = product.name,
            price = product.price,
            stock = product.stock
        )
    }
    fun restockProduct(id: Long, amount: Int): ProductResponse {
        val product = productRepository.findById(id).orElseThrow { RuntimeException("존재하지 않는 상품입니다.") }
        product.stock += amount
        val saved = productRepository.save(product)
        return ProductResponse(
            id = saved.id,
            name = saved.name,
            price = saved.price,
            stock = saved.stock
        )
    }
    fun setProductActive(id: Long, active: Boolean): ProductResponse {
        val product = productRepository.findById(id).orElseThrow { RuntimeException("존재하지 않는 상품입니다.") }
        product.isActive = active
        val saved = productRepository.save(product)
        return ProductResponse(
            id = saved.id,
            name = saved.name,
            price = saved.price,
            stock = saved.stock
        )
    }
    
    private fun Product.toResponse(): ProductResponse {
        return ProductResponse(
            id = this.id!!,
            name = this.name,
            price = this.price,
            stock = this.stock,
            updatedAt = this.updatedAt
        )
    }
} 