package shb.gpark.paymentservice.service

import shb.gpark.paymentservice.dto.PaymentRequest
import shb.gpark.paymentservice.dto.PaymentResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType

interface PgClient {
    fun approvePayment(request: PaymentRequest): PaymentResponse
}

@Component
class DummyPgClient : PgClient {
    override fun approvePayment(request: PaymentRequest): PaymentResponse {
        // 실제 운영에서는 외부 PG사 API 연동 (RestTemplate/WebClient 등)
        // 여기서는 항상 성공하는 더미 구현
        return PaymentResponse(
            success = true,
            transactionId = "TXN_${System.currentTimeMillis()}",
            message = "PG 결제 승인 성공",
            status = "COMPLETED"
        )
    }
}

@Component
class PgWebClient(
    @Value(" {pg.api.url:http://localhost:9000/pg/approve}")
    private val pgApiUrl: String
) : PgClient {
    private val webClient = WebClient.create()
    override fun approvePayment(request: PaymentRequest): PaymentResponse {
        // 실제 PG사 API 연동 예시 (동기 블록)
        return webClient.post()
            .uri(pgApiUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(PaymentResponse::class.java)
            .block() ?: PaymentResponse(false, null, "PG 연동 실패")
    }
} 