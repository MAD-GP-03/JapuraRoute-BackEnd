package com.example.japuraroute.module.auth.controller

import com.example.japuraroute.module.auth.dto.LoginRequest
import com.example.japuraroute.module.auth.dto.RegisterRequest
import com.example.japuraroute.module.auth.service.AuthService
import com.example.japuraroute.common.util.JwtUtil as JwtService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and Registration endpoints")
class AuthController(
    private val authService: AuthService,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService
) {

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account and returns a JWT token"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "User registered successfully",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid input or email already exists",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<Map<String, Any>> {
        return try {
            val user = authService.register(request)
            val token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.email))

            ResponseEntity.status(HttpStatus.CREATED).body(
                mapOf(
                    "status" to true,
                    "message" to "User registered successfully",
                    "token" to token,
                    "user" to mapOf(
                        "id" to user.id,
                        "username" to user.username,
                        "email" to user.email,
                        "role" to user.role
                    )
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                mapOf(
                    "status" to false,
                    "error" to (e.message ?: "Registration failed")
                )
            )
        }
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates user credentials and returns a JWT token"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Login successful",
                content = [Content(mediaType = "application/json")]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = [Content(mediaType = "application/json")]
            )
        ]
    )
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Map<String, Any>> {
        return try {
            // Authenticate user
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )

            val user = authService.login(request)
            val token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.email))

            ResponseEntity.ok(
                mapOf(
                    "status" to true,
                    "message" to "Login successful",
                    "token" to token,
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
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                mapOf(
                    "status" to false,
                    "error" to (e.message ?: "Invalid credentials")
                )
            )
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                mapOf(
                    "status" to false,
                    "error" to "Invalid email or password"
                )
            )
        }
    }
}