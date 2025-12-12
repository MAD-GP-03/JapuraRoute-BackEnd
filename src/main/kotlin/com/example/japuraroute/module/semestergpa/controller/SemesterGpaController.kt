package com.example.japuraroute.module.semestergpa.controller

import com.example.japuraroute.common.dto.ApiResponse
import com.example.japuraroute.module.module.model.SEMETSER_ID
import com.example.japuraroute.module.semestergpa.dto.*
import com.example.japuraroute.module.semestergpa.service.SemesterGpaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/admin/semester-gpa")
@Tag(name = "Admin - Semester GPA Management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
class AdminSemesterGpaController(
    private val semesterGpaService: SemesterGpaService
) {

    @PostMapping("/user/{userId}")
    @Operation(summary = "Create or update semester GPA for a user (Admin)")
    fun createOrUpdateSemesterGpa(
        @PathVariable userId: UUID,
        @Valid @RequestBody request: CreateSemesterGpaDTO
    ): ResponseEntity<ApiResponse<SemesterGpaResponseDTO>> {
        return try {
            val semesterGpa = semesterGpaService.createOrUpdateSemesterGpa(userId, request)
            val response = semesterGpaService.toResponseDTO(semesterGpa)
            
            val isNewRecord = semesterGpa.createdAt?.equals(semesterGpa.updatedAt) == true
            val action = if (isNewRecord) "created" else "updated"

            ResponseEntity.status(if (isNewRecord) HttpStatus.CREATED else HttpStatus.OK).body(
                ApiResponse(
                    status = true,
                    message = "Semester GPA $action successfully. GPA: ${semesterGpa.gpa}, Total Credits: ${semesterGpa.totalCredits}",
                    data = response
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    status = false,
                    message = e.message
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

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all semester GPAs for a user (Admin)")
    fun getAllSemesterGpasForUser(
        @PathVariable userId: UUID
    ): ResponseEntity<ApiResponse<List<SemesterGpaResponseDTO>>> {
        val semesterGpas = semesterGpaService.getAllSemesterGpasForUser(userId)
        val responses = semesterGpas.map { semesterGpaService.toResponseDTO(it) }
        
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Retrieved ${responses.size} semester GPA record(s)",
                data = responses
            )
        )
    }

    @GetMapping("/user/{userId}/semester/{semesterId}")
    @Operation(summary = "Get specific semester GPA for a user (Admin)")
    fun getSemesterGpaByUserAndSemester(
        @PathVariable userId: UUID,
        @PathVariable semesterId: SEMETSER_ID
    ): ResponseEntity<ApiResponse<SemesterGpaResponseDTO>> {
        return try {
            val semesterGpa = semesterGpaService.getSemesterGpaByUserAndSemester(userId, semesterId)
            val response = semesterGpaService.toResponseDTO(semesterGpa)
            
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = response
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

    @GetMapping("/user/{userId}/cgpa")
    @Operation(summary = "Calculate overall CGPA for a user (Admin)")
    fun calculateOverallCgpa(
        @PathVariable userId: UUID
    ): ResponseEntity<ApiResponse<Map<String, Float>>> {
        val (totalCredits, cgpa) = semesterGpaService.calculateOverallCgpa(userId)
        
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Overall CGPA calculated successfully",
                data = mapOf(
                    "totalCredits" to totalCredits,
                    "cgpa" to cgpa
                )
            )
        )
    }

    @PutMapping("/user/{userId}/semester/{semesterId}")
    @Operation(summary = "Update semester GPA for a user (Admin)")
    fun updateSemesterGpa(
        @PathVariable userId: UUID,
        @PathVariable semesterId: SEMETSER_ID,
        @Valid @RequestBody request: UpdateSemesterGpaDTO
    ): ResponseEntity<ApiResponse<SemesterGpaResponseDTO>> {
        return try {
            val semesterGpa = semesterGpaService.updateSemesterGpa(userId, semesterId, request)
            val response = semesterGpaService.toResponseDTO(semesterGpa)
            
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Semester GPA updated successfully. New GPA: ${semesterGpa.gpa}",
                    data = response
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
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    status = false,
                    message = e.message
                )
            )
        }
    }

    @DeleteMapping("/user/{userId}/semester/{semesterId}")
    @Operation(summary = "Delete semester GPA for a user (Admin)")
    fun deleteSemesterGpa(
        @PathVariable userId: UUID,
        @PathVariable semesterId: SEMETSER_ID
    ): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            semesterGpaService.deleteSemesterGpa(userId, semesterId)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Semester GPA deleted successfully"
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete semester GPA by ID (Admin)")
    fun deleteSemesterGpaById(
        @PathVariable id: UUID
    ): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            semesterGpaService.deleteSemesterGpaById(id)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Semester GPA deleted successfully"
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

@RestController
@RequestMapping("/api/student/semester-gpa")
@Tag(name = "Student - My Semester GPA")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('STUDENT')")
class StudentSemesterGpaController(
    private val semesterGpaService: SemesterGpaService,
    private val userRepository: com.example.japuraroute.module.user.repository.UserRepository
) {

    @PostMapping
    @Operation(summary = "Create or update my semester GPA")
    fun createOrUpdateMySemesterGpa(
        @AuthenticationPrincipal userDetails: UserDetails,
        @Valid @RequestBody request: CreateSemesterGpaDTO
    ): ResponseEntity<ApiResponse<SemesterGpaResponseDTO>> {
        return try {
            // Get current user
            val user = userRepository.findByEmail(userDetails.username)
                ?: throw NoSuchElementException("User not found")

            val semesterGpa = semesterGpaService.createOrUpdateSemesterGpa(user.id!!, request)
            val response = semesterGpaService.toResponseDTO(semesterGpa)
            
            val isNewRecord = semesterGpa.createdAt?.equals(semesterGpa.updatedAt) == true
            val action = if (isNewRecord) "created" else "updated"

            ResponseEntity.status(if (isNewRecord) HttpStatus.CREATED else HttpStatus.OK).body(
                ApiResponse(
                    status = true,
                    message = "Semester GPA $action successfully. GPA: ${semesterGpa.gpa}, Total Credits: ${semesterGpa.totalCredits}",
                    data = response
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    status = false,
                    message = e.message
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

    @GetMapping
    @Operation(summary = "Get all my semester GPAs")
    fun getMyAllSemesterGpas(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<List<SemesterGpaResponseDTO>>> {
        val user = userRepository.findByEmail(userDetails.username)
            ?: throw NoSuchElementException("User not found")

        val semesterGpas = semesterGpaService.getAllSemesterGpasForUser(user.id!!)
        val responses = semesterGpas.map { semesterGpaService.toResponseDTO(it) }
        
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Retrieved ${responses.size} semester GPA record(s)",
                data = responses
            )
        )
    }

    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get my specific semester GPA")
    fun getMySemesterGpa(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable semesterId: SEMETSER_ID
    ): ResponseEntity<ApiResponse<SemesterGpaResponseDTO>> {
        return try {
            val user = userRepository.findByEmail(userDetails.username)
                ?: throw NoSuchElementException("User not found")

            val semesterGpa = semesterGpaService.getSemesterGpaByUserAndSemester(user.id!!, semesterId)
            val response = semesterGpaService.toResponseDTO(semesterGpa)
            
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = response
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

    @GetMapping("/cgpa")
    @Operation(summary = "Get my overall CGPA")
    fun getMyOverallCgpa(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Map<String, Float>>> {
        val user = userRepository.findByEmail(userDetails.username)
            ?: throw NoSuchElementException("User not found")

        val (totalCredits, cgpa) = semesterGpaService.calculateOverallCgpa(user.id!!)
        
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Overall CGPA calculated successfully",
                data = mapOf(
                    "totalCredits" to totalCredits,
                    "cgpa" to cgpa
                )
            )
        )
    }

    @PutMapping("/semester/{semesterId}")
    @Operation(summary = "Update my semester GPA")
    fun updateMySemesterGpa(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable semesterId: SEMETSER_ID,
        @Valid @RequestBody request: UpdateSemesterGpaDTO
    ): ResponseEntity<ApiResponse<SemesterGpaResponseDTO>> {
        return try {
            val user = userRepository.findByEmail(userDetails.username)
                ?: throw NoSuchElementException("User not found")

            val semesterGpa = semesterGpaService.updateSemesterGpa(user.id!!, semesterId, request)
            val response = semesterGpaService.toResponseDTO(semesterGpa)
            
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Semester GPA updated successfully. New GPA: ${semesterGpa.gpa}",
                    data = response
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
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse(
                    status = false,
                    message = e.message
                )
            )
        }
    }

    @DeleteMapping("/semester/{semesterId}")
    @Operation(summary = "Delete my semester GPA")
    fun deleteMySemesterGpa(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable semesterId: SEMETSER_ID
    ): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            val user = userRepository.findByEmail(userDetails.username)
                ?: throw NoSuchElementException("User not found")

            semesterGpaService.deleteSemesterGpa(user.id!!, semesterId)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Semester GPA deleted successfully"
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

