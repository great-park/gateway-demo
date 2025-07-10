package shb.gpark.productservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.productservice.dto.*
import shb.gpark.productservice.service.ProductService
import java.math.BigDecimal
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.data.domain.Sort

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "상품 관리 API")
class ProductController(
    private val productService: ProductService
) {
    
    @PostMapping("/api/products")
    fun createProduct(@RequestBody request: ProductCreateRequest): ResponseEntity<ProductResponse> {
        val product = productService.createProduct(request)
        return ResponseEntity.ok(product)
    }
    
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "모든 상품 목록을 조회합니다.")
    fun getAllProducts(): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        val products = productService.getAllProducts()
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "상품 목록을 성공적으로 조회했습니다.",
            data = products
        ))
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    fun getProductById(
        @Parameter(description = "상품 ID") @PathVariable id: Long
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "상품을 성공적으로 조회했습니다.",
            data = product
        ))
    }
    
    @PutMapping("/api/products/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody request: ProductCreateRequest): ResponseEntity<ProductResponse> {
        val product = productService.updateProduct(id, request)
        return ResponseEntity.ok(product)
    }

    @DeleteMapping("/api/products/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
    
    @PutMapping("/{id}/stock")
    @Operation(summary = "재고 수정", description = "상품의 재고를 수정합니다.")
    fun updateStock(
        @Parameter(description = "상품 ID") @PathVariable id: Long,
        @RequestBody request: ProductStockUpdateRequest
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        val product = productService.updateStock(id, request)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "재고가 성공적으로 수정되었습니다.",
            data = product
        ))
    }
    
    @GetMapping("/api/products")
    fun searchProducts(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) minStock: Int?,
        @RequestParam(required = false, defaultValue = "id,asc") sort: String
    ): List<ProductResponse> {
        return productService.searchProducts(name, minPrice, maxPrice, minStock, sort)
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 상품 조회", description = "특정 카테고리의 상품들을 조회합니다.")
    fun getProductsByCategory(
        @Parameter(description = "카테고리") @PathVariable category: String
    ): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        val products = productService.getProductsByCategory(category)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "카테고리별 상품을 성공적으로 조회했습니다.",
            data = products
        ))
    }
    
    @GetMapping("/search/name")
    @Operation(summary = "상품명으로 검색", description = "상품명으로 상품을 검색합니다.")
    fun getProductsByName(
        @Parameter(description = "상품명") @RequestParam name: String
    ): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        val products = productService.getProductsByName(name)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "상품명으로 검색 결과를 성공적으로 조회했습니다.",
            data = products
        ))
    }
    
    @GetMapping("/price-range")
    @Operation(summary = "가격 범위로 검색", description = "가격 범위로 상품을 검색합니다.")
    fun getProductsByPriceRange(
        @Parameter(description = "최소 가격") @RequestParam minPrice: BigDecimal,
        @Parameter(description = "최대 가격") @RequestParam maxPrice: BigDecimal
    ): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        val products = productService.getProductsByPriceRange(minPrice, maxPrice)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "가격 범위로 검색 결과를 성공적으로 조회했습니다.",
            data = products
        ))
    }
    
    @GetMapping("/low-stock")
    @Operation(summary = "재고 부족 상품 조회", description = "재고가 부족한 상품들을 조회합니다.")
    fun getLowStockProducts(
        @Parameter(description = "재고 임계값", example = "10") @RequestParam(defaultValue = "10") threshold: Int
    ): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        val products = productService.getLowStockProducts(threshold)
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "재고 부족 상품을 성공적으로 조회했습니다.",
            data = products
        ))
    }
    
    @GetMapping("/active")
    @Operation(summary = "활성 상품 조회", description = "활성 상태인 상품들만 조회합니다.")
    fun getActiveProducts(): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        val products = productService.getActiveProducts()
        return ResponseEntity.ok(ApiResponse(
            success = true,
            message = "활성 상품을 성공적으로 조회했습니다.",
            data = products
        ))
    }

    @GetMapping("/api/products/{id}")
    fun getProductDetail(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.getProduct(id)
        return ResponseEntity.ok(product)
    }

    @PostMapping("/api/products/{id}/restock")
    fun restockProduct(@PathVariable id: Long, @RequestParam amount: Int): ResponseEntity<ProductResponse> {
        val product = productService.restockProduct(id, amount)
        return ResponseEntity.ok(product)
    }

    @PostMapping("/api/products/{id}/activate")
    fun activateProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.setProductActive(id, true)
        return ResponseEntity.ok(product)
    }

    @PostMapping("/api/products/{id}/deactivate")
    fun deactivateProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.setProductActive(id, false)
        return ResponseEntity.ok(product)
    }
} 