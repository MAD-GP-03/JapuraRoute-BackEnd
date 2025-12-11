package com.example.japuraroute.module.module.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

enum class FocusAreaModules {
    ITC,
    ITS,
    ITN,
    ITM
}

enum class SEMETSER_ID {
    FIRST,
    SECOND,
    THIRD,
    FOURTH,
    FIFTH,
    SIXTH,
    SEVENTH,
    EIGHTH
}

@Entity
@Table(name = "module")
@EntityListeners(AuditingEntityListener::class)
class ModuleModel (

    @Column(name = "module_code", nullable = false, unique = true)
    var moduleCode: String,

    @Column(name = "module_name", nullable = false)
    var moduleName: String,

    @Column( name = "credits", nullable = false)
    var credits: Float,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column( name = "focus_area", nullable = false)
    var focusArea: List<FocusAreaModules>? = listOf(FocusAreaModules.ITC),

    @Enumerated(EnumType.STRING)
    @Column(name = "semester_id", nullable = false)
    var semetserId: SEMETSER_ID = SEMETSER_ID.FIRST

    ){

    constructor() : this("", "", 0.0f, listOf(FocusAreaModules.ITC), SEMETSER_ID.FIRST)

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
        if (other !is ModuleModel) return false
        return id != null && id == other.id
    }

    override fun toString(): String {
        return "ModuleModel(id=$id, Module Code='$moduleCode')"
    }


}