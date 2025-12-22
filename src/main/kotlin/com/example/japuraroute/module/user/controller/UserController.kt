package com.example.japuraroute.module.user.controller

import com.example.japuraroute.module.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management endpoints (Protected)")
class UserController(
    private val userService: UserService
) {

    @Operation(
        summary = "Get current user profile",
        description = "Returns the profile of the currently authenticated user. Requires JWT token.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved user profile"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token"
            )
        ]
    )
    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        val user = userService.getUserByEmail(authentication.name)

        return if (user != null) {
            ResponseEntity.ok(
                mapOf(
                    "status" to true,
                    "message" to "User profile retrieved successfully",
                    "user" to mapOf(
                        "id" to user.id,
                        "username" to user.username,
                        "email" to user.email,
                        "role" to user.role,
                        "uniYear" to user.details?.uni_year,
                        "focusArea" to user.details?.focus_area
                    )
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                mapOf(
                    "status" to false,
                    "message" to "User not found"
                )
            )
        }
    }

    @Operation(
        summary = "Get all users",
        description = "Returns a list of all users. Requires JWT token.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved users list"
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid or missing JWT token"
            )
        ]
    )
    @GetMapping
    fun getAllUsers(): ResponseEntity<Map<String, Any>> {
        val users = userService.getAllUsers()
        val usersData = users.map { user ->
            mapOf(
                "id" to user.id,
                "username" to user.username,
                "email" to user.email,
                "role" to user.role,
                "uniYear" to user.details?.uni_year,
                "focusArea" to user.details?.focus_area
            )
        }

        return ResponseEntity.ok(
            mapOf(
                "status" to true,
                "message" to "Users retrieved successfully",
                "data" to usersData
            )
        )
    }
}

