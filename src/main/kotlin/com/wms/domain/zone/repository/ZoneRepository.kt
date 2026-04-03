package com.wms.domain.zone.repository

import com.wms.domain.zone.model.Zone
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ZoneRepository {
    fun findById(id: Long): Zone?
    fun findByCode(code: String): Zone?
    fun findByWarehouseId(warehouseId: Long): List<Zone>
    fun findByWarehouseId(warehouseId: Long, pageable: Pageable): Page<Zone>
    fun findAll(pageable: Pageable): Page<Zone>
    fun save(zone: Zone): Zone
    fun delete(zone: Zone)
    fun existsByCodeAndWarehouseId(code: String, warehouseId: Long): Boolean
}
