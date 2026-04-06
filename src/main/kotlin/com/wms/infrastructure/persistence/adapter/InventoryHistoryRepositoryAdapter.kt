package com.wms.infrastructure.persistence.adapter

import com.wms.domain.inventory.model.InventoryHistory
import com.wms.domain.inventory.repository.InventoryHistoryRepository
import com.wms.infrastructure.persistence.repository.InventoryHistoryJpaRepository
import org.springframework.stereotype.Repository

@Repository
class InventoryHistoryRepositoryAdapter(
    private val jpaRepository: InventoryHistoryJpaRepository
) : InventoryHistoryRepository {
    
    override fun save(history: InventoryHistory): InventoryHistory {
        return jpaRepository.save(history)
    }
    
    override fun saveAll(histories: List<InventoryHistory>): List<InventoryHistory> {
        return jpaRepository.saveAll(histories)
    }
    
    override fun findByInventoryId(inventoryId: Long): List<InventoryHistory> {
        return jpaRepository.findByInventoryId(inventoryId)
    }
    
    override fun findByInventoryIdAndTransactionType(
        inventoryId: Long,
        transactionType: String
    ): List<InventoryHistory> {
        return jpaRepository.findByInventoryIdAndTransactionType(inventoryId, transactionType)
    }
    
    override fun findByInventoryIdAndReferenceType(
        inventoryId: Long,
        referenceType: String
    ): List<InventoryHistory> {
        return jpaRepository.findByInventoryIdAndReferenceType(inventoryId, referenceType)
    }
    
    override fun findById(id: Long): InventoryHistory? {
        return jpaRepository.findById(id).orElse(null)
    }
}
