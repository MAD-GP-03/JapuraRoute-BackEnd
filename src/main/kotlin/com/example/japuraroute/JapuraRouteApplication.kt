package com.example.japuraroute

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.example.japuraroute.module.user.repository"])
class JapuraRouteApplication

fun main(args: Array<String>) {
    runApplication<JapuraRouteApplication>(*args)
}
