package com.example.japuraroute.module.event.model

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

//@Entity
//@Table(name = "event")
//@EntityListeners(AuditingEntityListener::class)
//class EventModel (
//
//    @Column(name = "event_name", nullable = false)
//    var eventName: String,
//
//    @Column(name = "event_date", nullable = false)
//    var eventDate: LocalDate,
//
//    @Column(name = "start_time", nullable = false)
//    var startTime: LocalTime,
//
//    @Column(name = "end_time", nullable = true)
//    var endTime: LocalTime? = null,
//
//    @Column(name = "location", nullable = false)
//    var location: String,
//
//    @Column(name = "going_count", nullable = false)
//    var goingCount: Int,
//
//
//
//    ) {
//
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id", updatable = false, nullable = false)
//    var id: java.util.UUID? = null
//}
//
//
