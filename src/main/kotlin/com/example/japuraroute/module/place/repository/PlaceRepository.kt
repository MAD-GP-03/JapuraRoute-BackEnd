package com.example.japuraroute.module.place.repository

import com.example.japuraroute.module.place.model.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlaceRepository : JpaRepository<Place, UUID> {

    fun findByNameContainingIgnoreCase(name: String): List<Place>

    fun findByLocationContainingIgnoreCase(location: String): List<Place>

    @Query("SELECT p FROM Place p JOIN p.tags t WHERE LOWER(t) = LOWER(:tag)")
    fun findByTag(@Param("tag") tag: String): List<Place>

    @Query("SELECT p FROM Place p WHERE p.rating >= :minRating")
    fun findByMinRating(@Param("minRating") minRating: Double): List<Place>

    @Query("""
        SELECT DISTINCT p FROM Place p
        LEFT JOIN p.tags t
        WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')))
        AND (:minRating IS NULL OR p.rating >= :minRating)
        AND (:tags IS NULL OR :tagsSize = 0 OR t IN (:tags))
    """)
    fun searchPlaces(
        @Param("name") name: String?,
        @Param("location") location: String?,
        @Param("minRating") minRating: Double?,
        @Param("tags") tags: List<String>?,
        @Param("tagsSize") tagsSize: Int
    ): List<Place>

    fun existsByName(name: String): Boolean
}

