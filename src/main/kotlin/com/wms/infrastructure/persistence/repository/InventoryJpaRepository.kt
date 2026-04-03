package com.wms.infrastructure.persistence.repository

import com.wms.domain.inventory.model.Inventory
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.query.Procedure
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface InventoryJpaRepository : JpaRepository<Inventory, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.id = :id")
    fun findByIdWithLock(@Param("id") id: Long): Inventory?
    
    fun findByItemIdAndLocationId(itemId: Long, locationId: Long): Inventory?
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(nativeQuery = true, value = """
        SELECT * FROM inventories i
        WHERE i.item_id = :itemId
        AND (i.quantity - i.allocated_qty) > 0
        AND i.is_deleted = false
        ORDER BY i.created_at ASC
    """)
    fun findAllocatableWithLock(@Param("itemId") itemId: Long): List<Inventory>
    
    fun findByItemId(itemId: Long): List<Inventory>
    
    fun findByLocationId(locationId: Long): List<Inventory>
    
    @Query(nativeQuery = true, value = """
        SELECT * FROM inventories i
        WHERE i.is_deleted = false
        AND (:itemId IS NULL OR i.item_id = :itemId)
        AND (:locationId IS NULL OR i.location_id = :locationId)
        AND (:status IS NULL OR i.status = :status)
    """)
    fun searchWithCriteria(
        @Param("itemId") itemId: Long?,
        @Param("locationId") locationId: Long?,
        @Param("status") status: String?,
        pageable: Pageable
    ): Page<Inventory>
}
