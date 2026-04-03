package com.wms.infrastructure.web.inventory.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AdjustInventoryRequest(
    @field:NotNull
    val inventoryId: Long? = null,
    
    @field:NotBlank
    val adjustmentType: String? = null,
    
    @field:Min(1)
    val quantity: Int? = null,
    
    @field:NotBlank
    val reason: String? = null
)

data class CreateMovementRequest(
    @field:NotNull
    val inventoryId: Long? = null,
    
    @field:NotNull
    val fromLocationId: Long? = null,
    
    @field:NotNull
    val toLocationId: Long? = null,
    
    @field:Min(1)
    val quantity: Int? = null,
    
    @field:NotBlank
    val reason: String? = null
)
