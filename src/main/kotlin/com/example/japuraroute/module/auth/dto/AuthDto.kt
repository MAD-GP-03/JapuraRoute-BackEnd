package com.example.japuraroute.module.auth.dto

import jakarta.validation.constraints.*
import com.example.japuraroute.module.user.model.UserRole
import com.example.japuraroute.module.user.model.Department
import com.example.japuraroute.module.user.model.FocusArea
import com.example.japuraroute.module.user.model.UniYear

data class RegisterRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    val username: String,

    @field:NotBlank(message = "Full name is required")
    @field:Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    val fullName: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @field:Size(max = 100, message = "Email must not exceed 100 characters")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]+$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    val password: String,

    @field:NotBlank(message = "Phone number is required")
    @field:Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
    val phoneNumber: String,

    @field:NotBlank(message = "Address is required")
    @field:Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    val address: String,

    @field:NotBlank(message = "Role is required")
    val role: UserRole,

    val uni_year: UniYear?,

    @field:Size(min = 3, max = 20, message = "Registration Number must be between 3 and 20 characters")
    val reg_number: String?,

    val department: Department?,

    val focus_area: FocusArea?,

    @field:NotBlank(message = "NIC is required")
    @field:Size(min = 5, max = 20, message = "NIC Number must be between 3 and 20 characters")
    val nic: String

){
    @AssertTrue(message = "Uni year is required for STUDENT role")
    fun isUniYearValid(): Boolean {
        return !(role == UserRole.STUDENT && uni_year == null)
    }

    @AssertTrue(message = "Registration number is required for STUDENT role")
    fun validateRegNumber(): Boolean {
        return !(role == UserRole.STUDENT && reg_number.isNullOrBlank())
    }

    @AssertTrue(message = "Department is required for STUDENT role")
    fun validateDepartment(): Boolean {
        return !(role == UserRole.STUDENT && department == null)
    }

    @AssertTrue(message = "Focus area is required for STUDENT role")
    fun validateFocusArea(): Boolean {
        return !(role == UserRole.STUDENT && focus_area == null)
    }
}

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String
)