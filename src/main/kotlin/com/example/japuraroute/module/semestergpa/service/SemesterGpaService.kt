package com.example.japuraroute.module.semestergpa.service

import com.example.japuraroute.module.module.model.SEMETSER_ID
import com.example.japuraroute.module.semestergpa.dto.*
import com.example.japuraroute.module.semestergpa.model.SemesterGpaModel
import com.example.japuraroute.module.semestergpa.repository.SemesterGpaRepository
import com.example.japuraroute.module.user.repository.UserRepository
import com.example.japuraroute.module.user.repository.UserDetailsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class SemesterGpaService(
    private val semesterGpaRepository: SemesterGpaRepository,
    private val userRepository: UserRepository,
    private val userDetailsRepository: UserDetailsRepository
) {

    // Grade to Grade Point mapping
    private val gradePointMap = mapOf(
        "A+" to 4.00f,
        "A" to 4.00f,
        "A-" to 3.70f,
        "B+" to 3.30f,
        "B" to 3.00f,
        "B-" to 2.70f,
        "C+" to 2.30f,
        "C" to 2.00f,
        "C-" to 1.70f,
        "D+" to 1.30f,
        "D" to 1.00f,
        "E" to 0.00f
    )

    /**
     * Convert grade letter to grade points
     */
    private fun getGradePoints(grade: String): Float {
        return gradePointMap[grade.uppercase()] 
            ?: throw IllegalArgumentException("Invalid grade: $grade. Valid grades are: ${gradePointMap.keys.joinToString()}")
    }

    /**
     * Calculate GPA from subjects
     * Formula: Sum(credits * grade_points) / Sum(credits)
     */
    private fun calculateGpa(subjects: List<SubjectDTO>): Pair<Float, Float> {
        var totalCredits = 0.0f
        var weightedSum = 0.0f

        subjects.forEach { subject ->
            val gradePoints = getGradePoints(subject.grade)
            totalCredits += subject.credits
            weightedSum += subject.credits * gradePoints
        }

        val gpa = if (totalCredits > 0) weightedSum / totalCredits else 0.0f
        return Pair(totalCredits, gpa)
    }

    /**
     * Build semester JSON structure
     */
    private fun buildSemesterJson(semesterName: String, subjects: List<SubjectDTO>): Map<String, Any> {
        val subjectsList = subjects.map { subject ->
            mapOf(
                "subject_name" to subject.subjectName,
                "credits" to subject.credits,
                "grade" to subject.grade,
                "grade_points" to getGradePoints(subject.grade)
            )
        }

        return mapOf(
            "semester_name" to semesterName,
            "subjects" to subjectsList
        )
    }

    /**
     * Create or update semester GPA record for a user
     * If a record exists for the user and semester, it updates it; otherwise, creates a new one
     */
    @Transactional
    fun createOrUpdateSemesterGpa(userId: UUID, request: CreateSemesterGpaDTO): SemesterGpaModel {
        // Find user
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found with id: $userId") }

        // Calculate GPA and total credits
        val (totalCredits, gpa) = calculateGpa(request.subjects)

        // Build semester JSON
        val semesterJson = buildSemesterJson(request.semesterName, request.subjects)

        // Check if semester GPA already exists for this user
        val existingSemesterGpa = semesterGpaRepository.findByUserAndSemetserId(user, request.semesterId)

        return if (existingSemesterGpa != null) {
            // Update existing record
            existingSemesterGpa.semetserJSON = semesterJson
            existingSemesterGpa.totalCredits = totalCredits
            existingSemesterGpa.gpa = gpa
            semesterGpaRepository.save(existingSemesterGpa)
        } else {
            // Create new record
            val semesterGpa = SemesterGpaModel(
                semetserId = request.semesterId,
                semetserJSON = semesterJson,
                totalCredits = totalCredits,
                gpa = gpa
            )
            semesterGpa.user = user
            semesterGpaRepository.save(semesterGpa)
        }
    }

    /**
     * Update existing semester GPA
     */
    @Transactional
    fun updateSemesterGpa(userId: UUID, semesterId: SEMETSER_ID, request: UpdateSemesterGpaDTO): SemesterGpaModel {
        // Find user
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found with id: $userId") }

        // Find existing semester GPA
        val semesterGpa = semesterGpaRepository.findByUserAndSemetserId(user, semesterId)
            ?: throw NoSuchElementException("Semester GPA not found for user $userId and semester $semesterId")

        // Update if subjects provided
        request.subjects?.let { subjects ->
            val (totalCredits, gpa) = calculateGpa(subjects)
            
            // Get existing semester name or use provided one
            @Suppress("UNCHECKED_CAST")
            val existingSemesterName = (semesterGpa.semetserJSON["semester_name"] as? String) ?: "Semester"
            val semesterName = request.semesterName ?: existingSemesterName
            
            semesterGpa.semetserJSON = buildSemesterJson(semesterName, subjects)
            semesterGpa.totalCredits = totalCredits
            semesterGpa.gpa = gpa
        }

        // Update semester name only if provided and subjects not provided
        if (request.semesterName != null && request.subjects == null) {
            @Suppress("UNCHECKED_CAST")
            val existingJson = semesterGpa.semetserJSON.toMutableMap()
            existingJson["semester_name"] = request.semesterName
            semesterGpa.semetserJSON = existingJson
        }

        return semesterGpaRepository.save(semesterGpa)
    }

    /**
     * Get all semester GPAs for a user
     */
    fun getAllSemesterGpasForUser(userId: UUID): List<SemesterGpaModel> {
        return semesterGpaRepository.findByUserId(userId)
    }

    /**
     * Get specific semester GPA for a user
     */
    fun getSemesterGpaByUserAndSemester(userId: UUID, semesterId: SEMETSER_ID): SemesterGpaModel {
        return semesterGpaRepository.findByUserIdAndSemetserId(userId, semesterId)
            ?: throw NoSuchElementException("Semester GPA not found for user $userId and semester $semesterId")
    }

    /**
     * Get semester GPA by ID
     */
    fun getSemesterGpaById(id: UUID): SemesterGpaModel {
        return semesterGpaRepository.findById(id)
            .orElseThrow { NoSuchElementException("Semester GPA not found with id: $id") }
    }

    /**
     * Delete semester GPA
     */
    @Transactional
    fun deleteSemesterGpa(userId: UUID, semesterId: SEMETSER_ID) {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found with id: $userId") }

        val semesterGpa = semesterGpaRepository.findByUserAndSemetserId(user, semesterId)
            ?: throw NoSuchElementException("Semester GPA not found for user $userId and semester $semesterId")

        semesterGpaRepository.delete(semesterGpa)
    }

    /**
     * Delete semester GPA by ID
     */
    @Transactional
    fun deleteSemesterGpaById(id: UUID) {
        if (!semesterGpaRepository.existsById(id)) {
            throw NoSuchElementException("Semester GPA not found with id: $id")
        }
        semesterGpaRepository.deleteById(id)
    }

    /**
     * Calculate overall CGPA for a user across all semesters
     */
    fun calculateOverallCgpa(userId: UUID): Pair<Float, Float> {
        val allSemesters = semesterGpaRepository.findByUserId(userId)
        
        if (allSemesters.isEmpty()) {
            return Pair(0.0f, 0.0f)
        }

        val totalCredits = allSemesters.sumOf { it.totalCredits.toDouble() }.toFloat()
        val weightedSum = allSemesters.sumOf { (it.gpa * it.totalCredits).toDouble() }.toFloat()
        
        val cgpa = if (totalCredits > 0) weightedSum / totalCredits else 0.0f
        
        return Pair(totalCredits, cgpa)
    }

    /**
     * Convert entity to response DTO
     */
    fun toResponseDTO(semesterGpa: SemesterGpaModel): SemesterGpaResponseDTO {
        @Suppress("UNCHECKED_CAST")
        val semesterName = semesterGpa.semetserJSON["semester_name"] as? String ?: "Unknown Semester"
        
        @Suppress("UNCHECKED_CAST")
        val subjectsList = (semesterGpa.semetserJSON["subjects"] as? List<Map<String, Any>>) ?: emptyList()
        
        val subjects = subjectsList.map { subjectMap ->
            SubjectResponseDTO(
                subjectName = subjectMap["subject_name"] as? String ?: "",
                credits = (subjectMap["credits"] as? Number)?.toFloat() ?: 0.0f,
                grade = subjectMap["grade"] as? String ?: "",
                gradePoints = (subjectMap["grade_points"] as? Number)?.toFloat() ?: 0.0f
            )
        }

        return SemesterGpaResponseDTO(
            id = semesterGpa.id.toString(),
            userId = semesterGpa.user?.id.toString(),
            semesterId = semesterGpa.semetserId.name,
            semesterName = semesterName,
            subjects = subjects,
            totalCredits = semesterGpa.totalCredits,
            gpa = semesterGpa.gpa,
            createdAt = semesterGpa.createdAt?.toString(),
            updatedAt = semesterGpa.updatedAt?.toString()
        )
    }

    /**
     * Calculate batch average GPA based on user's uni year
     * Gets all students in the same uni_year and calculates their average overall CGPA
     * ULTRA-OPTIMIZED: Uses single database query with aggregation to calculate all statistics
     * Performance: O(1) - constant 2 queries regardless of batch size
     */
    fun calculateBatchAverageGpa(userId: UUID): BatchAverageGpaResponseDTO {
        // Find user's details to get their uni_year
        val userDetails = userDetailsRepository.findByUserId(userId)
            ?: throw NoSuchElementException("User details not found for user id: $userId")

        val uniYear = userDetails.uni_year
            ?: throw IllegalArgumentException("User does not have a uni_year set")

        // Get total students in the batch
        val batchUserDetails = userDetailsRepository.findByUni_year(uniYear)
        val totalStudents = batchUserDetails.size

        if (totalStudents == 0) {
            return BatchAverageGpaResponseDTO(
                uniYear = uniYear.name,
                totalStudents = 0,
                studentsWithGpa = 0,
                averageGpa = 0.0f,
                studentsWithoutGpa = 0
            )
        }

        // ULTRA-OPTIMIZED: Calculate all statistics in ONE database query
        // Returns: [studentCount, weightedGpaSum, totalCreditsSum]
        val statistics = semesterGpaRepository.calculateBatchStatistics(uniYear.name)

        val studentsWithGpa = (statistics[0] as Long).toInt()
        val weightedGpaSum = (statistics[1] as? Number)?.toDouble() ?: 0.0
        val totalCreditsSum = (statistics[2] as? Number)?.toDouble() ?: 0.0

        // Calculate batch average GPA: Sum(student_cgpa) / student_count
        // Where student_cgpa = Sum(semester_gpa * semester_credits) / Sum(semester_credits)
        val averageGpa = if (totalCreditsSum > 0.0) {
            (weightedGpaSum / totalCreditsSum).toFloat()
        } else {
            0.0f
        }

        val studentsWithoutGpa = totalStudents - studentsWithGpa

        return BatchAverageGpaResponseDTO(
            uniYear = uniYear.name,
            totalStudents = totalStudents,
            studentsWithGpa = studentsWithGpa,
            averageGpa = averageGpa,
            studentsWithoutGpa = studentsWithoutGpa
        )
    }
}

