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

    private val log = LoggerFactory.getLogger(javaClass);

    override fun filter(
        exchange: ServerWebExchange?,
        chain: GatewayFilterChain?
    ): Mono<Void?>? {
        val request = exchange?.request
        if (request != null) {
            log.info(">>> REQUEST: [{} {}] from IP: {}", request.method, request.uri, request.remoteAddress)
        }

        return chain?.filter(exchange)?.doFinally {
            val response = exchange?.response
            log.info("<<< RESPONSE: [status: {}] for [{} {}]",
                request?.method, request?.uri
            )
        }
    }

    override fun getOrder(): Int {
        return -1 // 다른 필터보다 먼저 실행되도록
    }
}