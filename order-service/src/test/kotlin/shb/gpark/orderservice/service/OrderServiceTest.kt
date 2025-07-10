package shb.gpark.orderservice.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.beans.factory.annotation.Autowired
import shb.gpark.orderservice.repository.OrderRepository
import shb.gpark.orderservice.dto.CreateOrderRequest
import shb.gpark.orderservice.dto.OrderResponse
import shb.gpark.orderservice.entity.Order
import java.math.BigDecimal

@SpringBootTest
class OrderServiceTest {
    @Autowired
    lateinit var orderService: OrderService

    @MockBean
    lateinit var orderRepository: OrderRepository

    @Test
    fun `주문 생성 성공`() {
        // given
        val request = CreateOrderRequest(
            userId = 1L,
            items = listOf(),
            notes = "테스트 주문"
        )
        val order = Order(
            id = 1L,
            userId = 1L,
            totalAmount = BigDecimal.TEN,
            status = shb.gpark.orderservice.entity.OrderStatus.PENDING,
            orderDate = java.time.LocalDateTime.now(),
            updatedAt = java.time.LocalDateTime.now(),
            notes = "테스트 주문"
        )
        `when`(orderRepository.save(any())).thenReturn(order)

        // when
        val result = orderService.createOrder(request)

        // then
        assertNotNull(result)
        assertEquals(order.id, result.id)
        assertEquals(order.userId, result.userId)
    }

    @Test
    fun `주문 조회 실패 - 존재하지 않는 주문`() {
        // given
        val orderId = 999L
        `when`(orderRepository.findById(orderId)).thenReturn(java.util.Optional.empty())

        // when & then
        val exception = org.junit.jupiter.api.assertThrows<RuntimeException> {
            orderService.getOrder(orderId)
        }
        assertTrue(exception.message!!.contains("존재하지 않는 주문"))
    }

    @Test
    fun `주문 상세 조회 성공`() {
        // given
        val orderId = 1L
        val order = Order(
            id = orderId,
            userId = 1L,
            totalAmount = BigDecimal.TEN,
            status = shb.gpark.orderservice.entity.OrderStatus.PENDING,
            orderDate = java.time.LocalDateTime.now(),
            updatedAt = java.time.LocalDateTime.now(),
            notes = "상세 테스트"
        )
        `when`(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order))

        // when
        val result = orderService.getOrder(orderId)

        // then
        assertNotNull(result)
        assertEquals(orderId, result.id)
        assertEquals(order.userId, result.userId)
    }

    @Test
    fun `주문 확정 성공`() {
        // given
        val orderId = 2L
        val order = Order(
            id = orderId,
            userId = 1L,
            totalAmount = BigDecimal.TEN,
            status = shb.gpark.orderservice.entity.OrderStatus.PENDING,
            orderDate = java.time.LocalDateTime.now(),
            updatedAt = java.time.LocalDateTime.now(),
            notes = "확정 테스트"
        )
        `when`(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order))
        `when`(orderRepository.save(order)).thenReturn(order.copy(status = shb.gpark.orderservice.entity.OrderStatus.CONFIRMED))

        // when
        val result = orderService.confirmOrder(orderId)

        // then
        assertEquals(shb.gpark.orderservice.entity.OrderStatus.CONFIRMED, result.status)
    }

    @Test
    fun `주문 취소 성공`() {
        // given
        val orderId = 3L
        val order = Order(
            id = orderId,
            userId = 1L,
            totalAmount = BigDecimal.TEN,
            status = shb.gpark.orderservice.entity.OrderStatus.PENDING,
            orderDate = java.time.LocalDateTime.now(),
            updatedAt = java.time.LocalDateTime.now(),
            notes = "취소 테스트"
        )
        `when`(orderRepository.findById(orderId)).thenReturn(java.util.Optional.of(order))
        `when`(orderRepository.save(order)).thenReturn(order.copy(status = shb.gpark.orderservice.entity.OrderStatus.CANCELLED))

        // when
        val result = orderService.cancelOrder(orderId)

        // then
        assertEquals(shb.gpark.orderservice.entity.OrderStatus.CANCELLED, result.status)
    }
} 