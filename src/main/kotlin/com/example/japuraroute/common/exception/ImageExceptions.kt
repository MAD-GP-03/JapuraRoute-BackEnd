package com.example.japuraroute.common.exception

class ImageUploadException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class ImageDeleteException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class InvalidFileException(message: String) : RuntimeException(message)