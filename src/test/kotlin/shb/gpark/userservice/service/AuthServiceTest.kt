package shb.gpark.userservice.service

import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import shb.gpark.userservice.config.JwtConfig
import shb.gpark.userservice.dto.LoginRequest
import shb.gpark.userservice.dto.RegisterRequest
import shb.gpark.userservice.model.User
import shb.gpark.userservice.model.UserRole
import shb.gpark.userservice.repository.UserRepository
import shb.gpark.userservice.util.JwtUtil
import java.util.*

class AuthServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: BCryptPasswordEncoder
    private lateinit var jwtConfig: JwtConfig
    private lateinit var jwtUtil: JwtUtil
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        passwordEncoder = BCryptPasswordEncoder()
        jwtConfig = JwtConfig()
        jwtUtil = JwtUtil(jwtConfig)
        authService = AuthService(userRepository, passwordEncoder, jwtUtil)
    }

    @Test
    fun `회원가입 성공`() {
        val request = RegisterRequest(
            name = "테스트유저",
            email = "test@example.com",
            password = "password123",
            role = UserRole.USER
        )
        given(userRepository.existsByEmail(request.email)).willReturn(false)
        given(userRepository.save(org.mockito.kotlin.any())).willAnswer { it.arguments[0] as User }

        val response = authService.register(request)
        assertNotNull(response.token)
        assertEquals(request.email, response.user.email)
        assertEquals(request.name, response.user.name)
        assertEquals(request.role, response.user.role)
    }

    @Test
    fun `로그인 성공`() {
        val rawPassword = "password123"
        val encodedPassword = passwordEncoder.encode(rawPassword)
        val user = User(
            id = 1L,
            name = "테스트유저",
            email = "test@example.com",
            password = encodedPassword,
            role = UserRole.USER,
            isActive = true
        )
        given(userRepository.findByEmail(user.email)).willReturn(user)

        val request = LoginRequest(email = user.email, password = rawPassword)
        val response = authService.login(request)
        assertNotNull(response.token)
        assertEquals(user.email, response.user.email)
    }

    @Test
    fun `토큰 검증 성공`() {
        val email = "test@example.com"
        val token = jwtUtil.generateToken(email, listOf(UserRole.USER.name))
        val result = authService.validateToken(token)
        assertTrue(result.valid)
        assertEquals(email, result.username)
        assertEquals(listOf(UserRole.USER.name), result.roles)
    }
} 