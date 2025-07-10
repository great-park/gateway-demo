package shb.gpark.orderservice.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.orderservice.dto.*
import shb.gpark.orderservice.entity.OrderStatus
import shb.gpark.orderservice.service.OrderService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import shb.gpark.orderservice.dto.UpdateOrderStatusRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping

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

    @GetMapping("/api/orders/{orderId}")
    fun getOrderDetail(@PathVariable orderId: Long) = orderService.getOrder(orderId)

    @GetMapping("/user/{userId}")
    fun getOrdersByUser(@PathVariable userId: Long): ResponseEntity<List<OrderSummaryResponse>> {
        val response = orderService.getOrdersByUser(userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/api/orders")
    fun searchOrders(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) from: String?,
        @RequestParam(required = false) to: String?,
        @PageableDefault(size = 10) pageable: Pageable
    ) = orderService.searchOrders(userId, status, from, to, pageable)

    @PatchMapping("/api/orders/{orderId}/status")
    fun updateOrderStatus(
        @PathVariable orderId: Long,
        @RequestBody request: UpdateOrderStatusRequest
    ) = orderService.updateOrderStatus(orderId, request)
} 