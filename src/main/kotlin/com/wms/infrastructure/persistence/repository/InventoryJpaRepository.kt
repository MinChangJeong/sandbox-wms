package com.wms.infrastructure.persistence.repository

import com.wms.domain.inventory.model.Inventory
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface InventoryJpaRepository : JpaRepository<Inventory, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    fun findByIdWithLock(@Param("id") id: Long): Inventory?
    
    fun findByItemIdAndLocationId(itemId: Long, locationId: Long): Inventory?
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i FROM Inventory i 
        WHERE i.itemId = :itemId 
        AND (i.quantity - i.allocatedQty) > 0
        AND i.isDeleted = false
        ORDER BY i.createdAt ASC
    """)
    fun findAllocatableWithLock(@Param("itemId") itemId: Long): List<Inventory>
    
    fun findByItemId(itemId: Long): List<Inventory>
    
    fun findByLocationId(locationId: Long): List<Inventory>
    
    @Query("""
        SELECT i FROM Inventory i 
        LEFT JOIN Location l ON i.locationId = l.id
        LEFT JOIN Zone z ON l.zoneId = z.id
        WHERE i.isDeleted = false
        AND (:itemId IS NULL OR i.itemId = :itemId)
        AND (:locationId IS NULL OR i.locationId = :locationId)
        AND (:warehouseId IS NULL OR z.warehouseId = :warehouseId)
        AND (:status IS NULL OR i.status = :status)
    """)
    fun searchWithCriteria(
        @Param("itemId") itemId: Long?,
        @Param("locationId") locationId: Long?,
        @Param("warehouseId") warehouseId: Long?,
        @Param("status") status: String?,
        pageable: Pageable
    ): Page<Inventory>
}
