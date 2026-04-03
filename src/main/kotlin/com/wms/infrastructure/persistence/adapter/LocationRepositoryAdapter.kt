package com.wms.infrastructure.persistence.adapter

import com.wms.domain.location.model.Location
import com.wms.domain.location.repository.LocationRepository
import com.wms.infrastructure.persistence.mapper.LocationMapper
import com.wms.infrastructure.persistence.repository.LocationJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class LocationRepositoryAdapter(
    private val jpaRepository: LocationJpaRepository,
    private val mapper: LocationMapper
) : LocationRepository {
    
    override fun save(location: Location): Location {
        val saved = jpaRepository.save(mapper.toEntity(location))
        return mapper.toDomain(saved)
    }
    
    override fun findById(id: Long): Location? {
        return jpaRepository.findById(id).orElse(null)?.let { mapper.toDomain(it) }
    }
    
    override fun findByCode(code: String): Location? {
        return jpaRepository.findByLocationCode(code)?.let { mapper.toDomain(it) }
    }
    
    override fun findByZoneId(zoneId: Long): List<Location> {
        return jpaRepository.findAllByZoneIdAndIsDeletedFalse(zoneId).map { mapper.toDomain(it) }
    }
    
    override fun findByZoneId(zoneId: Long, pageable: Pageable): Page<Location> {
        val allLocations = jpaRepository.findAllByZoneIdAndIsDeletedFalse(zoneId)
        val startIndex = pageable.pageNumber * pageable.pageSize
        val endIndex = minOf(startIndex + pageable.pageSize, allLocations.size)
        val content = if (startIndex < allLocations.size) {
            allLocations.subList(startIndex, endIndex).map { mapper.toDomain(it) }
        } else {
            emptyList()
        }
        return PageImpl(content, pageable, allLocations.size.toLong())
    }
    
    override fun findAll(pageable: Pageable): Page<Location> {
        return jpaRepository.findAll(pageable).map { mapper.toDomain(it) }
    }
    
    override fun delete(location: Location) {
        jpaRepository.deleteById(location.id)
    }
    
    override fun existsByCode(code: String): Boolean {
        return jpaRepository.findByLocationCode(code) != null
    }
}
