package shb.gpark.productservice.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @NotBlank(message = "상품명은 필수입니다")
    @Size(min = 1, max = 100, message = "상품명은 1-100자 사이여야 합니다")
    @Column(nullable = false, unique = true)
    val name: String,
    
    @NotBlank(message = "상품 설명은 필수입니다")
    @Size(max = 1000, message = "상품 설명은 1000자 이하여야 합니다")
    @Column(columnDefinition = "TEXT")
    val description: String,
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    @NotNull(message = "재고는 필수입니다")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다")
    @Column(nullable = false)
    val stock: Int,
    
    @NotBlank(message = "카테고리는 필수입니다")
    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다")
    @Column(nullable = false)
    val category: String,
    
    @Size(max = 200, message = "이미지 URL은 200자 이하여야 합니다")
    @Column(name = "image_url")
    val imageUrl: String? = null,
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        // updatedAt을 자동으로 업데이트
    }
} 