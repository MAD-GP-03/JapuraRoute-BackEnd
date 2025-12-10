package com.example.japuraroute.module.timetable.repository

import com.example.japuraroute.module.timetable.model.TimeTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.example.japuraroute.module.user.model.UniYear
import java.util.UUID

@Repository
interface TimeTableRepository : JpaRepository<TimeTable, UUID> {
    fun findByUniYear(uniYear: UniYear): TimeTable?
    fun existsByUniYear(uniYear: UniYear): Boolean
}