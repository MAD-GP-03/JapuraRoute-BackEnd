package com.example.japuraroute.module.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management endpoints (Protected)")
class UserController {

    @Operation(
        summary = "Get current user profile",
        description = "Returns the profile of the currently authenticated user. Requires JWT token.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved user profile"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token"
            )
        ]
    )
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "message" to "User profile retrieved successfully",
                "username" to authentication.name,
                "authorities" to authentication.authorities.map { it.authority }
            )
        )
    }

    @Operation(
        summary = "Get all users",
        description = "Returns a list of all users. Requires JWT token.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved users list"
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token"
            )
        ]
    )
    @GetMapping
    fun getAllUsers(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(
            mapOf(
                "message" to "This is a protected endpoint",
                "data" to "List of users would be here"
            )
        )
    }
}

