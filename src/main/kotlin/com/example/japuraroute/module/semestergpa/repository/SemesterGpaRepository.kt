package com.example.japuraroute.module.semestergpa.repository

import com.example.japuraroute.module.module.model.SEMETSER_ID
import com.example.japuraroute.module.semestergpa.model.SemesterGpaModel
import com.example.japuraroute.module.user.model.User
import com.example.japuraroute.module.user.model.UniYear
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SemesterGpaRepository : JpaRepository<SemesterGpaModel, UUID> {
    
    // Find all semester GPAs for a specific user
    fun findByUser(user: User): List<SemesterGpaModel>
    
    // Find all semester GPAs for a specific user by user ID
    fun findByUserId(userId: UUID): List<SemesterGpaModel>
    
    // Find specific semester GPA for a user
    fun findByUserAndSemetserId(user: User, semesterId: SEMETSER_ID): SemesterGpaModel?
    
    // Find by user ID and semester ID
    fun findByUserIdAndSemetserId(userId: UUID, semesterId: SEMETSER_ID): SemesterGpaModel?
    
    // Check if a semester GPA exists for a user
    fun existsByUserAndSemetserId(user: User, semesterId: SEMETSER_ID): Boolean
    
    // Delete all semester GPAs for a user
    fun deleteByUser(user: User)

    // OPTIMIZED: Find all semester GPAs for multiple users in ONE query
    @Query("SELECT s FROM SemesterGpaModel s WHERE s.user.id IN :userIds")
    fun findByUserIdIn(@Param("userIds") userIds: List<UUID>): List<SemesterGpaModel>

    // ULTRA-OPTIMIZED: Calculate batch average GPA statistics in a single database query
    // Returns: List containing [studentCount, weightedGpaSum, totalCreditsSum]
    @Query("""
        SELECT 
            COUNT(DISTINCT s.user.id),
            COALESCE(SUM(s.gpa * s.totalCredits), 0),
            COALESCE(SUM(s.totalCredits), 0)
        FROM SemesterGpaModel s
        JOIN s.user u
        JOIN UserDetails ud ON ud.user = u
        WHERE ud.uni_year = :uniYear
    """)
    fun calculateBatchStatistics(@Param("uniYear") uniYear: UniYear): List<Any>?
}

