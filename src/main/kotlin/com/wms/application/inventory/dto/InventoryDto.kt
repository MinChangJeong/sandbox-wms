package com.wms.application.inventory.dto

import com.wms.domain.inventory.model.Inventory
import java.time.LocalDateTime

data class InventoryDto(
    val id: Long,
    val itemId: Long,
    val locationId: Long,
    val quantity: Int,
    val availableQty: Int,
    val allocatedQty: Int,
    val status: String,
    val statusDisplayName: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromDomain(inventory: Inventory): InventoryDto {
            return InventoryDto(
                id = inventory.id,
                itemId = inventory.itemId,
                locationId = inventory.locationId,
                quantity = inventory.quantity,
                availableQty = inventory.availableQty,
                allocatedQty = inventory.allocatedQty,
                status = inventory.status.code,
                statusDisplayName = inventory.status.displayName,
                createdAt = inventory.createdAt
            )
        }
        
        fun fromDomainList(inventories: List<Inventory>): List<InventoryDto> {
            return inventories.map { fromDomain(it) }
        }
    }
}

data class InventoryHistoryDto(
    val id: Long,
    val inventoryId: Long,
    val transactionType: String,
    val changeQuantity: Int,
    val beforeQuantity: Int,
    val afterQuantity: Int,
    val referenceType: String?,
    val referenceId: Long?,
    val reason: String?,
    val createdAt: LocalDateTime,
    val createdBy: String
)

data class PagedResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
) {
    companion object {
        fun <T> from(
            content: List<T>,
            page: Int,
            size: Int,
            totalElements: Long
        ): PagedResponse<T> {
            val totalPages = (totalElements + size - 1) / size
            return PagedResponse(
                content = content,
                page = page,
                size = size,
                totalElements = totalElements,
                totalPages = totalPages.toInt(),
                hasNext = page < totalPages - 1,
                hasPrevious = page > 0
            )
        }
    }
}

data class CommandResult(
    val id: Long,
    val success: Boolean = true,
    val message: String? = null
)
