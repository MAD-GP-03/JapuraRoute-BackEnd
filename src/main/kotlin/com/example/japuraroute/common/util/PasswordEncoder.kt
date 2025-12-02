package com.example.japuraroute.common.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderCustom {
    private val bCryptPasswordEncoder = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String? {
        return bCryptPasswordEncoder.encode(rawPassword)
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword)
    }
}