package com.example.japuraroute.module.image.controller

import com.example.japuraroute.module.image.dto.*
import com.example.japuraroute.module.image.repository.ImageRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/images")
class ImageController(
    private val imageService: ImageRepository
) {

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    fun uploadImage(
        @RequestParam("file") file: MultipartFile,
        @RequestParam("folder", defaultValue = "images") folder: String
    ): ResponseEntity<ImageUploadResponse> {
        val response = imageService.uploadImage(file, folder)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/upload/multiple")
    @PreAuthorize("isAuthenticated()")
    fun uploadMultipleImages(
        @RequestParam("files") files: List<MultipartFile>,
        @RequestParam("folder", defaultValue = "images") folder: String
    ): ResponseEntity<MultipleImageUploadResponse> {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body(
                MultipleImageUploadResponse(
                    images = emptyList(),
                    totalUploaded = 0,
                    failedUploads = listOf(FailedUpload("", "No files provided"))
                )
            )
        }

        val response = imageService.uploadMultipleImages(files, folder)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    fun deleteImage(
        @RequestBody request: ImageDeleteRequest
    ): ResponseEntity<Map<String, Any>> {
        val deleted = imageService.deleteImage(request.imageKey)
        return ResponseEntity.ok(
            mapOf(
                "success" to deleted,
                "message" to "Image deleted successfully"
            )
        )
    }

    @DeleteMapping("/multiple")
    @PreAuthorize("isAuthenticated()")
    fun deleteMultipleImages(
        @RequestBody request: MultipleImageDeleteRequest
    ): ResponseEntity<MultipleImageDeleteResponse> {
        if (request.imageKeys.isEmpty()) {
            return ResponseEntity.badRequest().body(
                MultipleImageDeleteResponse(
                    deletedCount = 0,
                    failedDeletes = listOf(FailedDelete("", "No image keys provided")),
                    success = false
                )
            )
        }

        val response = imageService.deleteMultipleImages(request.imageKeys)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/url")
    fun getImageUrl(
        @RequestParam("key") imageKey: String
    ): ResponseEntity<Map<String, String>> {
        val url = imageService.getImageUrl(imageKey)
        return ResponseEntity.ok(mapOf("imageUrl" to url))
    }
}
