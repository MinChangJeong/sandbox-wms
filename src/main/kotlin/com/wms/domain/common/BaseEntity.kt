package com.wms.domain.common

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0
    
    @Version
    open val version: Long = 0
    
    @Column(name = "created_at", nullable = false, updatable = false)
    open val createdAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "created_by", nullable = false, updatable = false)
    open var createdBy: String = ""
    
    @Column(name = "updated_at", nullable = false)
    open var updatedAt: LocalDateTime = LocalDateTime.now()
    
    @Column(name = "updated_by", nullable = false)
    open var updatedBy: String = ""
    
    @Column(name = "is_deleted", nullable = false)
    open var isDeleted: Boolean = false
}
