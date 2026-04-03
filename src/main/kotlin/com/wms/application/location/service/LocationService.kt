package com.wms.application.location.service

import com.wms.application.location.dto.CreateLocationRequest
import com.wms.application.location.dto.UpdateLocationRequest
import com.wms.application.location.dto.LocationResponse
import com.wms.domain.common.status.LocationStatus
import com.wms.domain.common.status.LocationType
import com.wms.domain.location.model.Location
import com.wms.domain.location.repository.LocationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional
class LocationCommandService(
    private val locationRepository: LocationRepository
) {
    
    fun createLocation(request: CreateLocationRequest, userId: String): Long {
        require(!locationRepository.existsByCode(request.locationCode)) {
            "로케이션 코드 ${request.locationCode}는 이미 존재합니다"
        }
        
        val location = Location.create(
            zoneId = request.zoneId,
            locationCode = request.locationCode,
            rowNum = request.rowNum,
            columnNum = request.columnNum,
            level = request.level,
            locationType = LocationType.fromCode(request.locationType),
            maxWeight = request.maxWeight,
            maxVolume = request.maxVolume,
            createdBy = userId
        )
        
        val saved = locationRepository.save(location)
        return saved.id
    }
    
    fun updateLocation(id: Long, request: UpdateLocationRequest, userId: String) {
        val location = locationRepository.findById(id)
            ?: throw IllegalArgumentException("로케이션을 찾을 수 없습니다: $id")
        
        location.updateCapacity(
            maxWeight = request.maxWeight,
            maxVolume = request.maxVolume,
            updatedBy = userId
        )
        
        locationRepository.save(location)
    }
}

@Service
@Transactional(readOnly = true)
class LocationQueryService(
    private val locationRepository: LocationRepository
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun findLocationById(id: Long): LocationResponse {
        val location = locationRepository.findById(id)
            ?: throw IllegalArgumentException("로케이션을 찾을 수 없습니다: $id")
        
        return location.toResponse()
    }
    
    fun findLocationsByZoneId(zoneId: Long): List<LocationResponse> {
        return locationRepository.findByZoneId(zoneId).map { it.toResponse() }
    }
    
    fun findLocationsByZoneId(zoneId: Long, pageable: Pageable): Page<LocationResponse> {
        return locationRepository.findByZoneId(zoneId, pageable).map { it.toResponse() }
    }
    
    fun findLocationsByCode(code: String): LocationResponse {
        val location = locationRepository.findByCode(code)
            ?: throw IllegalArgumentException("로케이션을 찾을 수 없습니다: $code")
        
        return location.toResponse()
    }
    
    private fun Location.toResponse(): LocationResponse {
        return LocationResponse(
            id = this.id,
            zoneId = this.zoneId,
            locationCode = this.locationCode,
            rowNum = this.rowNum,
            columnNum = this.columnNum,
            level = this.level,
            locationType = this.locationType.code,
            maxWeight = this.maxWeight,
            maxVolume = this.maxVolume,
            status = this.status.code,
            isActive = this.isActive,
            createdAt = this.createdAt.format(formatter),
            updatedAt = this.updatedAt.format(formatter)
        )
    }
}
