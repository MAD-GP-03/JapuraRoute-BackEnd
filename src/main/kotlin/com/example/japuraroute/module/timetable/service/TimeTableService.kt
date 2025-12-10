package com.example.japuraroute.module.timetable.service

import com.example.japuraroute.module.timetable.dto.TimeTableDTO
import com.example.japuraroute.module.timetable.model.TimeTable
import com.example.japuraroute.module.timetable.repository.TimeTableRepository
import com.example.japuraroute.module.user.model.UniYear
import org.springframework.stereotype.Service
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TimetableService(
    private val timeTableRepository: TimeTableRepository,
    private val objectMapper: ObjectMapper
){

    @Transactional
    fun createOrUpdateTimeTable(request: TimeTableDTO): TimeTable {

        @Suppress("UNCHECKED_CAST")
        val timetableMap = objectMapper.convertValue(request.timetable, Map::class.java) as Map<String, Any>

        val existingTimetable = timeTableRepository.findByUniYear(request.uniYear)

        return if (existingTimetable != null) {
            existingTimetable.timetable = timetableMap
            timeTableRepository.save(existingTimetable)
        }else{
            val newTimeTable = TimeTable(
                uniYear = request.uniYear,
                timetable = timetableMap
            )
            timeTableRepository.save(newTimeTable)
        }
    }

    fun getTimeTableByYear(uniYear: UniYear): TimeTable? {
        return timeTableRepository.findByUniYear(uniYear)
    }

    fun getAllTimeTables(): List<TimeTable> {
        return timeTableRepository.findAll()
    }

    @Transactional
    fun deleteTimeTableByYear(uniYear: UniYear) {
        val timeTable = timeTableRepository.findByUniYear(uniYear)
            ?: throw NoSuchElementException("Timetable not found for year: $uniYear")
        timeTableRepository.delete(timeTable)
    }

    @Transactional
    fun deleteTimeTableById(id: UUID) {
        if (!timeTableRepository.existsById(id)) {
            throw NoSuchElementException("Timetable not found with id: $id")
        }
        timeTableRepository.deleteById(id)
    }
}