package com.example.japuraroute.common.util

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = "application/json"
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to false,
            "error" to "Unauthorized",
            "message" to (authException.message ?: "Authentication failed"),
            "exceptionType" to "AuthenticationException",
            "details" to "Please login again with valid credentials"
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}

