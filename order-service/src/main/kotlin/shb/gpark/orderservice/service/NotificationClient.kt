package shb.gpark.orderservice.service

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import kotlinx.coroutines.reactor.awaitSingle

@Service
class NotificationClient(
    private val webClient: WebClient
) {
    suspend fun sendSlackNotification(userId: Long, message: String): NotificationResponse {
        return webClient.post()
            .uri("/api/notifications/slack")
            .bodyValue(SlackNotificationRequest(userId, message))
            .retrieve()
            .awaitBody()
    }

    suspend fun sendEmailNotification(userId: Long, subject: String, message: String): NotificationResponse {
        return webClient.post()
            .uri("/api/notifications/email")
            .bodyValue(EmailNotificationRequest(userId, subject, message))
            .retrieve()
            .awaitBody()
    }
}

data class SlackNotificationRequest(
    val userId: Long,
    val message: String
)

data class EmailNotificationRequest(
    val userId: Long,
    val subject: String,
    val message: String
)

data class NotificationResponse(
    val success: Boolean,
    val message: String?
) 