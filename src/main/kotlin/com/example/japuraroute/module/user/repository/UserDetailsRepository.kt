package com.example.japuraroute.module.user.repository

import com.example.japuraroute.module.user.model.UniYear
import com.example.japuraroute.module.user.model.UserDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserDetailsRepository : JpaRepository<UserDetails, UUID> {
    fun findByUserId(userId: UUID): UserDetails?

    @Query("SELECT ud FROM UserDetails ud WHERE ud.uni_year = :uniYear")
    fun findByUniYear(@Param("uniYear") uniYear: UniYear): List<UserDetails>
}
