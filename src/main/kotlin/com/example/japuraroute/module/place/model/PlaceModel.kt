package com.example.japuraroute.module.place.model

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Entity
@Table(name = "places")
@EntityListeners(AuditingEntityListener::class)
class Place(

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "short_description")
    var shortDescription: String? = null,

    @Column(name = "description", length = 5000)
    var description: String? = null,


    @ElementCollection
    @CollectionTable(name = "place_tags", joinColumns = [JoinColumn(name = "place_id")])
    @Column(name = "tag")
    var tags: MutableList<String> = mutableListOf(),


    @Column(name = "location")
    var location: String? = null,

    @Column(name = "address")
    var address: String? = null,

    @Column(name = "latitude")
    var latitude: Double? = null,

    @Column(name = "longitude")
    var longitude: Double? = null,

    @Column(name = "contact_phone")
    var contactPhone: String? = null,

    @Column(name = "website_url")
    var websiteUrl: String? = null,


    @ElementCollection
    @CollectionTable(name = "place_images", joinColumns = [JoinColumn(name = "place_id")])
    @Column(name = "image_url", length = 1000)
    var images: MutableList<String> = mutableListOf(),


    @ElementCollection
    @CollectionTable(name = "place_operating_hours", joinColumns = [JoinColumn(name = "place_id")])
    var operatingHours: MutableList<OperatingHour> = mutableListOf(),

    @Column(name = "rating")
    var rating: Double? = null,

    @Column(name = "rating_count")
    var ratingCount: Int = 0,

) {

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
        if (other !is Place) return false
        return id != null && id == other.id
    }

    override fun toString(): String {
        return "ModuleModel(id=$id, Module Code='$name')"
    }

}

@Embeddable
class OperatingHour(
    @Column(name = "day", nullable = false)
    var day: String = "",

    @Column(name = "start_time")
    var startTime: LocalTime? = null,

    @Column(name = "end_time")
    var endTime: LocalTime? = null,

    @Column(name = "note")
    var note: String? = null
)
