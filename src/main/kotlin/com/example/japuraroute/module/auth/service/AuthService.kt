package com.example.japuraroute.module.auth.service

import com.example.japuraroute.module.auth.dto.RegisterRequest
import com.example.japuraroute.module.auth.dto.LoginRequest
import com.example.japuraroute.module.user.model.User
import com.example.japuraroute.module.user.model.UserDetails
import com.example.japuraroute.module.user.repository.UserRepository
import org.springframework.stereotype.Service
import com.example.japuraroute.common.util.PasswordEncoderCustom
import com.example.japuraroute.module.user.model.UserRole


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoderCustom
) {

    fun register(request: RegisterRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            username = request.username,
            role = request.role,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)!!
        )

        val userDetails = UserDetails(
            fullName = request.fullName,
            phoneNumber = request.phoneNumber,
            address = request.address,
            uni_year = request.uni_year,
            reg_number = request.reg_number,
            focus_area = request.focus_area,
            nic = request.nic
        )
        userDetails.user = user
        user.details = userDetails

        return userRepository.save(user)
    }


    fun login(request: LoginRequest): User {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid email or password")


        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        return user
    }
}