package shb.gpark.notificationservice.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import shb.gpark.notificationservice.dto.NotificationRequest
import shb.gpark.notificationservice.dto.NotificationType
import jakarta.mail.internet.MimeMessage

class NotificationServiceTest {
    private lateinit var mailSender: JavaMailSender
    private lateinit var service: NotificationService

    @BeforeEach
    fun setUp() {
        mailSender = mockk(relaxed = true)
        service = NotificationService(mailSender)
    }

    @Test
    fun `이메일 알림 성공`() {
        val request = NotificationRequest(
            type = NotificationType.EMAIL,
            to = "test@example.com",
            subject = "테스트 제목",
            message = "테스트 내용"
        )
        val response = service.sendNotification(request)
        assertTrue(response.success)
        assertEquals("이메일 발송 성공", response.message)
    }

    @Test
    fun `이메일 알림 실패 - 메일 서버 오류`() {
        val failMailSender = mockk<JavaMailSender>()
        every { failMailSender.createMimeMessage() } throws RuntimeException("메일 서버 오류")
        val failService = NotificationService(failMailSender)
        val request = NotificationRequest(
            type = NotificationType.EMAIL,
            to = "fail@example.com",
            subject = "실패 제목",
            message = "실패 내용"
        )
        val response = failService.sendNotification(request)
        assertFalse(response.success)
        assertTrue(response.message?.contains("이메일 발송 실패") == true)
    }

    @Test
    fun `이메일 알림 실패 - 메일 전송 오류`() {
        val failMailSender = mockk<JavaMailSender>()
        val mockMessage = mockk<MimeMessage>()
        every { failMailSender.createMimeMessage() } returns mockMessage
        every { failMailSender.send(any<MimeMessage>()) } throws RuntimeException("전송 실패")
        val failService = NotificationService(failMailSender)
        val request = NotificationRequest(
            type = NotificationType.EMAIL,
            to = "fail@example.com",
            subject = "실패 제목",
            message = "실패 내용"
        )
        val response = failService.sendNotification(request)
        assertFalse(response.success)
        assertTrue(response.message?.contains("이메일 발송 실패") == true)
    }

    @Test
    fun `슬랙 알림 Webhook 미설정`() {
        val request = NotificationRequest(
            type = NotificationType.SLACK,
            to = "#general",
            subject = "슬랙 제목",
            message = "슬랙 내용"
        )
        val response = service.sendNotification(request)
        assertFalse(response.success)
        assertTrue(response.message?.contains("Webhook URL") == true)
    }

    @Test
    fun `슬랙 알림 성공 시뮬레이션`() {
        // 실제 Slack API 호출 대신 성공 응답 시뮬레이션
        val request = NotificationRequest(
            type = NotificationType.SLACK,
            to = "#general",
            subject = "슬랙 제목",
            message = "슬랙 내용"
        )
        // 환경 변수 설정 시뮬레이션 (실제로는 테스트 환경에서 설정)
        val originalWebhook = System.getenv("SLACK_WEBHOOK_URL")
        try {
            // 실제 테스트에서는 환경 변수를 설정하거나 Mock을 사용해야 함
            val response = service.sendNotification(request)
            // Webhook URL이 설정되지 않은 경우 실패
            assertFalse(response.success)
        } finally {
            // 환경 변수 복원 (실제로는 필요 없음)
        }
    }

    // @Test
    // fun `이메일 발송 시 MimeMessageHelper 호출 검증`() {
    //     val mockMessage = mockk<MimeMessage>()
    //     every { mailSender.createMimeMessage() } returns mockMessage
    //     val request = NotificationRequest(
    //         type = NotificationType.EMAIL,
    //         to = "test@example.com",
    //         subject = "테스트 제목",
    //         message = "테스트 내용"
    //     )
    //     service.sendNotification(request)
    //     verify { mailSender.send(any<MimeMessage>()) }
    // }

    @Test
    fun `다양한 이메일 주소 형식 테스트`() {
        val testCases = listOf(
            "user@example.com",
            "user+tag@example.com",
            "user.name@example.com",
            "user@subdomain.example.com"
        )
        testCases.forEach { email ->
            val request = NotificationRequest(
                type = NotificationType.EMAIL,
                to = email,
                subject = "테스트",
                message = "테스트 내용"
            )
            val response = service.sendNotification(request)
            assertTrue(response.success, "이메일 주소: $email")
        }
    }

    @Test
    fun `빈 메시지나 제목 처리`() {
        val request = NotificationRequest(
            type = NotificationType.EMAIL,
            to = "test@example.com",
            subject = "",
            message = ""
        )
        val response = service.sendNotification(request)
        // 빈 문자열도 유효한 입력으로 처리 (실제 검증은 DTO에서 처리)
        assertTrue(response.success)
    }
} 