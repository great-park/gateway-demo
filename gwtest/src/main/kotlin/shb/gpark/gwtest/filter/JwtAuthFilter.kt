package shb.gpark.gwtest.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import shb.gpark.gwtest.service.JwtService

@Component
class JwtAuthFilter : AbstractGatewayFilterFactory<JwtAuthFilter.Config>(Config::class.java) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    private val jwtService = JwtService(shb.gpark.gwtest.config.JwtConfig())

    class Config(
        val requiredRoles: List<String> = listOf("USER")
    )

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val request = exchange.request
            val authHeader = request.headers.getFirst("Authorization")

            if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid or missing Authorization header for: {}", request.uri)
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return@GatewayFilter exchange.response.setComplete()
            }

            val token = authHeader.substring(7) // "Bearer " 제거

            // JWT 토큰 검증
            if (!jwtService.validateToken(token)) {
                log.warn("Invalid JWT token for: {}", request.uri)
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return@GatewayFilter exchange.response.setComplete()
            }

            // 토큰 만료 확인
            if (jwtService.isTokenExpired(token)) {
                log.warn("Expired JWT token for: {}", request.uri)
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return@GatewayFilter exchange.response.setComplete()
            }

            // 역할 확인
            val userRoles = jwtService.getRolesFromToken(token)
            val hasRequiredRole = config.requiredRoles.any { requiredRole ->
                userRoles.contains(requiredRole)
            }

            if (!hasRequiredRole) {
                log.warn("Insufficient permissions for user roles: {} at: {}", userRoles, request.uri)
                exchange.response.statusCode = HttpStatus.FORBIDDEN
                return@GatewayFilter exchange.response.setComplete()
            }

            // 사용자 정보를 헤더에 추가
            val username = jwtService.getUsernameFromToken(token)
            exchange.request.mutate()
                .header("X-User-Name", username)
                .header("X-User-Roles", userRoles.joinToString(","))
                .build()

            log.info("JWT authentication successful for user: {} at: {}", username, request.uri)
            chain.filter(exchange)
        }
    }
} 