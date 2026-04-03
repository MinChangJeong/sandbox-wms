package com.wms.domain.common

import java.time.Instant
import java.util.UUID

/**
 * 도메인 이벤트 인터페이스
 * 모든 도메인 이벤트는 이 인터페이스를 구현해야 함
 */
interface DomainEvent {
    val eventId: String
    val occurredAt: Instant
    val aggregateId: Long
    val aggregateType: String
}

/**
 * 기본 도메인 이벤트 구현
 */
abstract class BaseDomainEvent(
    override val aggregateId: Long,
    override val aggregateType: String,
    override val eventId: String = UUID.randomUUID().toString(),
    override val occurredAt: Instant = Instant.now()
) : DomainEvent
