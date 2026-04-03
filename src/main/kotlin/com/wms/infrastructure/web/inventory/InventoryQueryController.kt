package com.wms.infrastructure.web.inventory

import com.wms.application.inventory.dto.InventoryDto
import com.wms.application.inventory.dto.PagedResponse
import com.wms.application.inventory.query.GetInventoryQueryHandler
import com.wms.application.inventory.query.InventorySearchCriteria
import com.wms.application.inventory.query.SortDirection
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/inventory/queries")
class InventoryQueryController(
    private val getInventoryQueryHandler: GetInventoryQueryHandler
) {
    
    @GetMapping
    fun searchInventories(
        @RequestParam(required = false) itemId: Long?,
        @RequestParam(required = false) locationId: Long?,
        @RequestParam(required = false) warehouseId: Long?,
        @RequestParam(required = false) zoneId: Long?,
        @RequestParam(required = false) status: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "DESC") sortDirection: String
    ): ResponseEntity<PagedResponse<InventoryDto>> {
        val criteria = InventorySearchCriteria(
            itemId = itemId,
            locationId = locationId,
            warehouseId = warehouseId,
            zoneId = zoneId,
            status = status,
            page = page,
            size = size,
            sortBy = sortBy,
            sortDirection = SortDirection.valueOf(sortDirection.uppercase())
        )
        
        return ResponseEntity.ok(getInventoryQueryHandler.handle(criteria))
    }
}
