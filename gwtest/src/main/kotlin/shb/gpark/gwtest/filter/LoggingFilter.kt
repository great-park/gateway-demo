package shb.gpark.gwtest.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class LoggingFilter : GlobalFilter, Ordered {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain
    ): Mono<Void> {
        val request = exchange.request
        val method = request.method?.toString() ?: "UNKNOWN"
        val uri = request.uri?.toString() ?: "UNKNOWN"
        val remoteAddress = request.remoteAddress?.address?.hostAddress ?: "UNKNOWN"
        
        log.info(">>> REQUEST: [{} {}] from IP: {}", method, uri, remoteAddress)

        return chain.filter(exchange).doFinally {
            val response = exchange.response
            val statusCode = response.statusCode?.value() ?: 0
            log.info("<<< RESPONSE: [status: {}] for [{} {}]", statusCode, method, uri)
        }
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }
}