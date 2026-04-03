package com.wms.infrastructure.persistence.adapter

import com.wms.domain.item.model.Item
import com.wms.domain.item.repository.ItemRepository
import com.wms.infrastructure.persistence.mapper.ItemMapper
import com.wms.infrastructure.persistence.repository.ItemJpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ItemRepositoryAdapter(
    private val jpaRepository: ItemJpaRepository,
    private val mapper: ItemMapper
) : ItemRepository {
    
    override fun save(item: Item): Item {
        val saved = jpaRepository.save(mapper.toEntity(item))
        return mapper.toDomain(saved)
    }
    
    override fun findById(id: Long): Item? {
        return jpaRepository.findById(id).orElse(null)?.let { mapper.toDomain(it) }
    }
    
    override fun findByCode(code: String): Item? {
        return jpaRepository.findByItemCode(code)?.let { mapper.toDomain(it) }
    }
    
    override fun findByBarcode(barcode: String): Item? {
        return jpaRepository.findByBarcode(barcode)?.let { mapper.toDomain(it) }
    }
    
    override fun findAll(pageable: Pageable): Page<Item> {
        return jpaRepository.findAll(pageable).map { mapper.toDomain(it) }
    }
    
    override fun delete(item: Item) {
        jpaRepository.deleteById(item.id)
    }
    
    override fun existsByCode(code: String): Boolean {
        return jpaRepository.findByItemCode(code) != null
    }
    
    override fun existsByBarcode(barcode: String): Boolean {
        return jpaRepository.findByBarcode(barcode) != null
    }
}
