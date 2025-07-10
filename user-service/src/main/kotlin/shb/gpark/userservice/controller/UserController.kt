package shb.gpark.userservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import shb.gpark.userservice.model.User
import shb.gpark.userservice.service.UserService
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        return userService.getUserById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        return userService.getUserByEmail(email)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    fun createUser(@Valid @RequestBody user: User): ResponseEntity<User> {
        return try {
            ResponseEntity.ok(userService.createUser(user))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @Valid @RequestBody user: User): ResponseEntity<User> {
        return userService.updateUser(id, user)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        return if (userService.deleteUser(id)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "service" to "user-service",
                "status" to "UP",
                "timestamp" to System.currentTimeMillis(),
                "userCount" to userService.getAllUsers().size
            )
        )
    }
} 