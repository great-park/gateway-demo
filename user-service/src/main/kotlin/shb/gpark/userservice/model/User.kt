package shb.gpark.userservice.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @field:NotBlank(message = "이름은 필수입니다")
    @Column(nullable = false)
    val name: String = "",
    
    @field:Email(message = "올바른 이메일 형식이어야 합니다")
    @field:NotBlank(message = "이메일은 필수입니다")
    @Column(unique = true, nullable = false)
    val email: String = "",
    
    @Column(nullable = false)
    val password: String = "",
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole = UserRole.USER,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    val isActive: Boolean = true
) {
    constructor() : this(null, "", "", "", UserRole.USER, LocalDateTime.now(), LocalDateTime.now(), true)
}

enum class UserRole {
    ADMIN, USER, GUEST
} 