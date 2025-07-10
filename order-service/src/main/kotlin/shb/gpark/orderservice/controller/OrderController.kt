package shb.gpark.orderservice.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.orderservice.dto.*
import shb.gpark.orderservice.entity.OrderStatus
import shb.gpark.orderservice.service.OrderService

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService
) {
    @PostMapping
    fun createOrder(@Valid @RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val response = orderService.createOrder(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: Long): ResponseEntity<OrderResponse> {
        val response = orderService.getOrder(orderId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/user/{userId}")
    fun getOrdersByUser(@PathVariable userId: Long): ResponseEntity<List<OrderSummaryResponse>> {
        val response = orderService.getOrdersByUser(userId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{orderId}/status")
    fun updateOrderStatus(
        @PathVariable orderId: Long,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ): ResponseEntity<OrderResponse> {
        val response = orderService.updateOrderStatus(orderId, request.status)
        return ResponseEntity.ok(response)
    }
} 