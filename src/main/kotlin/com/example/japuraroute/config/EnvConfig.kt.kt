package com.example.japuraroute.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class EnvConfig {

    private val dotenv: Dotenv = Dotenv.configure()
        .ignoreIfMissing()
        .load()

    @Bean
    fun datasource(): DataSource {
        val databaseUrl = dotenv["DATABASE_URL"]
            ?: throw IllegalStateException("DATABASE_URL is not set in environment variables")
        val username = dotenv["DB_USERNAME"]
            ?: throw IllegalStateException("DB_USERNAME is not set in environment variables")
        val password = dotenv["DB_PASSWORD"]
            ?: throw IllegalStateException("DB_PASSWORD is not set in environment variables")

        // Parse DATABASE_URL format: host:port/database or host/database
        val parts = databaseUrl.split("/")
        val hostPort = parts[0]
        val dbName = parts.getOrNull(1) ?: "neondb"

        val host = if (hostPort.contains(":")) hostPort.split(":")[0] else hostPort
        val port = if (hostPort.contains(":")) hostPort.split(":")[1].toInt() else 5432

        // Build JDBC URL with SSL parameters for Neon
        val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName?sslmode=require&connectTimeout=60"

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            minimumIdle = 5
            connectionTimeout = 60000 // 60 seconds
            idleTimeout = 300000 // 5 minutes
            maxLifetime = 1200000 // 20 minutes
            connectionTestQuery = "SELECT 1"
        }

        return HikariDataSource(config)
    }
}