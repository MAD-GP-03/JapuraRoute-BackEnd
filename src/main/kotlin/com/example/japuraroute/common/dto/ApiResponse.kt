package com.example.japuraroute.common.dto

data class ApiResponse<T>(
    val status: Boolean,
    val message: String? = null,
    val data: T? = null
)

