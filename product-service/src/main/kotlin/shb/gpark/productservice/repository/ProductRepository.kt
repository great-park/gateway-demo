package shb.gpark.productservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import shb.gpark.productservice.model.Product

interface ProductRepository : JpaRepository<Product, Long> 