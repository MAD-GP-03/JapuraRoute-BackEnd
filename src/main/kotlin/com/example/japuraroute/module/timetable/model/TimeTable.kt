package com.example.japuraroute.module.timetable.model

import com.example.japuraroute.module.user.model.UniYear
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "time_table")
@EntityListeners(AuditingEntityListener::class)
class TimeTable(

    @Enumerated(EnumType.STRING)
    @Column(name = "uni_year", nullable = false, unique = true)
    @NotNull(message = "University year is required")
    var uniYear: UniYear,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "timetable", nullable = false)
    var timetable: Map<String, Any> = emptyMap(),

    ) {
    constructor() : this(UniYear.FIRST_YEAR, emptyMap())

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    var createdBy: String? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null

    @LastModifiedBy
    @Column(name = "updated_by")
    var updatedBy: String? = null

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TimeTable) return false
        return id != null && id == other.id
    }

    override fun toString(): String {
        return "TimeTable(id=$id, uniYear='$uniYear')"
    }
}