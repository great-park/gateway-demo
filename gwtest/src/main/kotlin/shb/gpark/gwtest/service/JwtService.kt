package shb.gpark.gwtest.service

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import shb.gpark.gwtest.config.JwtConfig
import java.util.*

@Service
class JwtService(
    private val jwtConfig: JwtConfig
) {

    private val secretKey = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray())

    fun generateToken(username: String, roles: List<String> = listOf("USER")): String {
        val now = Date()
        val expiration = Date(now.time + jwtConfig.expiration)

        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: JwtException) {
            false
        }
    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
                .subject
        } catch (e: JwtException) {
            null
        }
    }

    fun getRolesFromToken(token: String): List<String> {
        return try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            @Suppress("UNCHECKED_CAST")
            claims["roles"] as? List<String> ?: listOf("USER")
        } catch (e: JwtException) {
            listOf()
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

            val expiration = claims.expiration
            expiration.before(Date())
        } catch (e: JwtException) {
            true
        }
    }
} 