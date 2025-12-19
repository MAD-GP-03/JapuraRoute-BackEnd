package com.example.japuraroute.module.image.repository

import com.example.japuraroute.module.image.dto.ImageUploadResponse
import com.example.japuraroute.module.image.dto.MultipleImageDeleteResponse
import com.example.japuraroute.module.image.dto.MultipleImageUploadResponse
import org.springframework.web.multipart.MultipartFile

interface ImageRepository {
    fun uploadImage(file: MultipartFile, folder: String = "images"): ImageUploadResponse
    fun uploadMultipleImages(files: List<MultipartFile>, folder: String = "images"): MultipleImageUploadResponse
    fun deleteImage(imageKey: String): Boolean
    fun deleteMultipleImages(imageKeys: List<String>): MultipleImageDeleteResponse
    fun getImageUrl(imageKey: String): String
}