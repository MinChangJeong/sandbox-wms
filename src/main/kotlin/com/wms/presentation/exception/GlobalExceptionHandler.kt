package com.wms.presentation.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

data class ErrorResponse(
    val statusCode: Int,
    val message: String,
    val code: String,
    val timestamp: String = Instant.now().toString()
)

@RestControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                statusCode = 400,
                message = e.message ?: "유효하지 않은 요청입니다",
                code = "INVALID_REQUEST"
            )
        )
    }
    
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(e: IllegalStateException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(
                statusCode = 409,
                message = e.message ?: "작업을 수행할 수 없습니다",
                code = "INVALID_STATE"
            )
        )
    }
    
    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                statusCode = 500,
                message = "서버 오류가 발생했습니다",
                code = "INTERNAL_ERROR"
            )
        )
    }
}
