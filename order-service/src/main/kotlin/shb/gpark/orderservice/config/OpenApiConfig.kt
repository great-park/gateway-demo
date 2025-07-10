package shb.gpark.orderservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Order Service API")
                .description("주문 서비스 API 명세서")
                .version("v1.0.0")
        )

    @Bean
    fun publicApi(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("order-service")
        .pathsToMatch("/api/orders/**")
        .build()
} 