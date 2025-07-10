package shb.gpark.userservice.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import shb.gpark.userservice.model.User
import shb.gpark.userservice.model.UserRole
import shb.gpark.userservice.repository.UserRepository
import java.time.LocalDateTime
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getAllUsers(): List<User> {
        return userRepository.findAllByIsActiveTrue()
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): Optional<User> {
        return userRepository.findByEmailAndIsActiveTrue(email)
    }

    @Transactional
    fun createUser(user: User): User {
        // 이메일 중복 체크
        if (userRepository.findByEmail(user.email).isPresent) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다: ${user.email}")
        }
        
        return userRepository.save(user)
    }

    @Transactional
    fun updateUser(id: Long, updatedUser: User): Optional<User> {
        return userRepository.findById(id).map { existingUser ->
            existingUser.copy(
                name = updatedUser.name,
                email = updatedUser.email,
                role = updatedUser.role,
                updatedAt = LocalDateTime.now()
            )
        }.map { userRepository.save(it) }
    }

    @Transactional
    fun deleteUser(id: Long): Boolean {
        return userRepository.findById(id).map { user ->
            userRepository.save(user.copy(isActive = false))
            true
        }.orElse(false)
    }

    @Transactional
    fun createDefaultUsers() {
        if (userRepository.count() == 0L) {
            val adminUser = User(
                name = "관리자",
                email = "admin@example.com",
                password = "admin123",
                role = UserRole.ADMIN
            )
            
            val testUser = User(
                name = "테스트 사용자",
                email = "test@example.com",
                password = "test123",
                role = UserRole.USER
            )
            
            userRepository.save(adminUser)
            userRepository.save(testUser)
        }
    }
} 