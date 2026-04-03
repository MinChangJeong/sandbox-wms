package com.wms.infrastructure.persistence.repository

import com.wms.domain.item.model.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ItemJpaRepository : JpaRepository<Item, Long> {
    fun findByItemCode(itemCode: String): Item?
    
    @Query(value = "SELECT * FROM items WHERE barcode = :barcode", nativeQuery = true)
    fun findByBarcode(@Param("barcode") barcode: String): Item?
    
    fun findAllByIsDeletedFalse(): List<Item>
}
