package com.wms.domain.zone.event

import com.wms.domain.common.BaseDomainEvent

class ZoneCreatedEvent(
    aggregateId: Long,
    val zoneCode: String,
    val warehouseId: Long
) : BaseDomainEvent(aggregateId, "Zone")
