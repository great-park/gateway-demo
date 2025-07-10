package shb.gpark.gwtest.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class RateLimitFilter : AbstractGatewayFilterFactory<RateLimitFilter.Config>(Config::class.java) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    private val requestCounts = ConcurrentHashMap<String, AtomicInteger>()
    private val lastResetTime = ConcurrentHashMap<String, LocalDateTime>()

    class Config(
        val requestsPerMinute: Int = 60,
        val burstSize: Int = 10
    )

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            val clientIp = exchange.request.remoteAddress?.address?.hostAddress ?: "unknown"
            val key = "rate_limit_$clientIp"
            
            // 분 단위로 리셋
            val now = LocalDateTime.now()
            val lastReset = lastResetTime.getOrDefault(key, now.minusMinutes(1))
            
            if (ChronoUnit.MINUTES.between(lastReset, now) >= 1) {
                requestCounts[key] = AtomicInteger(0)
                lastResetTime[key] = now
            }
            
            val currentCount = requestCounts.getOrPut(key) { AtomicInteger(0) }
            val count = currentCount.incrementAndGet()
            
            log.debug("Rate limit check for IP: {}, count: {}/{}", clientIp, count, config.requestsPerMinute)
            
            if (count > config.requestsPerMinute) {
                log.warn("Rate limit exceeded for IP: {} ({} requests)", clientIp, count)
                exchange.response.statusCode = HttpStatus.TOO_MANY_REQUESTS
                exchange.response.headers.add("X-Rate-Limit-Reset", 
                    lastResetTime[key]?.plusMinutes(1)?.toString())
                return@GatewayFilter exchange.response.setComplete()
            }
            
            // Rate limit 헤더 추가
            exchange.response.headers.add("X-Rate-Limit-Limit", config.requestsPerMinute.toString())
            exchange.response.headers.add("X-Rate-Limit-Remaining", 
                (config.requestsPerMinute - count).toString())
            exchange.response.headers.add("X-Rate-Limit-Reset", 
                lastResetTime[key]?.plusMinutes(1)?.toString())
            
            chain.filter(exchange)
        }
    }
} 