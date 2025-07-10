package shb.gpark.productservice.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import shb.gpark.productservice.dto.CreateProductRequest
import shb.gpark.productservice.repository.ProductRepository
import java.math.BigDecimal

@SpringBootTest
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
        productRepository.deleteAll()
    }

    @Test
    @DisplayName("상품 생성 API 테스트")
    fun createProduct() {
        val request = CreateProductRequest(
            name = "테스트 상품",
            description = "테스트 설명",
            price = BigDecimal(10000),
            stock = 50,
            category = "테스트",
            imageUrl = null
        )

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated)
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.name").value("테스트 상품"))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.price").value(10000))
    }

    @Test
    @DisplayName("상품 목록 조회 API 테스트")
    fun getAllProducts() {
        // Given: 상품 데이터 생성
        val request1 = CreateProductRequest("상품1", "설명1", BigDecimal(1000), 10, "카테고리1", null)
        val request2 = CreateProductRequest("상품2", "설명2", BigDecimal(2000), 20, "카테고리2", null)

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        )

        // When & Then: 상품 목록 조회
        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products")
        )
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data").isArray())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.length()").value(2))
    }

    @Test
    @DisplayName("상품 상세 조회 API 테스트")
    fun getProductById() {
        // Given: 상품 생성
        val request = CreateProductRequest("테스트 상품", "설명", BigDecimal(1000), 10, "카테고리", null)
        
        val createResponse = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val responseContent = createResponse.response.contentAsString
        val responseMap = objectMapper.readValue(responseContent, Map::class.java)
        val productId = (responseMap["data"] as Map<*, *>)["id"]

        // When & Then: 상품 상세 조회
        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products/$productId")
        )
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.name").value("테스트 상품"))
    }

    @Test
    @DisplayName("상품 검색 API 테스트")
    fun searchProducts() {
        // Given: 상품들 생성
        val request1 = CreateProductRequest("노트북", "설명", BigDecimal(1000), 10, "전자제품", null)
        val request2 = CreateProductRequest("스마트폰", "설명", BigDecimal(2000), 20, "전자제품", null)

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1))
        )

        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2))
        )

        // When & Then: 검색
        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products/search")
                .param("name", "노트북")
        )
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data[0].name").value("노트북"))
    }

    @Test
    @DisplayName("재고 수정 API 테스트")
    fun updateStock() {
        // Given: 상품 생성
        val request = CreateProductRequest("테스트 상품", "설명", BigDecimal(1000), 10, "카테고리", null)
        
        val createResponse = mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated)
            .andReturn()

        val responseContent = createResponse.response.contentAsString
        val responseMap = objectMapper.readValue(responseContent, Map::class.java)
        val productId = (responseMap["data"] as Map<*, *>)["id"]

        // When & Then: 재고 수정
        val stockUpdateRequest = mapOf("stock" to 99)
        
        mockMvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/products/$productId/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdateRequest))
        )
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.success").value(true))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data.stock").value(99))
    }
} 