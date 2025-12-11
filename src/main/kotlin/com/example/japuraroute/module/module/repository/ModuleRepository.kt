package com.example.japuraroute.module.module.repository

import com.example.japuraroute.module.module.model.ModuleModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ModuleRepository : JpaRepository<ModuleModel, UUID> {
    fun findByModuleCode(moduleCode: String): ModuleModel?
    fun existsByModuleCode(moduleCode: String): Boolean
}