package com.example.japuraroute.config

import com.example.japuraroute.module.user.repository.UserRepository
import com.example.japuraroute.common.util.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.example.japuraroute.common.util.JwtAuthenticationEntryPoint

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val userRepository: UserRepository
) {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerKotlinModule()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            val user = userRepository.findByEmail(username)
                ?: throw UsernameNotFoundException("User not found")

            // Map our User entity to Spring Security's UserDetails
            org.springframework.security.core.userdetails.User.builder()
                .username(user.email)
                .password(user.passwordHash)
                .roles(user.role.name) // Use actual role from database
                .build()
        }
    }

    @Bean
    fun authenticationProvider(userDetailsService: UserDetailsService): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        @Lazy jwtAuthFilter: JwtAuthenticationFilter,
        authProvider: AuthenticationProvider,
        @Lazy jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/**").permitAll() // Open for Login/Register
                    .requestMatchers("/api/health").permitAll() // Health check endpoint
                    .requestMatchers("/error").permitAll() // Error endpoint
                    // ✅ Allow Actuator endpoints
                    .requestMatchers(
                        "/actuator/health/**",
                        "/actuator/health",
                        "/actuator/info",
                        "/actuator/metrics"
                    ).permitAll()
                    // ✅ Allow Swagger UI and API Docs
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll()
                    .anyRequest().authenticated() // Block everything else
            }
            .exceptionHandling { exceptions ->

                exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
            .sessionManagement {
                // CRITICAL: Stateless session for JWT
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}