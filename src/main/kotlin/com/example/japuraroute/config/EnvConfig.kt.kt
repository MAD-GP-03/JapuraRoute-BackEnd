package com.example.japuraroute.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.stereotype.Component

/**
 * Loads .env file and makes variables available to Spring environment
 * This allows ${JDBC_URL}, ${DB_USER}, ${DB_PASS} to work in application.properties
 */
@Component
class EnvConfig : ApplicationContextInitializer<ConfigurableApplicationContext> {

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load()

        val envMap = dotenv.entries()
            .associate { it.key to it.value }

        applicationContext.environment.propertySources
            .addFirst(MapPropertySource("dotenvProperties", envMap))
    }
}