package shb.gpark.gwtest.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

@Component
class AuthFilter : AbstractGatewayFilterFactory<AuthFilter.Config>(Config::class.java){
    private val log = LoggerFactory.getLogger(javaClass)

    class Config

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val request = exchange.request
            val authHeader = request.headers.getFirst("Authorization")

            if (authHeader.isNullOrBlank() || !isValidToken(authHeader)) {
                log.warn("Unauthorized access attempt to: {}", request.uri)
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return@GatewayFilter exchange.response.setComplete()
            }

            return@GatewayFilter chain.filter(exchange)
        }
    }

    private fun isValidToken(token: String): Boolean {
        // 실전에서는 JWT 파싱 또는 DB 확인이 들어가겠지만, 지금은 단순 체크
        return token == "Bearer chanho123"
    }
}