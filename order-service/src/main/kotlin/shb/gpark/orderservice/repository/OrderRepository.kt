package shb.gpark.orderservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import shb.gpark.orderservice.entity.Order
import shb.gpark.orderservice.entity.OrderStatus
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    
    fun findByUserId(userId: Long): List<Order>
    
    fun findByStatus(status: OrderStatus): List<Order>
    
    fun findByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order>
    
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    fun findByOrderDateBetween(@Param("startDate") startDate: LocalDateTime, @Param("endDate") endDate: LocalDateTime): List<Order>
    
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.orderDate BETWEEN :startDate AND :endDate")
    fun findByUserIdAndOrderDateBetween(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Order>
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    fun countByStatus(@Param("status") status: OrderStatus): Long
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    fun countByUserId(@Param("userId") userId: Long): Long
} 