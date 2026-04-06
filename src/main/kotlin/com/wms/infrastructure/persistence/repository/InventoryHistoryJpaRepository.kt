package com.wms.infrastructure.persistence.repository

import com.wms.domain.inventory.model.InventoryHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface InventoryHistoryJpaRepository : JpaRepository<InventoryHistory, Long> {
    
    @Query(nativeQuery = true, value = """
        SELECT * FROM inventory_histories 
        WHERE inventory_id = :inventoryId 
        AND is_deleted = false
        ORDER BY created_at DESC
    """)
    fun findByInventoryId(@Param("inventoryId") inventoryId: Long): List<InventoryHistory>
    
    @Query(nativeQuery = true, value = """
        SELECT * FROM inventory_histories 
        WHERE inventory_id = :inventoryId 
        AND transaction_type = :transactionType
        AND is_deleted = false
        ORDER BY created_at DESC
    """)
    fun findByInventoryIdAndTransactionType(
        @Param("inventoryId") inventoryId: Long,
        @Param("transactionType") transactionType: String
    ): List<InventoryHistory>
    
    @Query(nativeQuery = true, value = """
        SELECT * FROM inventory_histories 
        WHERE inventory_id = :inventoryId 
        AND reference_type = :referenceType
        AND is_deleted = false
        ORDER BY created_at DESC
    """)
    fun findByInventoryIdAndReferenceType(
        @Param("inventoryId") inventoryId: Long,
        @Param("referenceType") referenceType: String
    ): List<InventoryHistory>
}
