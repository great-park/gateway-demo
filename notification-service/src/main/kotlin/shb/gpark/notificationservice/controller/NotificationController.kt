package shb.gpark.notificationservice.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.notificationservice.dto.NotificationRequest
import shb.gpark.notificationservice.dto.NotificationResponse
import shb.gpark.notificationservice.service.NotificationService

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
    @PostMapping
    fun sendNotification(@Valid @RequestBody request: NotificationRequest): ResponseEntity<NotificationResponse> {
        val response = notificationService.sendNotification(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> =
        ResponseEntity.ok(mapOf("status" to "UP", "service" to "notification-service"))
} 