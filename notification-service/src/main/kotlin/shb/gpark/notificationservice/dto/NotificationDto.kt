package shb.gpark.notificationservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

enum class NotificationType {
    EMAIL, SLACK
}

data class NotificationRequest(
    @field:NotNull(message = "알림 타입은 필수입니다.")
    val type: NotificationType,
    @field:NotBlank(message = "수신자는 필수입니다.")
    val to: String,
    @field:NotBlank(message = "제목은 필수입니다.")
    val subject: String,
    @field:NotBlank(message = "내용은 필수입니다.")
    val message: String
)

data class NotificationResponse(
    val success: Boolean,
    val message: String? = null
) 