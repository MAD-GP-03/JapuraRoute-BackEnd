package com.example.japuraroute.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "do.spaces")
data class DOSpaceConfig(
    var endpoint: String = "",
    var accessKey: String = "",
    var secretKey: String = "",
    var region: String = "",
    var bucketName: String = "",
    var baseUrl: String = ""
)
