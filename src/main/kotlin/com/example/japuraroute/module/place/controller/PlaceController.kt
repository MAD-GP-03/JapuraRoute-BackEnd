package com.example.japuraroute.module.place.controller

import com.example.japuraroute.common.dto.ApiResponse
import com.example.japuraroute.module.place.dto.PlaceResponseDto
import com.example.japuraroute.module.place.dto.PlaceSearchCriteria
import com.example.japuraroute.module.place.dto.RatingRequestDto
import com.example.japuraroute.module.place.service.PlaceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/places")
@Tag(name = "Places", description = "Public place endpoints")
@SecurityRequirement(name = "bearerAuth")
class PlaceController(
    private val placeService: PlaceService
) {

    @GetMapping
    @Operation(summary = "Get all places with pagination")
    fun getAllPlaces(
        @PageableDefault(size = 20, sort = ["name"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PlaceResponseDto>>> {
        val places = placeService.getAllPlaces(pageable).map { placeService.toResponseDto(it) }
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Places retrieved successfully",
                data = places
            )
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get place by ID")
    fun getPlaceById(@PathVariable id: UUID): ResponseEntity<ApiResponse<PlaceResponseDto>> {
        return try {
            val place = placeService.getPlaceById(id)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = placeService.toResponseDto(place)
                )
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    status = false,
                    message = e.message
                )
            )
        }
    }

    @PostMapping("/search")
    @Operation(summary = "Search places with advanced criteria")
    fun searchPlaces(
        @Valid @RequestBody criteria: PlaceSearchCriteria,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PlaceResponseDto>>> {
        val places = placeService.searchPlaces(criteria, pageable).map { placeService.toResponseDto(it) }
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Search completed successfully",
                data = places
            )
        )
    }

    @GetMapping("/search/name")
    @Operation(summary = "Search places by name")
    fun searchByName(
        @RequestParam name: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PlaceResponseDto>>> {
        val places = placeService.searchByName(name, pageable).map { placeService.toResponseDto(it) }
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Places found",
                data = places
            )
        )
    }

    @GetMapping("/search/location")
    @Operation(summary = "Search places by location")
    fun searchByLocation(
        @RequestParam location: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PlaceResponseDto>>> {
        val places = placeService.searchByLocation(location, pageable).map { placeService.toResponseDto(it) }
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Places found",
                data = places
            )
        )
    }

    @GetMapping("/search/tag")
    @Operation(summary = "Search places by tag")
    fun searchByTag(
        @RequestParam tag: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PlaceResponseDto>>> {
        val places = placeService.searchByTag(tag, pageable).map { placeService.toResponseDto(it) }
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Places found",
                data = places
            )
        )
    }

    @GetMapping("/search/rating")
    @Operation(summary = "Search places by minimum rating")
    fun searchByRating(
        @RequestParam minRating: Double,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<PlaceResponseDto>>> {
        val places = placeService.findByMinRating(minRating, pageable).map { placeService.toResponseDto(it) }
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Places found",
                data = places
            )
        )
    }

    @PostMapping("/{id}/rating")
    @Operation(summary = "Add rating to place (authenticated users)")
    fun addRating(
        @PathVariable id: UUID,
        @Valid @RequestBody request: RatingRequestDto
    ): ResponseEntity<ApiResponse<PlaceResponseDto>> {
        return try {
            val place = placeService.addRating(id, request.rating)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Rating added successfully",
                    data = placeService.toResponseDto(place)
                )
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(
                    status = false,
                    message = e.message
                )
            )
        }
    }
}

