package shb.gpark.notificationservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import shb.gpark.notificationservice.dto.NotificationRequest
import shb.gpark.notificationservice.dto.NotificationType

@SpringBootTest(classes = [shb.gpark.notificationservice.NotificationServiceApplication::class])
@AutoConfigureWebMvc
@TestPropertySource(properties = ["SLACK_WEBHOOK_URL="])
class NotificationControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var mailSender: JavaMailSender

    @Test
    fun `이메일 알림 전송 API 테스트`() {
        val request = NotificationRequest(
            type = NotificationType.EMAIL,
            to = "test@example.com",
            subject = "테스트 제목",
            message = "테스트 내용"
        )
        mockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("이메일 발송 성공"))
    }

    @Test
    fun `슬랙 알림 전송 API 테스트`() {
        val request = NotificationRequest(
            type = NotificationType.SLACK,
            to = "#general",
            subject = "슬랙 제목",
            message = "슬랙 내용"
        )
        mockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Webhook URL")))
    }

    @Test
    fun `잘못된 요청 - 필수 필드 누락`() {
        val invalidRequest = mapOf(
            "type" to "EMAIL",
            "to" to "test@example.com"
            // subject, message 누락
        )
        mockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `헬스 체크 API 테스트`() {
        mockMvc.perform(get("/api/notifications/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.service").value("notification-service"))
    }
} 