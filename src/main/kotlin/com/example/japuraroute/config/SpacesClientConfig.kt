package com.example.japuraroute.config

import org.hibernate.internal.CoreLogging.logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class SpacesClientConfig (
    private val spacesConfig: DOSpaceConfig
){

    private val logger = LoggerFactory.getLogger(SpacesClientConfig::class.java)
    @Bean
    fun S3Client(): S3Client {

        val credentials = AwsBasicCredentials.create(
            spacesConfig.accessKey,
            spacesConfig.secretKey
        )

        return S3Client.builder()
            .endpointOverride(URI.create(spacesConfig.endpoint))
            .region(Region.of(spacesConfig.region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }

}