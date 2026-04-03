package com.wms.application.inventory.query

import com.wms.application.inventory.dto.InventoryDto
import com.wms.application.inventory.dto.PagedResponse
import com.wms.domain.inventory.repository.InventoryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetInventoryQueryHandler(
    private val inventoryRepository: InventoryRepository
) {
    
    fun handle(criteria: InventorySearchCriteria): PagedResponse<InventoryDto> {
        val sortOrder = if (criteria.sortDirection == SortDirection.ASC) {
            Sort.Direction.ASC
        } else {
            Sort.Direction.DESC
        }
        
        val pageable = PageRequest.of(
            criteria.page,
            criteria.size,
            Sort.by(sortOrder, criteria.sortBy)
        )
        
        val page = inventoryRepository.searchWithCriteria(
            itemId = criteria.itemId,
            locationId = criteria.locationId,
            warehouseId = criteria.warehouseId,
            status = criteria.status,
            pageable = pageable
        )
        
        return PagedResponse.from(
            content = InventoryDto.fromDomainList(page.content),
            page = page.number,
            size = page.size,
            totalElements = page.totalElements
        )
    }
}
