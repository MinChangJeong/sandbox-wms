package com.wms.infrastructure.persistence.repository

import com.wms.domain.zone.model.Zone
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ZoneJpaRepository : JpaRepository<Zone, Long> {
    fun findByZoneCodeAndWarehouseId(zoneCode: String, warehouseId: Long): Zone?
    fun findAllByWarehouseIdAndIsDeletedFalse(warehouseId: Long): List<Zone>
    fun findAllByWarehouseId(warehouseId: Long): List<Zone>
}
