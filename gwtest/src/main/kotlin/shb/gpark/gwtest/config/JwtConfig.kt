package shb.gpark.gwtest.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtConfig(
    var secret: String = "your-secret-key-here-make-it-long-and-secure-for-production",
    var expiration: Long = 86400000 // 24시간
) 