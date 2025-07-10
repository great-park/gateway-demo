package shb.gpark.orderservice.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.math.BigDecimal

@FeignClient(name = "payment-service", url = "\${payment.service.url:http://localhost:8085}")
interface PaymentClient {
    @PostMapping("/api/payments/process")
    fun processPayment(@RequestBody request: PaymentRequest): PaymentResponse
}

data class PaymentRequest(
    val orderId: Long,
    val userId: Long,
    val amount: BigDecimal,
    val description: String
)

data class PaymentResponse(
    val success: Boolean,
    val transactionId: String?,
    val message: String?
) 