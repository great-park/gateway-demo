package shb.gpark.productservice.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "products")
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val price: BigDecimal,
    var stock: Int,
    var isActive: Boolean = true
) 