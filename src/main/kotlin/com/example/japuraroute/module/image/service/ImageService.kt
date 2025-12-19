package com.example.japuraroute.module.image.service

import com.example.japuraroute.common.exception.InvalidFileException
import com.example.japuraroute.common.exception.ImageUploadException
import com.example.japuraroute.common.exception.ImageDeleteException
import com.example.japuraroute.config.DOSpaceConfig
import com.example.japuraroute.module.image.dto.FailedDelete
import com.example.japuraroute.module.image.dto.FailedUpload
import com.example.japuraroute.module.image.dto.ImageUploadResponse
import com.example.japuraroute.module.image.dto.MultipleImageDeleteResponse
import com.example.japuraroute.module.image.dto.MultipleImageUploadResponse
import com.example.japuraroute.module.image.repository.ImageRepository
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest


@Service
class ImageService (
    private val s3Client : S3Client,
    private val spacesConfig: DOSpaceConfig
) : ImageRepository {

    private val logger = LoggerFactory.getLogger(ImageService::class.java)

    companion object {
        private val ALLOWED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/gif", "image/webp")
        private const val MAX_FILE_SIZE = 5 * 1024 * 1024
    }

    override fun uploadImage( file: MultipartFile, folder : String  ) : ImageUploadResponse
    {
        validateFile(file)
        val fileName = "${folder}/${System.currentTimeMillis()}_${file.originalFilename}"
        val key = "$folder/$fileName"

        try {
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(spacesConfig.bucketName)
                .key(key)
                .acl("public-read")
                .contentType(file.contentType)
                .build()

            s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.inputStream.readAllBytes())
            )

            val imageUrl = "${spacesConfig.baseUrl}/$key"

            return ImageUploadResponse(
                imageUrl = imageUrl,
                imageKey = key,
                fileName = fileName,
                fileSize = file.size,
                contentType = file.contentType ?: "application/octet-stream"
            )

        } catch (e: Exception) {
            throw ImageUploadException("Failed to upload image: ${e.message}", e)
        }
    }

    override fun uploadMultipleImages(files: List<MultipartFile>, folder: String): MultipleImageUploadResponse {
        val uploadedImages = mutableListOf<ImageUploadResponse>()
        val failedUploads = mutableListOf<FailedUpload>()

        files.forEach { file ->
            try {
                val uploadResponse = uploadImage(file, folder)
                uploadedImages.add(uploadResponse)
            } catch (e: Exception) {
                logger.error("Failed to upload file: ${file.originalFilename}", e)
                failedUploads.add(
                    FailedUpload(
                        fileName = file.originalFilename ?: "unknown",
                        reason = e.message ?: "Unknown error"
                    )
                )
            }
        }

        return MultipleImageUploadResponse(
            images = uploadedImages,
            totalUploaded = uploadedImages.size,
            failedUploads = failedUploads
        )
    }

    override fun deleteImage(imageKey: String): Boolean {
        try {
            val deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(spacesConfig.bucketName)
                .key(imageKey)
                .build()

            s3Client.deleteObject(deleteObjectRequest)
            return true
        } catch (e: Exception) {
            throw ImageDeleteException("Failed to delete image: ${e.message}", e)
        }
    }

    override fun deleteMultipleImages(imageKeys: List<String>): MultipleImageDeleteResponse {
        val failedDeletes = mutableListOf<FailedDelete>()
        var deletedCount = 0

        imageKeys.forEach { imageKey ->
            try {
                deleteImage(imageKey)
                deletedCount++
            } catch (e: Exception) {
                logger.error("Failed to delete image: $imageKey", e)
                failedDeletes.add(
                    FailedDelete(
                        imageKey = imageKey,
                        reason = e.message ?: "Unknown error"
                    )
                )
            }
        }

        return MultipleImageDeleteResponse(
            deletedCount = deletedCount,
            failedDeletes = failedDeletes,
            success = failedDeletes.isEmpty()
        )
    }

    override fun getImageUrl(imageKey: String): String {
        return "${spacesConfig.baseUrl}/$imageKey"
    }

    private fun validateFile(file: MultipartFile) {
        if (file.isEmpty) {
            throw InvalidFileException("File is empty")
        }

        if (file.size > MAX_FILE_SIZE) {
            throw InvalidFileException("File size exceeds maximum limit of 5MB")
        }

        if (file.contentType !in ALLOWED_CONTENT_TYPES) {
            throw InvalidFileException("Invalid file type. Allowed types: JPEG, PNG, GIF, WEBP")
        }
    }
}