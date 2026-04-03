package com.wms.infrastructure.persistence.mapper

import com.wms.domain.zone.model.Zone
import org.springframework.stereotype.Component

@Component
class ZoneMapper {
    
    fun toDomain(entity: Zone): Zone = entity
    
    fun toEntity(domain: Zone): Zone = domain
}
