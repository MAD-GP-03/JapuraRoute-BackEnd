package com.example.japuraroute.module.user.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


enum class Department {
    IT,
    ET,
    BST
}

enum class FocusArea {
    COMMON,
    SOFTWARE,
    NETWORKING,
    MULTIMEDIA
}

enum class UniYear{
    FIRST_YEAR,
    SECOND_YEAR,
    THIRD_YEAR,
    FOURTH_YEAR
}

@Entity
@Table(name = "user_details")
@EntityListeners(AuditingEntityListener::class)
class UserDetails(
    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Column(name = "phone_number")
    var phoneNumber: String? = null,

    @Column(name = "address")
    var address: String? = null,

    @Column(name = "date_of_birth")
    var dateOfBirth: LocalDate? = null,

    @Column(name = "uni_year")
    var uni_year: UniYear? = null,

    @Column(name="reg_number")
    var reg_number: String? = null,

    @Column(name="department")
    var department: Department? = null,

    @Column(name = "focus_area")
    var focus_area: FocusArea? = FocusArea.COMMON,

    @Column(name="nic")
    var nic: String? = null,


    ) {
    constructor() : this("")

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID? = null


    @OneToOne(fetch = FetchType.LAZY)
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


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserDetails) return false
        return id != null && id == other.id
    }

    override fun toString(): String {
        return "UserDetails(id=$id, fullName='$fullName')"
    }
}