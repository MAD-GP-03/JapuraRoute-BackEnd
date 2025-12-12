package com.example.japuraroute.module.semestergpa.model

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import com.example.japuraroute.module.module.model.SEMETSER_ID
import com.example.japuraroute.module.user.model.User
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(
    name = "student_semester_gpa",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_user_semester",
            columnNames = ["user_id", "semester_id"]
        )
    ]
)
@EntityListeners(AuditingEntityListener::class)
class SemesterGpaModel(

    @Enumerated(EnumType.STRING)
    @Column(name = "semester_id", nullable = false)
    var semetserId: SEMETSER_ID,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "semester_json", nullable = false)
    var semetserJSON: Map<String, Any> = emptyMap(),

    @Column( name = "total_credits", nullable = false)
    var totalCredits: Float,

    @Column( name = "gpa", nullable = false)
    var gpa: Float,

    ) {
    constructor() : this( SEMETSER_ID.FIRST, emptyMap(), 0.0f, 0.0f)

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

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
        if (other !is SemesterGpaModel) return false
        return id != null && id == other.id
    }

    override fun toString(): String {
        return "SEM GPA(id=$id, SemID='$semetserId')"
    }
}