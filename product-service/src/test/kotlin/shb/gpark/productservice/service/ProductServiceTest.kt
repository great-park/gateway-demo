package shb.gpark.productservice.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import shb.gpark.productservice.dto.CreateProductRequest
import shb.gpark.productservice.dto.ProductSearchRequest
import shb.gpark.productservice.dto.ProductStockUpdateRequest
import shb.gpark.productservice.dto.UpdateProductRequest
import shb.gpark.productservice.entity.Product
import shb.gpark.productservice.repository.ProductRepository
import java.math.BigDecimal

@SpringBootTest
@Transactional
class ProductServiceTest @Autowired constructor(
    val productService: ProductService,
    val productRepository: ProductRepository
) {
    
    @BeforeEach
    fun setUp() {
        productRepository.deleteAll()
    }

    @Test
    @DisplayName("상품 생성 및 조회")
    fun createAndGetProduct() {
        val request = CreateProductRequest(
            name = "테스트 상품",
            description = "테스트 설명",
            price = BigDecimal(10000),
            stock = 50,
            category = "테스트",
            imageUrl = null
        )
        val created = productService.createProduct(request)
        val found = productService.getProductById(created.id)
        assertThat(found.name).isEqualTo("테스트 상품")
        assertThat(found.price).isEqualTo(BigDecimal(10000))
    }

    @Test
    @DisplayName("상품 목록 조회")
    fun getAllProducts() {
        val request1 = CreateProductRequest("상품1", "설명1", BigDecimal(1000), 10, "카테고리1", null)
        val request2 = CreateProductRequest("상품2", "설명2", BigDecimal(2000), 20, "카테고리2", null)
        productService.createProduct(request1)
        productService.createProduct(request2)
        val all = productService.getAllProducts()
        assertThat(all.size).isEqualTo(2)
    }

    @Test
    @DisplayName("상품 수정")
    fun updateProduct() {
        val request = CreateProductRequest("상품", "설명", BigDecimal(1000), 10, "카테고리", null)
        val created = productService.createProduct(request)
        val update = UpdateProductRequest(name = "수정상품", price = BigDecimal(2000))
        val updated = productService.updateProduct(created.id, update)
        assertThat(updated.name).isEqualTo("수정상품")
        assertThat(updated.price).isEqualTo(BigDecimal(2000))
    }

    @Test
    @DisplayName("상품 삭제")
    fun deleteProduct() {
        val request = CreateProductRequest("상품", "설명", BigDecimal(1000), 10, "카테고리", null)
        val created = productService.createProduct(request)
        productService.deleteProduct(created.id)
        val all = productService.getAllProducts()
        assertThat(all).isEmpty()
    }

    @Test
    @DisplayName("상품 검색")
    fun searchProducts() {
        productService.createProduct(CreateProductRequest("노트북", "설명", BigDecimal(1000), 10, "전자제품", null))
        productService.createProduct(CreateProductRequest("스마트폰", "설명", BigDecimal(2000), 20, "전자제품", null))
        val search = productService.searchProducts(ProductSearchRequest(name = "노트북"))
        assertThat(search.size).isEqualTo(1)
        assertThat(search[0].name).isEqualTo("노트북")
    }

    @Test
    @DisplayName("재고 수정")
    fun updateStock() {
        val created = productService.createProduct(CreateProductRequest("상품", "설명", BigDecimal(1000), 10, "카테고리", null))
        val updated = productService.updateStock(created.id, ProductStockUpdateRequest(stock = 99))
        assertThat(updated.stock).isEqualTo(99)
    }
} 