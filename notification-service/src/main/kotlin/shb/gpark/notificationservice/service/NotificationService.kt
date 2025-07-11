package shb.gpark.notificationservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import com.slack.api.Slack
import com.slack.api.webhook.Payload
import shb.gpark.notificationservice.dto.NotificationRequest
import shb.gpark.notificationservice.dto.NotificationResponse
import shb.gpark.notificationservice.dto.NotificationType
import org.slf4j.LoggerFactory

@Service
class NotificationService(
    private val mailSender: JavaMailSender
) {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendNotification(request: NotificationRequest): NotificationResponse {
        return when (request.type) {
            NotificationType.EMAIL -> sendEmail(request)
            NotificationType.SLACK -> sendSlack(request)
        }
    }

    fun sendEmail(request: NotificationRequest): NotificationResponse {
        return try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, false, "UTF-8")
            helper.setTo(request.to)
            helper.setSubject(request.subject)
            helper.setText(request.message, false)
            mailSender.send(message)
            logger.info("[이메일 발송 성공] to={}, subject={}", request.to, request.subject)
            NotificationResponse(true, "이메일 발송 성공")
        } catch (e: Exception) {
            logger.error("[이메일 발송 실패] to={}, subject={}, error={}", request.to, request.subject, e.message)
            NotificationResponse(false, "이메일 발송 실패: ${e.message}")
        }
    }

    fun sendSlack(request: NotificationRequest): NotificationResponse {
        return try {
            val webhookUrl = System.getenv("SLACK_WEBHOOK_URL") ?: ""
            if (webhookUrl.isBlank()) {
                logger.error("[슬랙 발송 실패] Webhook URL 미설정")
                return NotificationResponse(false, "슬랙 Webhook URL이 설정되지 않았습니다.")
            }
            val payload = Payload.builder().text("*${request.subject}*\n${request.message}").build()
            val response = Slack.getInstance().send(webhookUrl, payload)
            if (response.code == 200) {
                logger.info("[슬랙 발송 성공] to={}, subject={}", request.to, request.subject)
                NotificationResponse(true, "슬랙 발송 성공")
            } else {
                logger.error("[슬랙 발송 실패] code={}, body={}", response.code, response.body)
                NotificationResponse(false, "슬랙 발송 실패: ${response.body}")
            }
        } catch (e: Exception) {
            logger.error("[슬랙 발송 예외] error={}", e.message)
            NotificationResponse(false, "슬랙 발송 예외: ${e.message}")
        }
    }
} 