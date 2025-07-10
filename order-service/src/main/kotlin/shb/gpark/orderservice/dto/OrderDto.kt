package shb.gpark.orderservice.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import shb.gpark.orderservice.entity.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderRequest(
    @field:NotNull(message = "사용자 ID는 필수입니다")
    val userId: Long,
    
    @field:NotNull(message = "주문 항목은 필수입니다")
    val items: List<OrderItemRequest>,
    
    val notes: String? = null
)

data class OrderItemRequest(
    @field:NotNull(message = "상품 ID는 필수입니다")
    val productId: Long,
    
    @field:Positive(message = "수량은 0보다 커야 합니다")
    val quantity: Int
)

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val orderDate: LocalDateTime,
    val updatedAt: LocalDateTime,
    val items: List<OrderItemResponse>,
    val notes: String?
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal
)

data class UpdateOrderStatusRequest(
    @field:NotNull(message = "주문 상태는 필수입니다")
    val status: OrderStatus
)

data class OrderSummaryResponse(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val orderDate: LocalDateTime,
    val itemCount: Int
) 