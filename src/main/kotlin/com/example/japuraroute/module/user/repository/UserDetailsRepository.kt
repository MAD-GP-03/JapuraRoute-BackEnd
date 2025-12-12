package com.example.japuraroute.module.user.repository

import com.example.japuraroute.module.user.model.UniYear
import com.example.japuraroute.module.user.model.UserDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserDetailsRepository : JpaRepository<UserDetails, UUID> {
    fun findByUserId(userId: UUID): UserDetails?
    fun findByUni_year(uniYear: UniYear): List<UserDetails>
}
