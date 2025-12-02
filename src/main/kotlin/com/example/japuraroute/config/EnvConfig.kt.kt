package com.example.japuraroute.config

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.net.URI
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


        val cleanUriString = databaseUrl.replace(Regex("^postgres(?:ql)?://"), "http://")
            .replace("jdbc:postgresql://", "http://")

        val dbUri = URI(cleanUriString)

        val username = dotenv["DB_USERNAME"]
            ?: throw IllegalStateException("DB_USERNAME is not set in environment variables")
        val password = dotenv["DB_PASSWORD"]
            ?: throw IllegalStateException("DB_PASSWORD is not set in environment variables")

        val host = dbUri.host
        val port = if (dbUri.port > 0) dbUri.port else 5432
        val dbName = dbUri.path.removePrefix("/")


        var query = dbUri.query.orEmpty()
        if (!query.contains("sslmode=require")) {
            query = if (query.isNotEmpty()) "$query&sslmode=require" else "sslmode=require"
        }

        val cleanUrl = "jdbc:postgresql://$host:$port/$dbName?$query"

        return DriverManagerDataSource().apply {
            setDriverClassName("org.postgresql.Driver")
            url = cleanUrl
            this.username = username
            this.password = password
        }
    }
}