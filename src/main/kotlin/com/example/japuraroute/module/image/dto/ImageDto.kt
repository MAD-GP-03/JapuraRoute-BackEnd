package com.example.japuraroute.module.image.dto

data class ImageUploadResponse(
    val imageUrl: String,
    val imageKey: String,
    val fileName: String,
    val fileSize: Long,
    val contentType: String
)

data class MultipleImageUploadResponse(
    val images: List<ImageUploadResponse>,
    val totalUploaded: Int,
    val failedUploads: List<FailedUpload> = emptyList()
)

data class FailedUpload(
    val fileName: String,
    val reason: String
)

data class ImageDeleteRequest(
    val imageKey: String
)

data class MultipleImageDeleteRequest(
    val imageKeys: List<String>
)

data class MultipleImageDeleteResponse(
    val deletedCount: Int,
    val failedDeletes: List<FailedDelete> = emptyList(),
    val success: Boolean
)

data class FailedDelete(
    val imageKey: String,
    val reason: String
)