package shb.gpark.orderservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.orderservice.dto.*
import shb.gpark.orderservice.entity.Order
import shb.gpark.orderservice.entity.OrderItem
import shb.gpark.orderservice.entity.OrderStatus
import shb.gpark.orderservice.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageImpl
import java.math.BigDecimal
import java.time.LocalDateTime
import shb.gpark.orderservice.service.PaymentClient
import shb.gpark.orderservice.service.NotificationClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

interface PaymentService {
    fun processPayment(orderId: Long, amount: BigDecimal): Boolean
}

class MockPaymentService : PaymentService {
    override fun processPayment(orderId: Long, amount: BigDecimal): Boolean {
        // 실제 결제 연동 대신 항상 성공 처리
        return true
    }
}

interface NotificationService {
    fun sendOrderNotification(orderId: Long, userId: Long, message: String)
}

class MockNotificationService : NotificationService {
    override fun sendOrderNotification(orderId: Long, userId: Long, message: String) {
        // 실제 알림 연동 대신 로그 출력
        println("[알림] 주문 $orderId, 사용자 $userId: $message")
    }
}

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productServiceClient: ProductServiceClient,
    private val paymentClient: PaymentClient,
    private val notificationClient: NotificationClient
) {
    fun toOrderResponse(order: Order): OrderResponse {
        return OrderResponse(
            id = order.id,
            userId = order.userId,
            status = order.status,
            totalAmount = order.totalAmount,
            orderDate = order.orderDate,
            updatedAt = order.updatedAt,
            items = order.orderItems.map {
                OrderItemResponse(
                    id = it.id ?: 0L,
                    productId = it.productId,
                    productName = it.productName,
                    quantity = it.quantity,
                    unitPrice = it.unitPrice,
                    totalPrice = it.totalPrice
                )
            },
            notes = order.notes
        )
    }

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
        return toOrderResponse(saved)
    }

    fun getOrder(orderId: Long): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { RuntimeException("존재하지 않는 주문입니다.") }
        return toOrderResponse(order)
    }

    // 결제/알림 연동을 위한 주문 생성 이벤트 설계 예시
    data class OrderCreatedEvent(val orderId: Long, val userId: Long, val totalAmount: BigDecimal)
    fun publishOrderCreatedEvent(order: Order) {
        // 실제 이벤트 발행 로직은 메시지 브로커, 이벤트 버스 등과 연동 예정
        // 예시: eventPublisher.publish(OrderCreatedEvent(order.id, order.userId, order.totalAmount))
    }

    fun getOrdersByUser(userId: Long): List<OrderSummaryResponse> {
        return orderRepository.findByUserId(userId).map { it.toOrderSummaryResponse() }
    }

    fun searchOrders(userId: Long?, status: String?, from: String?, to: String?, pageable: Pageable): Page<OrderResponse> {
        val all = orderRepository.findAll()
            .filter { (userId == null || it.userId == userId) &&
                      (status == null || it.status.name == status) &&
                      (from == null || it.orderDate.isAfter(LocalDateTime.parse(from))) &&
                      (to == null || it.orderDate.isBefore(LocalDateTime.parse(to))) }
            .map { toOrderResponse(it) }
        val start = pageable.offset.toInt()
        val end = (start + pageable.pageSize).coerceAtMost(all.size)
        val content = if (start <= end) all.subList(start, end) else emptyList()
        return PageImpl(content, pageable, all.size.toLong())
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, request: UpdateOrderStatusRequest): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { RuntimeException("존재하지 않는 주문입니다.") }
        order.status = request.status
        order.updatedAt = java.time.LocalDateTime.now()
        val saved = orderRepository.save(order)
        return toOrderResponse(saved)
    }

    suspend fun confirmOrder(orderId: Long): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { RuntimeException("존재하지 않는 주문입니다.") }
        if (order.status != OrderStatus.PENDING) throw RuntimeException("이미 처리된 주문입니다.")
        
        // 실제 결제 서비스 호출 (동기)
        val paymentResponse = paymentClient.processPayment(
            PaymentRequest(
                orderId = order.id,
                userId = order.userId,
                amount = order.totalAmount,
                description = "주문 결제"
            )
        )
        if (!paymentResponse.success) throw RuntimeException("결제 실패: ${paymentResponse.message}")
        
        order.status = OrderStatus.CONFIRMED
        order.updatedAt = java.time.LocalDateTime.now()
        val saved = orderRepository.save(order)
        
        // 실제 알림 서비스 호출 (비동기)
        try {
            notificationClient.sendEmailNotification(
                order.userId,
                "주문 확정",
                "주문이 성공적으로 확정되었습니다. 주문번호: ${order.id}"
            )
        } catch (e: Exception) {
            println("알림 발송 실패: ${e.message}")
        }
        
        return toOrderResponse(saved)
    }
    suspend fun cancelOrder(orderId: Long): OrderResponse {
        val order = orderRepository.findById(orderId).orElseThrow { RuntimeException("존재하지 않는 주문입니다.") }
        if (order.status == OrderStatus.CANCELLED) throw RuntimeException("이미 취소된 주문입니다.")
        
        order.status = OrderStatus.CANCELLED
        order.updatedAt = java.time.LocalDateTime.now()
        val saved = orderRepository.save(order)
        
        // 실제 알림 서비스 호출 (비동기)
        try {
            notificationClient.sendSlackNotification(
                order.userId,
                "주문이 취소되었습니다. 주문번호: ${order.id}"
            )
        } catch (e: Exception) {
            println("알림 발송 실패: ${e.message}")
        }
        
        return toOrderResponse(saved)
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