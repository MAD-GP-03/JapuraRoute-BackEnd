package com.example.japuraroute.module.timetable.dto

import com.example.japuraroute.module.user.model.UniYear
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class TimeTableDTO(

    @field:NotNull(message = "University year is required")
    val uniYear: UniYear,

    @field:NotNull(message = "Timetable data is required")
    @field:Valid
    val timetable: TimetableData
)

data class TimetableData(

    @field:NotEmpty(message = "Timetable entries cannot be empty")
    @field:Valid
    val timetable: List<TimetableEntry>,

)

data class TimetableEntry(
    @field:NotNull(message = "Day is required")
    val day: String,

    @field:NotNull(message = "Start time is required")
    val start_time: String,

    @field:NotNull(message = "End time is required")
    val end_time: String,

    @field:NotNull(message = "Module code is required")
    val module_code: String,

    @field:NotNull(message = "Module name is required")
    val module_name: String,

    @field:NotNull(message = "Type is required")
    val type: String,

    @field:NotNull(message = "Lecturer is required")
    val lecturer: String,

    @field:NotNull(message = "Location is required")
    val location: String,

    @field:NotEmpty(message = "Focus area is required")
    val focus_area: List<String>
)


