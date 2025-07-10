package shb.gpark.productservice.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import shb.gpark.productservice.dto.ApiResponse
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFoundException(ex: ProductNotFoundException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse(
                success = false,
                message = ex.message ?: "상품을 찾을 수 없습니다.",
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(ProductAlreadyExistsException::class)
    fun handleProductAlreadyExistsException(ex: ProductAlreadyExistsException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse(
                success = false,
                message = ex.message ?: "이미 존재하는 상품입니다.",
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(InvalidProductDataException::class)
    fun handleInvalidProductDataException(ex: InvalidProductDataException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(
                success = false,
                message = ex.message ?: "잘못된 상품 데이터입니다.",
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStockException(ex: InsufficientStockException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(
                success = false,
                message = ex.message ?: "재고가 부족합니다.",
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(InvalidPriceException::class)
    fun handleInvalidPriceException(ex: InvalidPriceException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(
                success = false,
                message = ex.message ?: "잘못된 가격입니다.",
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(InvalidStockException::class)
    fun handleInvalidStockException(ex: InvalidStockException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(
                success = false,
                message = ex.message ?: "잘못된 재고 수량입니다.",
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        val errors = ex.bindingResult.fieldErrors.associate { 
            it.field to (it.defaultMessage ?: "유효하지 않은 값입니다") 
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(
                success = false,
                message = "입력 데이터 검증에 실패했습니다.",
                data = errors,
                timestamp = LocalDateTime.now()
            ))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(
                success = false,
                message = "서버 내부 오류가 발생했습니다.",
                timestamp = LocalDateTime.now()
            ))
    }
} 