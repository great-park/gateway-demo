package shb.gpark.productservice.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateProductRequest(
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(min = 1, max = 100, message = "상품명은 1-100자 사이여야 합니다")
    val name: String,
    
    @field:NotBlank(message = "상품 설명은 필수입니다")
    @field:Size(max = 1000, message = "상품 설명은 1000자 이하여야 합니다")
    val description: String,
    
    @field:NotNull(message = "가격은 필수입니다")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    val price: BigDecimal,
    
    @field:NotNull(message = "재고는 필수입니다")
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    val stock: Int,
    
    @field:NotBlank(message = "카테고리는 필수입니다")
    @field:Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    val category: String,
    
    @field:Size(max = 200, message = "이미지 URL은 200자 이하여야 합니다")
    val imageUrl: String? = null
)

data class UpdateProductRequest(
    @field:Size(min = 1, max = 100, message = "상품명은 1-100자 사이여야 합니다")
    val name: String? = null,
    
    @field:Size(max = 1000, message = "상품 설명은 1000자 이하여야 합니다")
    val description: String? = null,
    
    @field:DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    val price: BigDecimal? = null,
    
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    val stock: Int? = null,
    
    @field:Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    val category: String? = null,
    
    @field:Size(max = 200, message = "이미지 URL은 200자 이하여야 합니다")
    val imageUrl: String? = null,
    
    val isActive: Boolean? = null
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stock: Int,
    val category: String,
    val imageUrl: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class ProductSearchRequest(
    val name: String? = null,
    val category: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val isActive: Boolean? = true
)

data class ProductStockUpdateRequest(
    @field:NotNull(message = "재고 수량은 필수입니다")
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
    val stock: Int
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
) 

data class ProductCreateRequest(
    val name: String,
    val price: BigDecimal,
    val stock: Int
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: BigDecimal,
    val stock: Int
) 