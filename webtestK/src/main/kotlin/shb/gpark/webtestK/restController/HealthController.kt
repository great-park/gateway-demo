package shb.gpark.webtestK.restController

import org.springframework.web.bind.annotation.*

@RestController
class HealthController {

    @GetMapping("/hello")
    fun healthCheck(): Map<String, Any> {
        return mapOf(
            "message" to "Hello from Web Server",
            "status" to "UP",
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/secure/hello")
    fun secureHealthCheck(): Map<String, Any> {
        return mapOf(
            "message" to "Hello from Secure Web Server",
            "status" to "UP",
            "timestamp" to System.currentTimeMillis(),
            "security" to "AUTHENTICATED"
        )
    }

    @GetMapping("/api/users")
    fun getUsers(): List<Map<String, Any>> {
        return listOf(
            mapOf("id" to 1, "name" to "John Doe", "email" to "john@example.com"),
            mapOf("id" to 2, "name" to "Jane Smith", "email" to "jane@example.com"),
            mapOf("id" to 3, "name" to "Bob Johnson", "email" to "bob@example.com")
        )
    }

    @GetMapping("/api/users/{id}")
    fun getUserById(@PathVariable id: Long): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to "User $id",
            "email" to "user$id@example.com",
            "createdAt" to System.currentTimeMillis()
        )
    }

    @PostMapping("/api/users")
    fun createUser(@RequestBody user: Map<String, Any>): Map<String, Any> {
        return mapOf(
            "id" to (System.currentTimeMillis() % 1000),
            "name" to (user["name"] ?: "Unknown"),
            "email" to (user["email"] ?: "unknown@example.com"),
            "createdAt" to System.currentTimeMillis(),
            "status" to "CREATED"
        )
    }

    @GetMapping("/api/status")
    fun getStatus(): Map<String, Any> {
        return mapOf(
            "service" to "webtestK",
            "version" to "1.0.0",
            "status" to "RUNNING",
            "uptime" to System.currentTimeMillis(),
            "memory" to Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        )
    }
}