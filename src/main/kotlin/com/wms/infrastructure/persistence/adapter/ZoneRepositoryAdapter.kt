package com.wms.infrastructure.persistence.adapter

import com.wms.domain.zone.model.Zone
import com.wms.domain.zone.repository.ZoneRepository
import com.wms.infrastructure.persistence.mapper.ZoneMapper
import com.wms.infrastructure.persistence.repository.ZoneJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Repository

@Repository
class ZoneRepositoryAdapter(
    private val jpaRepository: ZoneJpaRepository,
    private val mapper: ZoneMapper
) : ZoneRepository {
    
    override fun save(zone: Zone): Zone {
        val saved = jpaRepository.save(mapper.toEntity(zone))
        return mapper.toDomain(saved)
    }
    
    override fun findById(id: Long): Zone? {
        return jpaRepository.findById(id).orElse(null)?.let { mapper.toDomain(it) }
    }
    
    override fun findByCode(code: String): Zone? {
        return jpaRepository.findAll()
            .filter { !it.isDeleted }
            .firstOrNull { it.zoneCode == code }
            ?.let { mapper.toDomain(it) }
    }
    
    override fun findByWarehouseId(warehouseId: Long): List<Zone> {
        return jpaRepository.findAllByWarehouseIdAndIsDeletedFalse(warehouseId).map { mapper.toDomain(it) }
    }
    
    override fun findByWarehouseId(warehouseId: Long, pageable: Pageable): Page<Zone> {
        val allZones = jpaRepository.findAllByWarehouseIdAndIsDeletedFalse(warehouseId)
        val startIndex = pageable.pageNumber * pageable.pageSize
        val endIndex = minOf(startIndex + pageable.pageSize, allZones.size)
        val content = if (startIndex < allZones.size) {
            allZones.subList(startIndex, endIndex).map { mapper.toDomain(it) }
        } else {
            emptyList()
        }
        return PageImpl(content, pageable, allZones.size.toLong())
    }
    
    override fun findAll(pageable: Pageable): Page<Zone> {
        return jpaRepository.findAll(pageable).map { mapper.toDomain(it) }
    }
    
    override fun delete(zone: Zone) {
        jpaRepository.deleteById(zone.id)
    }
    
    override fun existsByCodeAndWarehouseId(code: String, warehouseId: Long): Boolean {
        return jpaRepository.findByZoneCodeAndWarehouseId(code, warehouseId) != null
    }
}
