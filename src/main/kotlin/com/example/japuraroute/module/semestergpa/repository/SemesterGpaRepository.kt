package com.example.japuraroute.module.semestergpa.repository

import com.example.japuraroute.module.module.model.SEMETSER_ID
import com.example.japuraroute.module.semestergpa.model.SemesterGpaModel
import com.example.japuraroute.module.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
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
}

