package com.wms.domain.warehouse.repository

import com.wms.domain.warehouse.model.Warehouse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface WarehouseRepository {
    fun findById(id: Long): Warehouse?
    fun findByCode(code: String): Warehouse?
    fun findAll(pageable: Pageable): Page<Warehouse>
    fun findAll(): List<Warehouse>
    fun save(warehouse: Warehouse): Warehouse
    fun delete(warehouse: Warehouse)
    fun existsByCode(code: String): Boolean
}
