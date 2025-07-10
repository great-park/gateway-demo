package shb.gpark.productservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.productservice.model.Product
import shb.gpark.productservice.model.ProductCategory
import shb.gpark.productservice.service.ProductService
import jakarta.validation.Valid
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getAllProducts())
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<Product> {
        return productService.getProductById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/category/{category}")
    fun getProductsByCategory(@PathVariable category: ProductCategory): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getProductsByCategory(category))
    }

    @GetMapping("/search")
    fun searchProducts(@RequestParam keyword: String): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.searchProducts(keyword))
    }

    @GetMapping("/low-stock")
    fun getLowStockProducts(@RequestParam(defaultValue = "10") threshold: Int): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold))
    }

    @GetMapping("/price-range")
    fun getProductsByPriceRange(
        @RequestParam minPrice: BigDecimal,
        @RequestParam maxPrice: BigDecimal
    ): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getProductsByPriceRange(minPrice, maxPrice))
    }

    @PostMapping
    fun createProduct(@Valid @RequestBody product: Product): ResponseEntity<Product> {
        return try {
            ResponseEntity.ok(productService.createProduct(product))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @Valid @RequestBody product: Product): ResponseEntity<Product> {
        return productService.updateProduct(id, product)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        return if (productService.deleteProduct(id)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/{id}/stock")
    fun updateStock(@PathVariable id: Long, @RequestParam quantity: Int): ResponseEntity<Product> {
        return try {
            productService.updateStock(id, quantity)
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "service" to "product-service",
                "status" to "UP",
                "timestamp" to System.currentTimeMillis(),
                "productCount" to productService.getAllProducts().size
            )
        )
    }
} 