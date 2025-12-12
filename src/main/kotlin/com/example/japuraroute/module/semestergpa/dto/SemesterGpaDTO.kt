package com.example.japuraroute.module.semestergpa.dto

import com.example.japuraroute.module.module.model.SEMETSER_ID
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

// Request DTO for creating semester GPA
data class CreateSemesterGpaDTO(
    @field:NotNull(message = "Semester ID is required")
    val semesterId: SEMETSER_ID,

    @field:NotBlank(message = "Semester name is required")
    val semesterName: String,

    @field:NotEmpty(message = "At least one subject is required")
    @field:Valid
    val subjects: List<SubjectDTO>
)

// Request DTO for updating semester GPA
data class UpdateSemesterGpaDTO(
    val semesterName: String? = null,

    @field:Valid
    val subjects: List<SubjectDTO>? = null
)

// Subject details within a semester
data class SubjectDTO(
    @field:NotBlank(message = "Subject name is required")
    val subjectName: String,

    @field:NotNull(message = "Credits is required")
    @field:Positive(message = "Credits must be positive")
    val credits: Float,

    @field:NotBlank(message = "Grade is required")
    val grade: String
)

// Response DTO for semester GPA
data class SemesterGpaResponseDTO(
    val id: String,
    val userId: String,
    val semesterId: String,
    val semesterName: String,
    val subjects: List<SubjectResponseDTO>,
    val totalCredits: Float,
    val gpa: Float,
    val createdAt: String?,
    val updatedAt: String?
)

// Subject response with calculated grade points
data class SubjectResponseDTO(
    val subjectName: String,
    val credits: Float,
    val grade: String,
    val gradePoints: Float
)

// Response DTO for batch average GPA
data class BatchAverageGpaResponseDTO(
    val uniYear: String,
    val totalStudents: Int,
    val studentsWithGpa: Int,
    val averageGpa: Float,
    val studentsWithoutGpa: Int
)

