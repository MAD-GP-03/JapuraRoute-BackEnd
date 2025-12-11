package com.example.japuraroute.module.module.dto

import com.example.japuraroute.module.module.model.FocusAreaModules
import com.example.japuraroute.module.module.model.SEMETSER_ID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateModuleDTO(
    @field:NotBlank(message = "Module code is required")
    val moduleCode: String,

    @field:NotBlank(message = "Module name is required")
    val moduleName: String,

    @field:NotNull(message = "Credits is required")
    @field:Positive(message = "Credits must be positive")
    val credits: Float,

    @field:NotEmpty(message = "At least one focus area is required")
    val focusArea: List<FocusAreaModules>,

    @field:NotNull(message = "Semester ID is required")
    val semesterId: SEMETSER_ID
)

data class UpdateModuleDTO(
    val moduleName: String? = null,
    
    @field:Positive(message = "Credits must be positive")
    val credits: Float? = null,
    
    val focusArea: List<FocusAreaModules>? = null,

    val semesterId: SEMETSER_ID? = null
)

data class ModuleResponseDTO(
    val id: String,
    val moduleCode: String,
    val moduleName: String,
    val credits: Float,
    val focusArea: List<FocusAreaModules>,
    val semesterId: String,
    val createdAt: String?,
    val updatedAt: String?
)

