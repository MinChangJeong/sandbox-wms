package com.wms.domain.warehouse.event

import com.wms.domain.common.BaseDomainEvent

class WarehouseCreatedEvent(
    aggregateId: Long,
    val warehouseCode: String
) : BaseDomainEvent(aggregateId, "Warehouse")

class WarehouseActivatedEvent(
    aggregateId: Long,
    val warehouseCode: String
) : BaseDomainEvent(aggregateId, "Warehouse")

class WarehouseDeactivatedEvent(
    aggregateId: Long,
    val warehouseCode: String
) : BaseDomainEvent(aggregateId, "Warehouse")
