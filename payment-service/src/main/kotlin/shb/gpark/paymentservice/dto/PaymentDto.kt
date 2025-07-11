package shb.gpark.paymentservice.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

// 결제 요청 DTO
data class PaymentRequest(
    @field:NotNull(message = "주문 ID는 필수입니다")
    val orderId: Long,
    
    @field:NotNull(message = "사용자 ID는 필수입니다")
    val userId: Long,
    
    @field:Positive(message = "결제 금액은 0보다 커야 합니다")
    val amount: BigDecimal,
    
    val description: String? = null,
    
    val paymentMethod: String = "CARD"
)

// 결제 응답 DTO
data class PaymentResponse(
    val success: Boolean,
    val transactionId: String? = null,
    val message: String,
    val paymentId: Long? = null,
    val status: String? = null
)

// 결제 상세 정보 DTO
data class PaymentDetailResponse(
    val id: Long,
    val orderId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val status: String,
    val paymentDate: LocalDateTime,
    val updatedAt: LocalDateTime,
    val transactionId: String?,
    val description: String?,
    val paymentMethod: String,
    val failureReason: String?
)

// 결제 요약 정보 DTO
data class PaymentSummaryResponse(
    val id: Long,
    val orderId: Long,
    val amount: BigDecimal,
    val status: String,
    val paymentDate: LocalDateTime,
    val transactionId: String?
)

// 결제 상태 업데이트 요청 DTO
data class UpdatePaymentStatusRequest(
    @field:NotNull(message = "결제 상태는 필수입니다")
    val status: String,
    
    val failureReason: String? = null
)

// 환불 요청 DTO
data class RefundRequest(
    @field:NotNull(message = "결제 ID는 필수입니다")
    val paymentId: Long,
    
    @field:Positive(message = "환불 금액은 0보다 커야 합니다")
    val amount: BigDecimal,
    
    val reason: String? = null
)

// 환불 응답 DTO
data class RefundResponse(
    val success: Boolean,
    val refundId: String? = null,
    val message: String,
    val amount: BigDecimal? = null
) 