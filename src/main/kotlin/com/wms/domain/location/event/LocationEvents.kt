package com.wms.domain.location.event

import com.wms.domain.common.BaseDomainEvent

class LocationCreatedEvent(
    aggregateId: Long,
    val locationCode: String,
    val zoneId: Long
) : BaseDomainEvent(aggregateId, "Location")
