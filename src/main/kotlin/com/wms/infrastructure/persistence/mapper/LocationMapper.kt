package com.wms.infrastructure.persistence.mapper

import com.wms.domain.location.model.Location
import org.springframework.stereotype.Component

@Component
class LocationMapper {
    
    fun toDomain(entity: Location): Location = entity
    
    fun toEntity(domain: Location): Location = domain
}
