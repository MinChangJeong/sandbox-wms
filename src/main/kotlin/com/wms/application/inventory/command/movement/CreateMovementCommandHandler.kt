package com.wms.application.inventory.command.movement

import com.wms.application.inventory.dto.CommandResult
import com.wms.domain.common.exception.WmsException
import com.wms.domain.inventory.repository.InventoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateMovementCommandHandler(
    private val inventoryRepository: InventoryRepository
) {
    
    @Transactional
    fun handle(command: CreateMovementCommand): CommandResult {
        val inventory = inventoryRepository.findByIdWithLock(command.inventoryId)
            ?: throw WmsException.InventoryNotFound(command.inventoryId)
        
        inventory.moveOut(
            quantity = command.quantity,
            toLocationId = command.toLocationId,
            updatedBy = command.createdBy
        )
        
        val sourceLocationId = inventory.locationId
        
        var targetInventory = inventoryRepository.findByItemIdAndLocationId(
            itemId = inventory.itemId,
            locationId = command.toLocationId
        )
        
        if (targetInventory == null) {
            targetInventory = com.wms.domain.inventory.model.Inventory.create(
                itemId = inventory.itemId,
                locationId = command.toLocationId,
                quantity = command.quantity,
                createdBy = command.createdBy
            )
        } else {
            targetInventory.moveIn(
                quantity = command.quantity,
                fromLocationId = sourceLocationId,
                updatedBy = command.createdBy
            )
        }
        
        inventoryRepository.save(inventory)
        inventoryRepository.save(targetInventory)
        
        return CommandResult(
            id = inventory.id,
            success = true,
            message = "재고 이동 완료"
        )
    }
}
