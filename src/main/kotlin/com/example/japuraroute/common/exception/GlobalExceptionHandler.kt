package com.example.japuraroute.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import com.example.japuraroute.common.exception.ImageUploadException
import com.example.japuraroute.common.exception.ImageDeleteException
import com.example.japuraroute.common.exception.InvalidFileException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import kotlin.toString

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = mutableMapOf<String, String>()

        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "Invalid value"
            errors[fieldName] = errorMessage
        }

        val response = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to false,
            "error" to "Validation Failed",
            "message" to "Invalid input data",
            "errors" to errors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, Any>> {
        val response = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to false,
            "error" to "Bad Request",
            "message" to (ex.message ?: "Invalid request")
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, Any>> {
        // Log the actual exception for debugging
        ex.printStackTrace()

        val response = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to false,
            "error" to "Internal Server Error",
            "message" to (ex.message ?: "An unexpected error occurred"),
            "exceptionType" to ex.javaClass.simpleName,
            "details" to (ex.cause?.message ?: "No additional details")
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    @ExceptionHandler(ImageUploadException::class)
    fun handleImageUploadException(ex: ImageUploadException): ResponseEntity<Map<String, Any>> {
        val response = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to false,
            "error" to "Image Upload Failed",
            "message" to (ex.message ?: "Image upload failed")
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    @ExceptionHandler(ImageDeleteException::class)
    fun handleImageDeleteException(ex: ImageDeleteException): ResponseEntity<Map<String, Any>> {
        val response = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to false,
            "error" to "Image Delete Failed",
            "message" to (ex.message ?: "Image deletion failed")
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    @ExceptionHandler(InvalidFileException::class)
    fun handleInvalidFileException(ex: InvalidFileException): ResponseEntity<Map<String, Any>> {
        val response = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to false,
            "error" to "Invalid File",
            "message" to (ex.message ?: "Invalid file")
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }


}

