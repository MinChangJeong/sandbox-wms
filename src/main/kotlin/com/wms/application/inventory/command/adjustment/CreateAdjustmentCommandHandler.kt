package com.wms.application.inventory.command.adjustment

import com.wms.application.inventory.dto.CommandResult
import com.wms.domain.common.exception.WmsException
import com.wms.domain.inventory.repository.InventoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateAdjustmentCommandHandler(
    private val inventoryRepository: InventoryRepository
) {
    
    @Transactional
    fun handle(command: CreateAdjustmentCommand): CommandResult {
        val inventory = inventoryRepository.findByIdWithLock(command.inventoryId)
            ?: throw WmsException.InventoryNotFound(command.inventoryId)
        
        inventory.adjust(
            adjustmentType = command.adjustmentType,
            quantity = command.quantity,
            reason = command.reason,
            updatedBy = command.createdBy
        )
        
        inventoryRepository.save(inventory)
        
        return CommandResult(
            id = inventory.id,
            success = true,
            message = "재고 조정 완료"
        )
    }
}
