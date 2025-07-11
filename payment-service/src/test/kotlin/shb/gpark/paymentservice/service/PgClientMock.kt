package shb.gpark.paymentservice.service

import shb.gpark.paymentservice.dto.PaymentRequest
import shb.gpark.paymentservice.dto.PaymentResponse

class PgClientMock : PgClient {
    override fun approvePayment(request: PaymentRequest): PaymentResponse {
        // 테스트에서는 항상 성공 반환
        return PaymentResponse(
            success = true,
            transactionId = "MOCK_TXN_12345",
            message = "테스트 결제 성공",
            status = "COMPLETED"
        )
    }
} 