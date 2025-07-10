package shb.gpark.orderservice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import shb.gpark.orderservice.dto.ProductResponse
import shb.gpark.orderservice.dto.StockUpdateRequest

@Component
class ProductServiceClient(
    private val webClient: WebClient,
    @Value("\${external.product-service.url}")
    private val productServiceUrl: String
) {
    fun getProduct(productId: Long): Mono<ProductResponse> {
        return webClient.get()
            .uri("${productServiceUrl}/{id}", productId)
            .retrieve()
            .bodyToMono(ProductResponse::class.java)
    }

    fun updateStock(productId: Long, quantity: Int): Mono<Void> {
        val request = StockUpdateRequest(productId, quantity)
        return webClient.put()
            .uri("${productServiceUrl}/{id}/stock", productId)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void::class.java)
    }
} 