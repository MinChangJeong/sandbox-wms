package com.wms.application.inventory.query

data class InventorySearchCriteria(
    val itemId: Long? = null,
    val locationId: Long? = null,
    val warehouseId: Long? = null,
    val zoneId: Long? = null,
    val status: String? = null,
    
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "createdAt",
    val sortDirection: SortDirection = SortDirection.DESC
) {
    init {
        require(page >= 0) { "페이지는 0 이상이어야 합니다" }
        require(size in 1..100) { "페이지 크기는 1~100이어야 합니다" }
    }
}

enum class SortDirection {
    ASC, DESC
}
