package shb.gpark.orderservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.orderservice.dto.*
import shb.gpark.orderservice.entity.Order
import shb.gpark.orderservice.entity.OrderItem
import shb.gpark.orderservice.entity.OrderStatus
import shb.gpark.orderservice.repository.OrderRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productServiceClient: ProductServiceClient
) {
    @Transactional
    fun createOrder(request: CreateOrderRequest): OrderResponse {
        // 상품 정보 조회 및 재고 확인
        val orderItems = request.items.map { itemReq ->
            val product = productServiceClient.getProduct(itemReq.productId).block()
                ?: throw IllegalArgumentException("상품 정보를 찾을 수 없습니다: ${itemReq.productId}")
            if (!product.isActive) throw IllegalArgumentException("비활성 상품입니다: ${product.name}")
            if (product.stock < itemReq.quantity) throw IllegalArgumentException("재고 부족: ${product.name}")
            OrderItem(
                order = Order(), // 임시, 아래에서 실제 Order와 연결됨
                productId = product.id,
                productName = product.name,
                quantity = itemReq.quantity,
                unitPrice = product.price,
                totalPrice = product.price.multiply(BigDecimal(itemReq.quantity))
            )
        }
        val totalAmount = orderItems.sumOf { it.totalPrice }
        val order = Order(
            userId = request.userId,
            status = OrderStatus.PENDING,
            totalAmount = totalAmount,
            orderDate = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            notes = request.notes ?: ""
        )
        // OrderItem에 실제 Order 연결
        orderItems.forEach { it.order = order }
        order.orderItems.addAll(orderItems)
        val saved = orderRepository.save(order)
        // 재고 차감
        orderItems.forEach { item ->
            productServiceClient.updateStock(item.productId, -item.quantity).block()
        }
        return saved.toOrderResponse()
    }

    fun getOrder(orderId: Long): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { IllegalArgumentException("주문을 찾을 수 없습니다") }
        return order.toOrderResponse()
    }

    fun getOrdersByUser(userId: Long): List<OrderSummaryResponse> {
        return orderRepository.findByUserId(userId).map { it.toOrderSummaryResponse() }
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, request: UpdateOrderStatusRequest): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { RuntimeException("존재하지 않는 주문입니다.") }
        order.status = request.status
        order.updatedAt = java.time.LocalDateTime.now()
        val saved = orderRepository.save(order)
        return toOrderResponse(saved)
    }

    private fun Order.toOrderResponse(): OrderResponse {
        return OrderResponse(
            id = this.id!!,
            userId = this.userId,
            status = this.status,
            totalAmount = this.totalAmount,
            orderDate = this.orderDate,
            updatedAt = this.updatedAt,
            items = this.orderItems.map { it.toOrderItemResponse() },
            notes = this.notes
        )
    }
    private fun OrderItem.toOrderItemResponse(): OrderItemResponse {
        return OrderItemResponse(
            id = this.id!!,
            productId = this.productId,
            productName = this.productName,
            quantity = this.quantity,
            unitPrice = this.unitPrice,
            totalPrice = this.totalPrice
        )
    }
    private fun Order.toOrderSummaryResponse(): OrderSummaryResponse {
        return OrderSummaryResponse(
            id = this.id!!,
            userId = this.userId,
            status = this.status,
            totalAmount = this.totalAmount,
            orderDate = this.orderDate,
            itemCount = this.orderItems.size
        )
    }
} 