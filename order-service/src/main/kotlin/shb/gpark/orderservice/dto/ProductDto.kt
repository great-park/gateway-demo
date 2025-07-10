package shb.gpark.orderservice.dto

import java.math.BigDecimal

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val category: String?,
    val isActive: Boolean
)

data class StockUpdateRequest(
    val productId: Long,
    val quantity: Int
) 