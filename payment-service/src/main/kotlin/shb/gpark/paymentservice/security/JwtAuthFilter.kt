package shb.gpark.paymentservice.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class JwtAuthFilter : OncePerRequestFilter() {
    private val secretKey = "my-very-secret-key-which-should-be-long-enough-for-hmac".toByteArray()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            try {
                val claims: Claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .body
                val username = claims.subject
                if (username != null) {
                    val auth = UsernamePasswordAuthenticationToken(
                        User(username, "", emptyList()),
                        null,
                        emptyList()
                    )
                    auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = auth
                }
            } catch (e: Exception) {
                // JWT 검증 실패 시 인증 미설정 (401 처리)
            }
        }
        filterChain.doFilter(request, response)
    }
} 