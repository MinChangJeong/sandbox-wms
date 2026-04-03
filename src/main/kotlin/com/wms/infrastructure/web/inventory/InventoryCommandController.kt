package com.wms.infrastructure.web.inventory

import com.wms.application.inventory.command.adjustment.CreateAdjustmentCommand
import com.wms.application.inventory.command.adjustment.CreateAdjustmentCommandHandler
import com.wms.application.inventory.command.movement.CreateMovementCommand
import com.wms.application.inventory.command.movement.CreateMovementCommandHandler
import com.wms.application.inventory.dto.CommandResult
import com.wms.infrastructure.web.inventory.dto.AdjustInventoryRequest
import com.wms.infrastructure.web.inventory.dto.CreateMovementRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory/commands")
class InventoryCommandController(
    private val adjustmentHandler: CreateAdjustmentCommandHandler,
    private val movementHandler: CreateMovementCommandHandler
) {
    
    @PostMapping("/adjustments")
    fun adjustInventory(
        @Valid @RequestBody request: AdjustInventoryRequest
    ): ResponseEntity<CommandResult> {
        val command = CreateAdjustmentCommand(
            inventoryId = request.inventoryId!!,
            adjustmentType = request.adjustmentType!!,
            quantity = request.quantity!!,
            reason = request.reason!!,
            createdBy = "system"
        )
        
        val result = adjustmentHandler.handle(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }
    
    @PostMapping("/movements")
    fun moveInventory(
        @Valid @RequestBody request: CreateMovementRequest
    ): ResponseEntity<CommandResult> {
        val command = CreateMovementCommand(
            inventoryId = request.inventoryId!!,
            fromLocationId = request.fromLocationId!!,
            toLocationId = request.toLocationId!!,
            quantity = request.quantity!!,
            reason = request.reason!!,
            createdBy = "system"
        )
        
        val result = movementHandler.handle(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }
}
