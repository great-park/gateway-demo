package shb.gpark.orderservice.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @field:NotNull(message = "사용자 ID는 필수입니다")
    @Column(nullable = false)
    val userId: Long,
    
    @field:NotNull(message = "주문 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus,
    
    @field:Positive(message = "총 금액은 0보다 커야 합니다")
    @Column(nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal = BigDecimal.ZERO,
    
    @Column(nullable = false)
    val orderDate: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val orderItems: MutableList<OrderItem> = mutableListOf(),
    
    @Column(length = 500)
    val notes: String? = null
) {
    constructor() : this(0L, 0L, OrderStatus.PENDING, BigDecimal.ZERO, LocalDateTime.now(), LocalDateTime.now(), mutableListOf(), null)
}

enum class OrderStatus {
    PENDING,    // 대기중
    CONFIRMED,  // 확인됨
    PROCESSING, // 처리중
    SHIPPED,    // 배송됨
    DELIVERED,  // 배송완료
    CANCELLED,  // 취소됨
    REFUNDED    // 환불됨
} 