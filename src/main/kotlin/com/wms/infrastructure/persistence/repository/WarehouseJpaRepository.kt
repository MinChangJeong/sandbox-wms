package com.wms.infrastructure.persistence.repository

import com.wms.domain.warehouse.model.Warehouse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseJpaRepository : JpaRepository<Warehouse, Long> {
    fun findByWarehouseCode(warehouseCode: String): Warehouse?
    fun findAllByIsDeletedFalse(): List<Warehouse>
}
