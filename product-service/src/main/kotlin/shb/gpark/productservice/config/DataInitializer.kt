package shb.gpark.productservice.config

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import shb.gpark.productservice.dto.CreateProductRequest
import shb.gpark.productservice.service.ProductService
import java.math.BigDecimal

@Component
class DataInitializer(
    private val productService: ProductService
) : CommandLineRunner {
    
    override fun run(vararg args: String?) {
        // 기본 상품 데이터 생성
        val sampleProducts = listOf(
            CreateProductRequest(
                name = "MacBook Pro 16인치",
                description = "Apple M3 Pro 칩을 탑재한 최신 MacBook Pro 16인치 모델입니다.",
                price = BigDecimal("3500000"),
                stock = 15,
                category = "노트북",
                imageUrl = "https://example.com/macbook-pro.jpg"
            ),
            CreateProductRequest(
                name = "iPhone 15 Pro",
                description = "A17 Pro 칩과 티타늄 디자인을 적용한 최신 iPhone입니다.",
                price = BigDecimal("1500000"),
                stock = 25,
                category = "스마트폰",
                imageUrl = "https://example.com/iphone-15-pro.jpg"
            ),
            CreateProductRequest(
                name = "Samsung Galaxy S24 Ultra",
                description = "S Pen이 내장된 삼성의 최고급 스마트폰입니다.",
                price = BigDecimal("1800000"),
                stock = 20,
                category = "스마트폰",
                imageUrl = "https://example.com/galaxy-s24-ultra.jpg"
            ),
            CreateProductRequest(
                name = "iPad Air 5세대",
                description = "M1 칩을 탑재한 가벼운 iPad Air입니다.",
                price = BigDecimal("900000"),
                stock = 30,
                category = "태블릿",
                imageUrl = "https://example.com/ipad-air.jpg"
            ),
            CreateProductRequest(
                name = "AirPods Pro 2세대",
                description = "액티브 노이즈 캔슬링이 적용된 무선 이어폰입니다.",
                price = BigDecimal("350000"),
                stock = 50,
                category = "액세서리",
                imageUrl = "https://example.com/airpods-pro.jpg"
            ),
            CreateProductRequest(
                name = "Apple Watch Series 9",
                description = "S9 칩을 탑재한 최신 Apple Watch입니다.",
                price = BigDecimal("550000"),
                stock = 8,
                category = "웨어러블",
                imageUrl = "https://example.com/apple-watch.jpg"
            ),
            CreateProductRequest(
                name = "Dell XPS 13",
                description = "인피니티 엣지 디스플레이를 적용한 울트라북입니다.",
                price = BigDecimal("2200000"),
                stock = 12,
                category = "노트북",
                imageUrl = "https://example.com/dell-xps.jpg"
            ),
            CreateProductRequest(
                name = "Sony WH-1000XM5",
                description = "업계 최고 수준의 노이즈 캔슬링 헤드폰입니다.",
                price = BigDecimal("450000"),
                stock = 18,
                category = "액세서리",
                imageUrl = "https://example.com/sony-wh1000xm5.jpg"
            )
        )
        
        // 기존 상품이 없을 때만 기본 데이터 생성
        val existingProducts = productService.getAllProducts()
        if (existingProducts.isEmpty()) {
            sampleProducts.forEach { productRequest ->
                try {
                    productService.createProduct(productRequest)
                } catch (e: Exception) {
                    // 중복 키 에러 등은 무시
                }
            }
            println("✅ Product Service 기본 데이터가 성공적으로 생성되었습니다.")
        }
    }
} 