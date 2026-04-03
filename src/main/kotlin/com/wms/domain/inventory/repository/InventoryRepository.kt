package com.wms.domain.inventory.repository

import com.wms.domain.inventory.model.Inventory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InventoryRepository {
    fun findById(id: Long): Inventory?
    
    fun findByItemIdAndLocationId(itemId: Long, locationId: Long): Inventory?
    
    fun findByIdWithLock(id: Long): Inventory?
    
    fun findAllocatableWithLock(itemId: Long, requiredQty: Int): List<Inventory>
    
    fun findByItemId(itemId: Long): List<Inventory>
    
    fun findByLocationId(locationId: Long): List<Inventory>
    
    fun findAll(pageable: Pageable): Page<Inventory>
    
    fun searchWithCriteria(
        itemId: Long? = null,
        locationId: Long? = null,
        warehouseId: Long? = null,
        status: String? = null,
        pageable: Pageable
    ): Page<Inventory>
    
    fun save(inventory: Inventory): Inventory
    
    fun saveAll(inventories: List<Inventory>): List<Inventory>
    
    fun delete(inventory: Inventory)
}
