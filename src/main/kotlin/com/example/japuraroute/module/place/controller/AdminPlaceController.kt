package com.example.japuraroute.module.place.controller

import com.example.japuraroute.common.dto.ApiResponse
import com.example.japuraroute.module.place.dto.*
import com.example.japuraroute.module.place.service.PlaceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/admin/places")
@Tag(name = "Admin - Place Management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN')")
class AdminPlaceController(
    private val placeService: PlaceService
) {

    @PostMapping
    @Operation(summary = "Create a new place")
    fun createPlace(
        @Valid @RequestBody request: CreatePlaceDto
    ): ResponseEntity<ApiResponse<PlaceResponseDto>> {
        return try {
            val place = placeService.createPlace(request)
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse(
                    status = true,
                    message = "Place created successfully",
                    data = placeService.toResponseDto(place)
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse(
                    status = false,
                    message = e.message
                )
            )
        }
    }

    @GetMapping
    @Operation(summary = "Get all places")
    fun getAllPlaces(): ResponseEntity<ApiResponse<List<PlaceResponseDto>>> {
        val places = placeService.getAllPlaces().map { placeService.toResponseDto(it) }
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

    @PutMapping("/{id}")
    @Operation(summary = "Update place by ID")
    fun updatePlace(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePlaceDto
    ): ResponseEntity<ApiResponse<PlaceResponseDto>> {
        return try {
            val place = placeService.updatePlace(id, request)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Place updated successfully",
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
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse(
                    status = false,
                    message = e.message
                )
            )
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete place by ID")
    fun deletePlace(@PathVariable id: UUID): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            placeService.deletePlace(id)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Place deleted successfully"
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
        @Valid @RequestBody criteria: PlaceSearchCriteria
    ): ResponseEntity<ApiResponse<List<PlaceResponseDto>>> {
        val places = placeService.searchPlaces(criteria).map { placeService.toResponseDto(it) }
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Search completed successfully",
                data = places
            )
        )
    }

    
}

