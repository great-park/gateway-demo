package shb.gpark.gwtest.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.gwtest.service.JwtService

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any>> {
        // 간단한 인증 로직 (실제로는 DB에서 사용자 확인)
        if (isValidCredentials(loginRequest.username, loginRequest.password)) {
            val roles = when (loginRequest.username) {
                "admin" -> listOf("ADMIN", "USER")
                "user" -> listOf("USER")
                else -> listOf("USER")
            }
            
            val token = jwtService.generateToken(loginRequest.username, roles)
            
            return ResponseEntity.ok(
                mapOf(
                    "token" to (token ?: ""),
                    "username" to loginRequest.username,
                    "roles" to roles,
                    "expiresIn" to 86400000 // 24시간
                )
            )
        }
        
        return ResponseEntity.status(401).body(
            mapOf("error" to "Invalid credentials")
        )
    }

    @PostMapping("/validate")
    fun validateToken(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Map<String, Any>> {
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                mapOf("error" to "Invalid token format")
            )
        }
        
        val token = authHeader.substring(7)
        
        return if (jwtService.validateToken(token) && !jwtService.isTokenExpired(token)) {
            val username = jwtService.getUsernameFromToken(token) ?: ""
            val roles = jwtService.getRolesFromToken(token)
            
            ResponseEntity.ok(
                mapOf(
                    "valid" to true,
                    "username" to username,
                    "roles" to roles
                )
            )
        } else {
            ResponseEntity.status(401).body(
                mapOf("valid" to false, "error" to "Invalid or expired token")
            )
        }
    }

    private fun isValidCredentials(username: String, password: String): Boolean {
        // 간단한 인증 (실제로는 DB에서 확인)
        return when {
            username == "admin" && password == "admin123" -> true
            username == "user" && password == "user123" -> true
            username == "chanho" && password == "chanho123" -> true
            else -> false
        }
    }
}

data class LoginRequest(
    val username: String,
    val password: String
) 