package com.example.japuraroute

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Import(TestConfig::class)

    @Test
    fun contextLoads() {
        // Test that the application context loads successfully
    }

