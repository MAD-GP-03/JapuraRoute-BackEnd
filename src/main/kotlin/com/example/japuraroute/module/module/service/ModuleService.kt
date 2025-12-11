package com.example.japuraroute.module.module.service

import com.example.japuraroute.module.module.dto.CreateModuleDTO
import com.example.japuraroute.module.module.dto.ModuleResponseDTO
import com.example.japuraroute.module.module.dto.UpdateModuleDTO
import com.example.japuraroute.module.module.model.ModuleModel
import com.example.japuraroute.module.module.repository.ModuleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ModuleService(
    private val moduleRepository: ModuleRepository
) {

    @Transactional
    fun createModule(request: CreateModuleDTO): ModuleModel {
        // Check if module code already exists
        if (moduleRepository.existsByModuleCode(request.moduleCode)) {
            throw IllegalArgumentException("Module with code '${request.moduleCode}' already exists")
        }

        val module = ModuleModel(
            moduleCode = request.moduleCode,
            moduleName = request.moduleName,
            credits = request.credits,
            focusArea = request.focusArea,
            semetserId = request.semesterId
        )

        return moduleRepository.save(module)
    }

    fun getAllModules(): List<ModuleModel> {
        return moduleRepository.findAll()
    }

    fun getModuleById(id: UUID): ModuleModel {
        return moduleRepository.findById(id)
            .orElseThrow { NoSuchElementException("Module not found with id: $id") }
    }

    fun getModuleByCode(moduleCode: String): ModuleModel {
        return moduleRepository.findByModuleCode(moduleCode)
            ?: throw NoSuchElementException("Module not found with code: $moduleCode")
    }

    @Transactional
    fun updateModule(id: UUID, request: UpdateModuleDTO): ModuleModel {
        val module = getModuleById(id)

        request.moduleName?.let { module.moduleName = it }
        request.credits?.let { module.credits = it }
        request.focusArea?.let { module.focusArea = it }
        request.semesterId?.let { module.semetserId = it }

        return moduleRepository.save(module)
    }

    @Transactional
    fun deleteModule(id: UUID) {
        if (!moduleRepository.existsById(id)) {
            throw NoSuchElementException("Module not found with id: $id")
        }
        moduleRepository.deleteById(id)
    }

    @Transactional
    fun deleteModuleByCode(moduleCode: String) {
        val module = moduleRepository.findByModuleCode(moduleCode)
            ?: throw NoSuchElementException("Module not found with code: $moduleCode")
        moduleRepository.delete(module)
    }

    // Helper function to map entity to DTO
    fun toResponseDTO(module: ModuleModel): ModuleResponseDTO {
        return ModuleResponseDTO(
            id = module.id.toString(),
            moduleCode = module.moduleCode,
            moduleName = module.moduleName,
            credits = module.credits,
            focusArea = module.focusArea ?: emptyList(),
            semesterId = module.semetserId.name,
            createdAt = module.createdAt?.toString(),
            updatedAt = module.updatedAt?.toString()
        )
    }
}

