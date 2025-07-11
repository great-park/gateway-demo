package shb.gpark.paymentservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import shb.gpark.paymentservice.entity.Payment
import shb.gpark.paymentservice.entity.PaymentStatus
import java.time.LocalDateTime

@Repository
interface PaymentRepository : JpaRepository<Payment, Long> {
    
    fun findByOrderId(orderId: Long): List<Payment>
    
    fun findByUserId(userId: Long): List<Payment>
    
    fun findByStatus(status: PaymentStatus): List<Payment>
    
    fun findByPaymentDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Payment>
    
    fun findByOrderIdAndStatus(orderId: Long, status: PaymentStatus): List<Payment>
    
    fun findByUserIdAndStatus(userId: Long, status: PaymentStatus): List<Payment>
    
    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.paymentDate BETWEEN :startDate AND :endDate")
    fun findByUserIdAndPaymentDateBetween(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Payment>
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.paymentDate BETWEEN :startDate AND :endDate")
    fun countByStatusAndPaymentDateBetween(
        @Param("status") status: PaymentStatus,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): Long
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status AND p.paymentDate BETWEEN :startDate AND :endDate")
    fun sumAmountByStatusAndPaymentDateBetween(
        @Param("status") status: PaymentStatus,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): java.math.BigDecimal?
} 