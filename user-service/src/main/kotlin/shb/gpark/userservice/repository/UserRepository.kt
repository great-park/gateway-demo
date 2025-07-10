package shb.gpark.userservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import shb.gpark.userservice.model.User
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun findByEmailAndIsActiveTrue(email: String): Optional<User>
    fun findAllByIsActiveTrue(): List<User>
} 