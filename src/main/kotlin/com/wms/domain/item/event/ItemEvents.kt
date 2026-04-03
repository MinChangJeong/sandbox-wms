package com.wms.domain.item.event

import com.wms.domain.common.BaseDomainEvent

class ItemCreatedEvent(
    aggregateId: Long,
    val itemCode: String
) : BaseDomainEvent(aggregateId, "Item")
