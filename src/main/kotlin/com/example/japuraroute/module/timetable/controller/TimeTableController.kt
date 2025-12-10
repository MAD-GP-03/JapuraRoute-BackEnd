package com.example.japuraroute.module.timetable.controller

import com.example.japuraroute.common.dto.ApiResponse
import com.example.japuraroute.module.timetable.model.TimeTable
import com.example.japuraroute.module.timetable.service.TimetableService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.example.japuraroute.module.timetable.dto.TimeTableDTO
import com.example.japuraroute.module.user.model.UniYear
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@RestController
@RequestMapping("/api/admin/timetables")
@Tag(name="Admin - Timetable Management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")

class AdminTimeTableController (
    private val timetableService: TimetableService,
){
   @PostMapping
   @Operation(summary = "Create or update timetable by university year")
   fun createOrUpdateTimeTable(
       @Valid @RequestBody request: TimeTableDTO
   ): ResponseEntity<ApiResponse<TimeTable>> {
       val timeTable = timetableService.createOrUpdateTimeTable(request)
       val isNewRecord = timeTable.createdAt?.equals(timeTable.updatedAt) == true

       return ResponseEntity.status(if (isNewRecord) HttpStatus.CREATED else HttpStatus.OK).body(
           ApiResponse(
               status = true,
               message = if (isNewRecord) "Timetable created successfully" else "Timetable updated successfully",
               data = timeTable
           )
       )
   }

    @GetMapping
    @Operation(summary = "Get all timetables")
    fun getAllTimeTables(): ResponseEntity<ApiResponse<List<TimeTable>>> {
        val timeTables = timetableService.getAllTimeTables()
        return ResponseEntity.ok(
            ApiResponse(status = true, data = timeTables)
        )
    }

    @GetMapping("/year/{uniYear}")
    @Operation(summary = "Get timetable by university year")
    fun getTimeTableByYear(@PathVariable uniYear: UniYear): ResponseEntity<ApiResponse<TimeTable>> {
        val timeTable = timetableService.getTimeTableByYear(uniYear)
        return if (timeTable != null) {
            ResponseEntity.ok(ApiResponse(status = true, data = timeTable))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(status = false, message = "No timetable found for $uniYear")
            )
        }
    }

    @DeleteMapping("/year/{uniYear}")
    @Operation(summary = "Delete timetable by university year")
    fun deleteTimeTableByYear(@PathVariable uniYear: UniYear): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            timetableService.deleteTimeTableByYear(uniYear)
            ResponseEntity.ok(
                ApiResponse(status = true, message = "Timetable deleted successfully")
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(status = false, message = e.message)
            )
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete timetable by ID")
    fun deleteTimeTableById(@PathVariable id: UUID): ResponseEntity<ApiResponse<Nothing>> {
        return try {
            timetableService.deleteTimeTableById(id)
            ResponseEntity.ok(
                ApiResponse(status = true, message = "Timetable deleted successfully")
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(status = false, message = e.message)
            )
        }
    }

}

@RestController
@RequestMapping("/api/timetables")
@Tag(name = "Timetable - Public")
class PublicTimeTableController(
    private val timeTableService: TimetableService
) {

    @GetMapping("/year/{uniYear}")
    @Operation(summary = "Get timetable by university year")
    fun getTimeTableByYear(@PathVariable uniYear: UniYear): ResponseEntity<ApiResponse<TimeTable>> {
        val timeTable = timeTableService.getTimeTableByYear(uniYear)
        return if (timeTable != null) {
            ResponseEntity.ok(ApiResponse(status = true, data = timeTable))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse(status = false, message = "No timetable found for $uniYear")
            )
        }
    }
}
