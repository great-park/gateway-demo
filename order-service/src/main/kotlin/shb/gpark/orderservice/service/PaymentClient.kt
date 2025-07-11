package shb.gpark.orderservice.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.math.BigDecimal

@Service
class PaymentClient(
    private val webClient: WebClient
) {
    suspend fun processPayment(request: PaymentRequest): PaymentResponse {
        return webClient.post()
            .uri("/api/payments/process")
            .bodyValue(request)
            .retrieve()
            .awaitBody()
    }
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