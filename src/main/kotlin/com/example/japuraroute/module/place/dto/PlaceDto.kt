package com.example.japuraroute.module.place.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.time.LocalTime

data class CreatePlaceDto(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    val name: String,

    @field:Size(max = 500, message = "Short description must not exceed 500 characters")
    val shortDescription: String? = null,

    @field:Size(max = 5000, message = "Description must not exceed 5000 characters")
    val description: String? = null,

    @field:Size(max = 20, message = "Maximum 20 tags allowed")
    val tags: List<@NotBlank(message = "Tag cannot be blank") String> = emptyList(),

    @field:Size(max = 200, message = "Location must not exceed 200 characters")
    val location: String? = null,

    @field:Size(max = 500, message = "Address must not exceed 500 characters")
    val address: String? = null,

    @field:DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    val latitude: Double? = null,

    @field:DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    val longitude: Double? = null,

    @field:Pattern(regexp = "^[+]?[0-9\\s\\-()]+\$", message = "Invalid phone number format")
    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    val contactPhone: String? = null,

    @field:Pattern(
        regexp = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\$",
        message = "Invalid URL format"
    )
    @field:Size(max = 1000, message = "Website URL must not exceed 1000 characters")
    val websiteUrl: String? = null,

    @field:Size(max = 50, message = "Maximum 50 images allowed")
    val images: List<@NotBlank(message = "Image URL cannot be blank") String> = emptyList(),

    @field:Valid
    @field:Size(max = 7, message = "Maximum 7 operating hours (one per day)")
    val operatingHours: List<OperatingHourDto>? = null
)

data class UpdatePlaceDto(
    @field:Size(min = 2, max = 200, message = "Name must be between 2 and 200 characters")
    val name: String? = null,

    @field:Size(max = 500, message = "Short description must not exceed 500 characters")
    val shortDescription: String? = null,

    @field:Size(max = 5000, message = "Description must not exceed 5000 characters")
    val description: String? = null,

    @field:Size(max = 20, message = "Maximum 20 tags allowed")
    val tags: List<String>? = null,

    @field:Size(max = 200, message = "Location must not exceed 200 characters")
    val location: String? = null,

    @field:Size(max = 500, message = "Address must not exceed 500 characters")
    val address: String? = null,

    @field:DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    val latitude: Double? = null,

    @field:DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    val longitude: Double? = null,

    @field:Pattern(regexp = "^[+]?[0-9\\s\\-()]+\$", message = "Invalid phone number format")
    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    val contactPhone: String? = null,

    @field:Pattern(
        regexp = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\$",
        message = "Invalid URL format"
    )
    @field:Size(max = 1000, message = "Website URL must not exceed 1000 characters")
    val websiteUrl: String? = null,

    @field:Size(max = 50, message = "Maximum 50 images allowed")
    val images: List<String>? = null,

    @field:Valid
    @field:Size(max = 7, message = "Maximum 7 operating hours")
    val operatingHours: List<OperatingHourDto>? = null
)

data class PlaceResponseDto(
    val id: String?,
    val name: String,
    val shortDescription: String?,
    val description: String?,
    val tags: List<String>,
    val location: String?,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val contactPhone: String?,
    val websiteUrl: String?,
    val images: List<String>,
    val operatingHours: List<OperatingHourDto>,
    val rating: Double?,
    val ratingCount: Int,
    val createdAt: String?,
    val createdBy: String?,
    val updatedAt: String?,
    val updatedBy: String?
)

data class OperatingHourDto(
    @field:NotBlank(message = "Day is required")
    @field:Pattern(
        regexp = "^(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)$",
        message = "Day must be a valid day of the week"
    )
    val day: String,

    @JsonFormat(pattern = "HH:mm:ss")
    val startTime: LocalTime? = null,

    @JsonFormat(pattern = "HH:mm:ss")
    val endTime: LocalTime? = null,

    @field:Size(max = 200, message = "Note must not exceed 200 characters")
    val note: String? = null
)

data class PlaceSearchCriteria(
    @field:Size(max = 200, message = "Name search must not exceed 200 characters")
    val name: String? = null,

    @field:Size(max = 10, message = "Maximum 10 tags for search")
    val tags: List<String>? = null,

    @field:Size(max = 200, message = "Location search must not exceed 200 characters")
    val location: String? = null,

    @field:DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @field:DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    val minRating: Double? = null
)

data class ImageRequestDto(
    @field:NotBlank(message = "Image URL is required")
    @field:Size(max = 1000, message = "Image URL must not exceed 1000 characters")
    val imageUrl: String
)

data class RatingRequestDto(
    @field:NotNull(message = "Rating is required")
    @field:DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @field:DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    var rating: Double
)

