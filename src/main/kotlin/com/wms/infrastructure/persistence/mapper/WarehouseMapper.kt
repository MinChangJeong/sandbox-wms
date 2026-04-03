package com.wms.infrastructure.persistence.mapper

import com.wms.domain.warehouse.model.Warehouse
import org.springframework.stereotype.Component

@Component
class WarehouseMapper {
    
    fun toDomain(entity: Warehouse): Warehouse = entity
    
    fun toEntity(domain: Warehouse): Warehouse = domain
}
