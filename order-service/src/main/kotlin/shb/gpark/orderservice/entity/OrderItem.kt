package shb.gpark.orderservice.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,
    
    @field:NotNull(message = "상품 ID는 필수입니다")
    @Column(nullable = false)
    val productId: Long,
    
    @field:NotNull(message = "상품명은 필수입니다")
    @Column(nullable = false, length = 200)
    val productName: String,
    
    @field:Positive(message = "수량은 0보다 커야 합니다")
    @Column(nullable = false)
    val quantity: Int,
    
    @field:Positive(message = "단가는 0보다 커야 합니다")
    @Column(nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,
    
    @field:Positive(message = "총 가격은 0보다 커야 합니다")
    @Column(nullable = false, precision = 10, scale = 2)
    val totalPrice: BigDecimal
) {
    constructor() : this(null, Order(), 0L, "", 0, BigDecimal.ZERO, BigDecimal.ZERO)
} 