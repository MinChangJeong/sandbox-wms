package com.wms.domain.common

abstract class AggregateRoot : BaseEntity() {
    @Transient
    private val _domainEvents = mutableListOf<DomainEvent>()
    
    val domainEvents: List<DomainEvent> get() = _domainEvents.toList()
    
    protected fun registerEvent(event: DomainEvent) {
        _domainEvents.add(event)
    }
    
    fun clearDomainEvents() {
        _domainEvents.clear()
    }
}
