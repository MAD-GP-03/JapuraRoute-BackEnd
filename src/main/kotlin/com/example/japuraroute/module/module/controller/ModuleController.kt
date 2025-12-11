package com.example.japuraroute.module.module.controller

import com.example.japuraroute.common.dto.ApiResponse
import com.example.japuraroute.module.module.dto.CreateModuleDTO
import com.example.japuraroute.module.module.dto.UpdateModuleDTO
import com.example.japuraroute.module.module.model.ModuleModel
import com.example.japuraroute.module.module.service.ModuleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/admin/modules")
@Tag(name = "Admin - Module Management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN')")
class AdminModuleController(
    private val moduleService: ModuleService
) {

    @PostMapping
    @Operation(summary = "Create a new module")
    fun createModule(
        @Valid @RequestBody request: CreateModuleDTO
    ): ResponseEntity<ApiResponse<ModuleModel>> {
        return try {
            val module = moduleService.createModule(request)
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse(
                    status = true,
                    message = "Module created successfully",
                    data = module
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
    @Operation(summary = "Get all modules")
    fun getAllModules(): ResponseEntity<ApiResponse<List<ModuleModel>>> {
        val modules = moduleService.getAllModules()
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                message = "Modules retrieved successfully",
                data = modules
            )
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get module by ID")
    fun getModuleById(@PathVariable id: UUID): ResponseEntity<ApiResponse<ModuleModel>> {
        return try {
            val module = moduleService.getModuleById(id)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = module
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

    @GetMapping("/code/{moduleCode}")
    @Operation(summary = "Get module by module code")
    fun getModuleByCode(@PathVariable moduleCode: String): ResponseEntity<ApiResponse<ModuleModel>> {
        return try {
            val module = moduleService.getModuleByCode(moduleCode)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = module
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
    @Operation(summary = "Update module by ID")
    fun updateModule(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateModuleDTO
    ): ResponseEntity<ApiResponse<ModuleModel>> {
        return try {
            val module = moduleService.updateModule(id, request)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Module updated successfully",
                    data = module
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
    @Operation(summary = "Delete module by ID")
    fun deleteModule(@PathVariable id: UUID): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            moduleService.deleteModule(id)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Module deleted successfully"
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

    @DeleteMapping("/code/{moduleCode}")
    @Operation(summary = "Delete module by module code")
    fun deleteModuleByCode(@PathVariable moduleCode: String): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            moduleService.deleteModuleByCode(moduleCode)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    message = "Module deleted successfully"
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
@RequestMapping("/api/modules")
@Tag(name = "Public - Module Information")
class PublicModuleController(
    private val moduleService: ModuleService
) {

    @GetMapping
    @Operation(summary = "Get all modules (Public)")
    fun getAllModules(): ResponseEntity<ApiResponse<List<ModuleModel>>> {
        val modules = moduleService.getAllModules()
        return ResponseEntity.ok(
            ApiResponse(
                status = true,
                data = modules
            )
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get module by ID (Public)")
    fun getModuleById(@PathVariable id: UUID): ResponseEntity<ApiResponse<ModuleModel>> {
        return try {
            val module = moduleService.getModuleById(id)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = module
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

    @GetMapping("/code/{moduleCode}")
    @Operation(summary = "Get module by module code (Public)")
    fun getModuleByCode(@PathVariable moduleCode: String): ResponseEntity<ApiResponse<ModuleModel>> {
        return try {
            val module = moduleService.getModuleByCode(moduleCode)
            ResponseEntity.ok(
                ApiResponse(
                    status = true,
                    data = module
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

