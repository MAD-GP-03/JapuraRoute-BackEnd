package com.example.japuraroute.common.util

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtUtil,
    @Lazy private val userDetailsService: UserDetailsService,
    private val authenticationEntryPoint: JwtAuthenticationEntryPoint
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val jwt = authHeader.substring(7) // Remove "Bearer " prefix
            val userEmail = jwtService.extractUsername(jwt)

            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = this.userDetailsService.loadUserByUsername(userEmail)

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
            filterChain.doFilter(request, response)
        } catch (ex: ExpiredJwtException) {
            // Token expired - return 401 instead of 500
            authenticationEntryPoint.commence(
                request,
                response,
                InsufficientAuthenticationException("JWT token has expired. Please login again.")
            )
        } catch (ex: SignatureException) {
            // Invalid signature - return 401
            authenticationEntryPoint.commence(
                request,
                response,
                InsufficientAuthenticationException("Invalid JWT signature")
            )
        } catch (ex: MalformedJwtException) {
            // Malformed token - return 401
            authenticationEntryPoint.commence(
                request,
                response,
                InsufficientAuthenticationException("Malformed JWT token")
            )
        } catch (ex: JwtException) {
            // Other JWT errors - return 401
            authenticationEntryPoint.commence(
                request,
                response,
                InsufficientAuthenticationException("Invalid JWT token: ${ex.message}")
            )
        } catch (ex: IllegalArgumentException) {
            // Empty or null claims - return 401
            authenticationEntryPoint.commence(
                request,
                response,
                InsufficientAuthenticationException("JWT claims string is empty")
            )
        }
    }
}