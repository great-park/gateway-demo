package shb.gpark.paymentservice.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.paymentservice.dto.*
import shb.gpark.paymentservice.service.PaymentService
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    
    @Operation(summary = "결제 처리", description = "외부 PG사 연동을 통한 결제 승인 처리")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "결제 성공/실패 결과 반환")
    )
    @PostMapping("/process")
    fun processPayment(@Valid @RequestBody request: PaymentRequest): ResponseEntity<PaymentResponse> {
        val response = runBlocking { paymentService.processPayment(request) }
        return ResponseEntity.ok(response)
    }
    
    @Operation(summary = "결제 상세 조회", description = "결제 ID로 결제 상세 정보 조회")
    @GetMapping("/{paymentId}")
    fun getPayment(@Parameter(description = "결제 ID") @PathVariable paymentId: Long): ResponseEntity<PaymentDetailResponse> {
        val response = paymentService.getPayment(paymentId)
        return ResponseEntity.ok(response)
    }
    
    @Operation(summary = "주문별 결제 내역 조회", description = "주문 ID로 결제 내역 전체 조회")
    @GetMapping("/order/{orderId}")
    fun getPaymentsByOrder(@Parameter(description = "주문 ID") @PathVariable orderId: Long): ResponseEntity<List<PaymentDetailResponse>> {
        val response = paymentService.getPaymentsByOrder(orderId)
        return ResponseEntity.ok(response)
    }
    
    @Operation(summary = "사용자별 결제 내역 조회", description = "사용자 ID로 결제 내역 전체 조회")
    @GetMapping("/user/{userId}")
    fun getPaymentsByUser(@Parameter(description = "사용자 ID") @PathVariable userId: Long): ResponseEntity<List<PaymentSummaryResponse>> {
        val response = paymentService.getPaymentsByUser(userId)
        return ResponseEntity.ok(response)
    }
    
    @Operation(summary = "결제 상태 변경", description = "결제 상태(성공/실패/환불 등) 변경")
    @PatchMapping("/{paymentId}/status")
    fun updatePaymentStatus(
        @Parameter(description = "결제 ID") @PathVariable paymentId: Long,
        @Valid @RequestBody request: UpdatePaymentStatusRequest
    ): ResponseEntity<PaymentDetailResponse> {
        val response = paymentService.updatePaymentStatus(paymentId, request)
        return ResponseEntity.ok(response)
    }
    
    @Operation(summary = "결제 환불 처리", description = "결제 환불 요청 처리")
    @PostMapping("/refund")
    fun refundPayment(@Valid @RequestBody request: RefundRequest): ResponseEntity<RefundResponse> {
        val response = runBlocking { paymentService.refundPayment(request) }
        return ResponseEntity.ok(response)
    }
    
    @Operation(summary = "결제 통계 조회", description = "기간별 결제 성공/실패 통계 조회")
    @GetMapping("/statistics")
    fun getPaymentStatistics(
        @Parameter(description = "시작일(yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) startDate: String?,
        @Parameter(description = "종료일(yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) endDate: String?
    ): ResponseEntity<Map<String, Any>> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val start = startDate?.let { LocalDateTime.parse(it, formatter) } ?: LocalDateTime.now().minusDays(30)
        val end = endDate?.let { LocalDateTime.parse(it, formatter) } ?: LocalDateTime.now()
        
        val response = paymentService.getPaymentStatistics(start, end)
        return ResponseEntity.ok(response)
    }
    
    @Operation(summary = "헬스 체크", description = "서비스 상태 확인")
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "UP", "service" to "payment-service"))
    }
} 