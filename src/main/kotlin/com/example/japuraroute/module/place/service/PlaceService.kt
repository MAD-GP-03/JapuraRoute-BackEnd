package com.example.japuraroute.module.place.service

import com.example.japuraroute.module.place.dto.*
import com.example.japuraroute.module.place.model.OperatingHour
import com.example.japuraroute.module.place.model.Place
import com.example.japuraroute.module.place.repository.PlaceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PlaceService(private val placeRepository: PlaceRepository) {

    @Transactional
    fun createPlace(dto: CreatePlaceDto): Place {
        if (placeRepository.existsByName(dto.name)) {
            throw IllegalArgumentException("Place with name '${dto.name}' already exists")
        }

        val place = Place(
            name = dto.name,
            shortDescription = dto.shortDescription,
            description = dto.description,
            tags = dto.tags.toMutableList(),
            location = dto.location,
            address = dto.address,
            latitude = dto.latitude,
            longitude = dto.longitude,
            contactPhone = dto.contactPhone,
            websiteUrl = dto.websiteUrl,
            images = dto.images.toMutableList(),
            operatingHours = dto.operatingHours.map { it.toEntity() }.toMutableList()
        )
        return placeRepository.save(place)
    }

    fun getPlaceById(id: UUID): Place {
        return placeRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("Place not found with id: $id")
    }

    fun getAllPlaces(pageable: Pageable): Page<Place> {
        return placeRepository.findAll(pageable)
    }

    @Transactional
    fun updatePlace(id: UUID, dto: UpdatePlaceDto): Place {
        val place = getPlaceById(id)

        dto.name?.let {
            if (it != place.name && placeRepository.existsByName(it)) {
                throw IllegalArgumentException("Place with name '$it' already exists")
            }
            place.name = it
        }
        dto.shortDescription?.let { place.shortDescription = it }
        dto.description?.let { place.description = it }
        dto.tags?.let { place.tags = it.toMutableList() }
        dto.location?.let { place.location = it }
        dto.address?.let { place.address = it }
        dto.latitude?.let { place.latitude = it }
        dto.longitude?.let { place.longitude = it }
        dto.contactPhone?.let { place.contactPhone = it }
        dto.websiteUrl?.let { place.websiteUrl = it }
        dto.images?.let { place.images = it.toMutableList() }
        dto.operatingHours?.let {
            place.operatingHours = it.map { oh -> oh.toEntity() }.toMutableList()
        }

        return placeRepository.save(place)
    }

    @Transactional
    fun deletePlace(id: UUID) {
        if (!placeRepository.existsById(id)) {
            throw NoSuchElementException("Place not found with id: $id")
        }
        placeRepository.deleteById(id)
    }

    fun searchPlaces(criteria: PlaceSearchCriteria, pageable: Pageable): Page<Place> {
        return placeRepository.searchPlaces(
            name = criteria.name,
            location = criteria.location,
            minRating = criteria.minRating,
            tags = criteria.tags?.takeIf { it.isNotEmpty() },
            pageable = pageable
        )
    }

    fun searchByName(name: String, pageable: Pageable): Page<Place> {
        return placeRepository.findByNameContainingIgnoreCase(name, pageable)
    }

    fun searchByLocation(location: String, pageable: Pageable): Page<Place> {
        return placeRepository.findByLocationContainingIgnoreCase(location, pageable)
    }

    fun searchByTag(tag: String, pageable: Pageable): Page<Place> {
        return placeRepository.findByTag(tag, pageable)
    }

    fun findByMinRating(minRating: Double, pageable: Pageable): Page<Place> {
        return placeRepository.findByMinRating(minRating, pageable)
    }

    @Transactional
    fun addImage(id: UUID, imageUrl: String): Place {
        val place = getPlaceById(id)

        if (place.images.size >= 50) {
            throw IllegalStateException("Maximum number of images (50) reached")
        }

        if (place.images.contains(imageUrl)) {
            throw IllegalArgumentException("Image URL already exists")
        }

        place.images.add(imageUrl)
        return placeRepository.save(place)
    }

    @Transactional
    fun removeImage(id: UUID, imageUrl: String): Place {
        val place = getPlaceById(id)

        if (!place.images.remove(imageUrl)) {
            throw IllegalArgumentException("Image URL not found")
        }

        return placeRepository.save(place)
    }

    @Transactional
    fun addRating(id: UUID, newRating: Double): Place {
        val place = getPlaceById(id)

        val totalRating = (place.rating ?: 0.0) * place.ratingCount + newRating
        place.ratingCount += 1
        place.rating = totalRating / place.ratingCount

        return placeRepository.save(place)
    }

    // Helper function to map entity to DTO
    fun toResponseDto(place: Place): PlaceResponseDto {
        return PlaceResponseDto(
            id = place.id?.toString(),
            name = place.name,
            shortDescription = place.shortDescription,
            description = place.description,
            tags = place.tags.toList(),
            location = place.location,
            address = place.address,
            latitude = place.latitude,
            longitude = place.longitude,
            contactPhone = place.contactPhone,
            websiteUrl = place.websiteUrl,
            images = place.images.toList(),
            operatingHours = place.operatingHours.map { it.toDto() },
            rating = place.rating,
            ratingCount = place.ratingCount,
            createdAt = place.createdAt?.toString(),
            createdBy = place.createdBy,
            updatedAt = place.updatedAt?.toString(),
            updatedBy = place.updatedBy
        )
    }

    // Extension functions for mapping
    private fun OperatingHour.toDto() = OperatingHourDto(
        day = day,
        startTime = startTime,
        endTime = endTime,
        note = note
    )

    private fun OperatingHourDto.toEntity() = OperatingHour(
        day = day,
        startTime = startTime,
        endTime = endTime,
        note = note
    )
}

