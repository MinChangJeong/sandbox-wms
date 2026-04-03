package com.wms.infrastructure.persistence.mapper

import com.wms.domain.inventory.model.Inventory
import org.springframework.stereotype.Component

@Component
class InventoryMapper {
    
    fun toDomain(entity: Inventory): Inventory = entity
    
    fun toEntity(domain: Inventory): Inventory = domain
}
