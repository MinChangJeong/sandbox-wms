package com.wms.domain.item.repository

import com.wms.domain.item.model.Item
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ItemRepository {
    fun findById(id: Long): Item?
    fun findByCode(code: String): Item?
    fun findByBarcode(barcode: String): Item?
    fun findAll(pageable: Pageable): Page<Item>
    fun save(item: Item): Item
    fun delete(item: Item)
    fun existsByCode(code: String): Boolean
    fun existsByBarcode(barcode: String): Boolean
}
