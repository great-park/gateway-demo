package shb.gpark.paymentservice.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @field:NotNull(message = "주문 ID는 필수입니다")
    @Column(nullable = false)
    val orderId: Long,
    
    @field:NotNull(message = "사용자 ID는 필수입니다")
    @Column(nullable = false)
    val userId: Long,
    
    @field:Positive(message = "결제 금액은 0보다 커야 합니다")
    @Column(nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal,
    
    // 결제 상태는 결제 처리/환불/실패 등에서 변경될 수 있으므로 var로 선언
    @field:NotNull(message = "결제 상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PaymentStatus,
    
    @Column(nullable = false)
    val paymentDate: LocalDateTime = LocalDateTime.now(),
    
    // 결제/환불 등에서 갱신되므로 var로 선언
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(length = 100)
    val transactionId: String? = null,
    
    @Column(length = 500)
    val description: String? = null,
    
    @Column(length = 100)
    val paymentMethod: String = "CARD",
    
    // 실패 사유는 결제 실패/환불 등에서 변경될 수 있으므로 var로 선언
    @Column(length = 500)
    var failureReason: String? = null
) {
    constructor() : this(0L, 0L, 0L, BigDecimal.ZERO, PaymentStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), null, null, "CARD", null)
}

enum class PaymentStatus {
    PENDING,    // 대기중
    PROCESSING, // 처리중
    COMPLETED,  // 완료
    FAILED,     // 실패
    CANCELLED,  // 취소됨
    REFUNDED    // 환불됨
} 