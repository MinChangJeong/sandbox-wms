package com.wms.domain.common.exception

sealed class WmsException(
    val errorCode: String,
    override val message: String,
    val details: Map<String, Any> = emptyMap(),
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    companion object {
        fun InventoryNotFound(inventoryId: Long): InventoryException.NotFound {
            return InventoryException.NotFound(inventoryId)
        }
    }
}

sealed class WarehouseException(
    errorCode: String,
    message: String,
    details: Map<String, Any> = emptyMap()
) : WmsException(errorCode, message, details) {
    
    class NotFound(warehouseId: Long) : WarehouseException(
        "WH_001",
        "창고를 찾을 수 없습니다",
        mapOf("warehouseId" to warehouseId)
    )
    
    class DuplicateCode(warehouseCode: String) : WarehouseException(
        "WH_002",
        "이미 존재하는 창고 코드입니다",
        mapOf("warehouseCode" to warehouseCode)
    )
    
    class NotActive(warehouseId: Long) : WarehouseException(
        "WH_003",
        "활성화되지 않은 창고입니다",
        mapOf("warehouseId" to warehouseId)
    )
}

sealed class ZoneException(
    errorCode: String,
    message: String,
    details: Map<String, Any> = emptyMap()
) : WmsException(errorCode, message, details) {
    
    class NotFound(zoneId: Long) : ZoneException(
        "ZN_001",
        "구역을 찾을 수 없습니다",
        mapOf("zoneId" to zoneId)
    )
    
    class DuplicateCode(zoneCode: String, warehouseId: Long) : ZoneException(
        "ZN_002",
        "해당 창고 내 이미 존재하는 구역 코드입니다",
        mapOf("zoneCode" to zoneCode, "warehouseId" to warehouseId)
    )
    
    class CannotChangeTypeWithInventory(zoneId: Long) : ZoneException(
        "ZN_003",
        "재고가 있는 구역의 타입을 변경할 수 없습니다",
        mapOf("zoneId" to zoneId)
    )
    
    class InvalidTemperatureRange(min: Int, max: Int) : ZoneException(
        "ZN_004",
        "온도 범위가 잘못되었습니다",
        mapOf("min" to min, "max" to max)
    )
}

sealed class LocationException(
    errorCode: String,
    message: String,
    details: Map<String, Any> = emptyMap()
) : WmsException(errorCode, message, details) {
    
    class NotFound(locationId: Long) : LocationException(
        "LOC_001",
        "로케이션을 찾을 수 없습니다",
        mapOf("locationId" to locationId)
    )
    
    class DuplicateCode(locationCode: String) : LocationException(
        "LOC_002",
        "이미 존재하는 로케이션 코드입니다",
        mapOf("locationCode" to locationCode)
    )
    
    class CannotReduceCapacity(locationId: Long) : LocationException(
        "LOC_003",
        "재고가 있는 로케이션의 용량을 축소할 수 없습니다",
        mapOf("locationId" to locationId)
    )
    
    class CannotAcceptInventory(locationId: Long, currentStatus: String) : LocationException(
        "LOC_004",
        "현재 상태에서는 재고를 적치할 수 없습니다",
        mapOf("locationId" to locationId, "currentStatus" to currentStatus)
    )
}

sealed class ItemException(
    errorCode: String,
    message: String,
    details: Map<String, Any> = emptyMap()
) : WmsException(errorCode, message, details) {
    
    class NotFound(itemId: Long) : ItemException(
        "ITEM_001",
        "품목을 찾을 수 없습니다",
        mapOf("itemId" to itemId)
    )
    
    class DuplicateCode(itemCode: String) : ItemException(
        "ITEM_002",
        "이미 존재하는 품목 코드입니다",
        mapOf("itemCode" to itemCode)
    )
    
    class DuplicateBarcode(barcode: String) : ItemException(
        "ITEM_003",
        "이미 존재하는 바코드입니다",
        mapOf("barcode" to barcode)
    )
    
    class CannotChangeStorageType(itemId: Long) : ItemException(
        "ITEM_004",
        "재고가 있는 품목의 보관 타입을 변경할 수 없습니다",
        mapOf("itemId" to itemId)
    )
}

sealed class InventoryException(
    errorCode: String,
    message: String,
    details: Map<String, Any> = emptyMap()
) : WmsException(errorCode, message, details) {
    
    class NotFound(inventoryId: Long) : InventoryException(
        "INV_001",
        "재고를 찾을 수 없습니다",
        mapOf("inventoryId" to inventoryId)
    )
    
    class InsufficientQuantity(
        inventoryId: Long,
        requested: Int,
        available: Int
    ) : InventoryException(
        "INV_002",
        "가용 재고가 부족합니다",
        mapOf("inventoryId" to inventoryId, "requested" to requested, "available" to available)
    )
    
    class InvalidStatusTransition(
        inventoryId: Long,
        from: String,
        to: String
    ) : InventoryException(
        "INV_003",
        "유효하지 않은 상태 전이입니다",
        mapOf("inventoryId" to inventoryId, "from" to from, "to" to to)
    )
    
    class AlreadyAllocated(inventoryId: Long) : InventoryException(
        "INV_004",
        "이미 할당된 재고입니다",
        mapOf("inventoryId" to inventoryId)
    )
}

class ValidationException(
    message: String,
    val violations: List<FieldViolation>
) : WmsException(
    "VAL_001",
    message,
    mapOf("violations" to violations)
)

data class FieldViolation(
    val field: String,
    val message: String,
    val rejectedValue: Any?
)

class ConcurrencyException(
    message: String,
    val resourceType: String,
    val resourceId: Long
) : WmsException(
    "CONC_001",
    message,
    mapOf("resourceType" to resourceType, "resourceId" to resourceId)
)
