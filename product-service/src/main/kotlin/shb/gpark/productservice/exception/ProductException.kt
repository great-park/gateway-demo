package shb.gpark.productservice.exception

sealed class ProductException(message: String) : RuntimeException(message)

class ProductNotFoundException(id: Long) : ProductException("상품을 찾을 수 없습니다. ID: $id")

class ProductAlreadyExistsException(name: String) : ProductException("이미 존재하는 상품명입니다: $name")

class InvalidProductDataException(message: String) : ProductException("잘못된 상품 데이터: $message")

class InsufficientStockException(productId: Long, requested: Int, available: Int) : 
    ProductException("재고가 부족합니다. 상품 ID: $productId, 요청: $requested, 보유: $available")

class InvalidPriceException(price: String) : ProductException("잘못된 가격입니다: $price")

class InvalidStockException(stock: Int) : ProductException("잘못된 재고 수량입니다: $stock") 