package com.example.japuraroute.config

import org.springframework.context.annotation.Configuration

/**
 * Actuator Configuration
 * Spring Boot automatically provides database health indicators
 * Access at: /actuator/health
 */
@Configuration
class ActuatorConfig {
    // Spring Boot auto-configures health indicators including database health
    // No custom code needed - just configure via application.properties
}

