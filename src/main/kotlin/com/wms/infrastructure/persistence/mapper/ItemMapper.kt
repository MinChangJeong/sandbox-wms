package com.wms.infrastructure.persistence.mapper

import com.wms.domain.item.model.Item
import org.springframework.stereotype.Component

@Component
class ItemMapper {
    
    fun toDomain(entity: Item): Item = entity
    
    fun toEntity(domain: Item): Item = domain
}
