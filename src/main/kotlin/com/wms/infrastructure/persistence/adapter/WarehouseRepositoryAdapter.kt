package com.wms.infrastructure.persistence.adapter

import com.wms.domain.warehouse.model.Warehouse
import com.wms.domain.warehouse.repository.WarehouseRepository
import com.wms.infrastructure.persistence.mapper.WarehouseMapper
import com.wms.infrastructure.persistence.repository.WarehouseJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class WarehouseRepositoryAdapter(
    private val jpaRepository: WarehouseJpaRepository,
    private val mapper: WarehouseMapper
) : WarehouseRepository {
    
    override fun save(warehouse: Warehouse): Warehouse {
        val saved = jpaRepository.save(mapper.toEntity(warehouse))
        return mapper.toDomain(saved)
    }
    
    override fun findById(id: Long): Warehouse? {
        return jpaRepository.findById(id).orElse(null)?.let { mapper.toDomain(it) }
    }
    
    override fun findByCode(code: String): Warehouse? {
        return jpaRepository.findByWarehouseCode(code)?.let { mapper.toDomain(it) }
    }
    
    override fun findAll(pageable: Pageable): Page<Warehouse> {
        return jpaRepository.findAll(pageable).map { mapper.toDomain(it) }
    }
    
    override fun findAll(): List<Warehouse> {
        return jpaRepository.findAllByIsDeletedFalse().map { mapper.toDomain(it) }
    }
    
    override fun delete(warehouse: Warehouse) {
        jpaRepository.deleteById(warehouse.id)
    }
    
    override fun existsByCode(code: String): Boolean {
        return jpaRepository.findByWarehouseCode(code) != null
    }
}
