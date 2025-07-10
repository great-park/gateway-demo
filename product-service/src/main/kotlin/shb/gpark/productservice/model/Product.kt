package shb.gpark.productservice.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @NotBlank(message = "상품명은 필수입니다")
    @Column(nullable = false)
    val name: String,
    
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    
    @NotNull(message = "가격은 필수입니다")
    @Positive(message = "가격은 양수여야 합니다")
    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    @NotNull(message = "재고는 필수입니다")
    @Positive(message = "재고는 양수여야 합니다")
    @Column(nullable = false)
    val stock: Int,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ProductCategory,
    
    @Column(nullable = false)
    val isActive: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ProductCategory {
    ELECTRONICS,
    CLOTHING,
    BOOKS,
    FOOD,
    SPORTS,
    HOME,
    BEAUTY,
    OTHER
} 