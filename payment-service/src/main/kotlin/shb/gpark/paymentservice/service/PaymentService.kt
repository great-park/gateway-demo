package shb.gpark.paymentservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.paymentservice.dto.*
import shb.gpark.paymentservice.entity.Payment
import shb.gpark.paymentservice.entity.PaymentStatus
import shb.gpark.paymentservice.repository.PaymentRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import org.slf4j.LoggerFactory

class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val pgClient: PgClient
) {
    private val logger = LoggerFactory.getLogger(PaymentService::class.java)
    
    @Transactional
    suspend fun processPayment(request: PaymentRequest): PaymentResponse {
        logger.info("[결제 요청] orderId={}, userId={}, amount={}", request.orderId, request.userId, request.amount)
        // 외부 PG사 결제 승인 요청
        val pgResult = pgClient.approvePayment(request)
        
        val payment = Payment(
            orderId = request.orderId,
            userId = request.userId,
            amount = request.amount,
            status = if (pgResult.success) PaymentStatus.COMPLETED else PaymentStatus.FAILED,
            description = request.description,
            paymentMethod = request.paymentMethod,
            transactionId = pgResult.transactionId,
            failureReason = if (pgResult.success) null else pgResult.message
        )
        val saved = paymentRepository.save(payment)
        if (pgResult.success) {
            logger.info("[결제 성공] paymentId={}, transactionId={}", saved.id, saved.transactionId)
        } else {
            logger.warn("[결제 실패] paymentId={}, reason={}", saved.id, saved.failureReason)
        }
        
        return pgResult.copy(paymentId = saved.id, status = payment.status.name)
    }
    
    fun getPayment(paymentId: Long): PaymentDetailResponse {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { RuntimeException("존재하지 않는 결제입니다.") }
        
        return toPaymentDetailResponse(payment)
    }
    
    fun getPaymentsByOrder(orderId: Long): List<PaymentDetailResponse> {
        return paymentRepository.findByOrderId(orderId).map { toPaymentDetailResponse(it) }
    }
    
    fun getPaymentsByUser(userId: Long): List<PaymentSummaryResponse> {
        return paymentRepository.findByUserId(userId).map { toPaymentSummaryResponse(it) }
    }
    
    @Transactional
    fun updatePaymentStatus(paymentId: Long, request: UpdatePaymentStatusRequest): PaymentDetailResponse {
        val payment = paymentRepository.findById(paymentId)
            .orElseThrow { RuntimeException("존재하지 않는 결제입니다.") }
        
        val newStatus = PaymentStatus.valueOf(request.status.uppercase())
        val updatedPayment = payment.copy(
            status = newStatus,
            failureReason = request.failureReason,
            updatedAt = LocalDateTime.now()
        )
        
        val saved = paymentRepository.save(updatedPayment)
        return toPaymentDetailResponse(saved)
    }
    
    @Transactional
    suspend fun refundPayment(request: RefundRequest): RefundResponse {
        logger.info("[환불 요청] paymentId={}, amount={}", request.paymentId, request.amount)
        val payment = paymentRepository.findById(request.paymentId)
            .orElseThrow { RuntimeException("존재하지 않는 결제입니다.") }
        
        if (payment.status != PaymentStatus.COMPLETED) {
            logger.warn("[환불 실패] paymentId={}, 사유=완료된 결제만 환불 가능", payment.id)
            return RefundResponse(
                success = false,
                message = "완료된 결제만 환불할 수 있습니다."
            )
        }
        
        if (request.amount > payment.amount) {
            logger.warn("[환불 실패] paymentId={}, 사유=환불 금액 초과", payment.id)
            return RefundResponse(
                success = false,
                message = "환불 금액이 결제 금액을 초과할 수 없습니다."
            )
        }
        
        // 환불 처리 시뮬레이션
        val refundId = "REF_${System.currentTimeMillis()}"
        val refundedPayment = payment.copy(
            status = PaymentStatus.REFUNDED,
            updatedAt = LocalDateTime.now()
        )
        paymentRepository.save(refundedPayment)
        logger.info("[환불 성공] paymentId={}, refundId={}, amount={}", payment.id, refundId, request.amount)
        return RefundResponse(
            success = true,
            refundId = refundId,
            message = "환불이 성공적으로 처리되었습니다.",
            amount = request.amount
        )
    }
    
    fun getPaymentStatistics(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Map<String, Any> {
        val completedCount = paymentRepository.countByStatusAndPaymentDateBetween(
            PaymentStatus.COMPLETED, startDate, endDate
        )
        val completedAmount = paymentRepository.sumAmountByStatusAndPaymentDateBetween(
            PaymentStatus.COMPLETED, startDate, endDate
        ) ?: BigDecimal.ZERO
        
        val failedCount = paymentRepository.countByStatusAndPaymentDateBetween(
            PaymentStatus.FAILED, startDate, endDate
        )
        
        return mapOf(
            "completedCount" to completedCount,
            "completedAmount" to completedAmount,
            "failedCount" to failedCount,
            "successRate" to if (completedCount + failedCount > 0) {
                (completedCount.toDouble() / (completedCount + failedCount)) * 100
            } else 0.0
        )
    }
    
    private fun toPaymentDetailResponse(payment: Payment): PaymentDetailResponse {
        return PaymentDetailResponse(
            id = payment.id,
            orderId = payment.orderId,
            userId = payment.userId,
            amount = payment.amount,
            status = payment.status.name,
            paymentDate = payment.paymentDate,
            updatedAt = payment.updatedAt,
            transactionId = payment.transactionId,
            description = payment.description,
            paymentMethod = payment.paymentMethod,
            failureReason = payment.failureReason
        )
    }
    
    private fun toPaymentSummaryResponse(payment: Payment): PaymentSummaryResponse {
        return PaymentSummaryResponse(
            id = payment.id,
            orderId = payment.orderId,
            amount = payment.amount,
            status = payment.status.name,
            paymentDate = payment.paymentDate,
            transactionId = payment.transactionId
        )
    }
} 