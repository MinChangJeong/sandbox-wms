package com.wms.application.item.service

import com.wms.application.item.dto.CreateItemRequest
import com.wms.application.item.dto.UpdateItemRequest
import com.wms.application.item.dto.ItemResponse
import com.wms.domain.common.status.ItemUnit
import com.wms.domain.common.status.StorageType
import com.wms.domain.item.model.Item
import com.wms.domain.item.repository.ItemRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional
class ItemCommandService(
    private val itemRepository: ItemRepository
) {
    
    fun createItem(request: CreateItemRequest, userId: String): Long {
        require(!itemRepository.existsByCode(request.itemCode)) {
            "품목 코드 ${request.itemCode}는 이미 존재합니다"
        }
        
        if (request.barcode != null) {
            require(!itemRepository.existsByBarcode(request.barcode)) {
                "바코드 ${request.barcode}는 이미 존재합니다"
            }
        }
        
        val item = Item.create(
            itemCode = request.itemCode,
            itemName = request.itemName,
            barcode = request.barcode,
            category = request.category,
            unit = ItemUnit.fromCode(request.unit),
            storageType = StorageType.fromCode(request.storageType),
            expiryManaged = request.expiryManaged,
            lotManaged = request.lotManaged,
            createdBy = userId
        )
        
        val saved = itemRepository.save(item)
        return saved.id
    }
    
    fun updateItem(id: Long, request: UpdateItemRequest, userId: String) {
        val item = itemRepository.findById(id)
            ?: throw IllegalArgumentException("품목을 찾을 수 없습니다: $id")
        
        item.updateInfo(
            itemName = request.itemName,
            category = request.category,
            unit = ItemUnit.fromCode(request.unit),
            storageType = StorageType.fromCode(request.storageType),
            updatedBy = userId
        )
        
        itemRepository.save(item)
    }
}

@Service
@Transactional(readOnly = true)
class ItemQueryService(
    private val itemRepository: ItemRepository
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun findItemById(id: Long): ItemResponse {
        val item = itemRepository.findById(id)
            ?: throw IllegalArgumentException("품목을 찾을 수 없습니다: $id")
        
        return item.toResponse()
    }
    
    fun findItemByCode(code: String): ItemResponse {
        val item = itemRepository.findByCode(code)
            ?: throw IllegalArgumentException("품목을 찾을 수 없습니다: $code")
        
        return item.toResponse()
    }
    
    fun findItemByBarcode(barcode: String): ItemResponse {
        val item = itemRepository.findByBarcode(barcode)
            ?: throw IllegalArgumentException("바코드를 찾을 수 없습니다: $barcode")
        
        return item.toResponse()
    }
    
    fun findAllItems(pageable: Pageable): Page<ItemResponse> {
        return itemRepository.findAll(pageable).map { it.toResponse() }
    }
    
    private fun Item.toResponse(): ItemResponse {
        return ItemResponse(
            id = this.id,
            itemCode = this.itemCode,
            itemName = this.itemName,
            barcode = this.barcode,
            category = this.category,
            unit = this.unit.code,
            storageType = this.storageType.code,
            expiryManaged = this.expiryManaged,
            lotManaged = this.lotManaged,
            isActive = this.isActive,
            createdAt = this.createdAt.format(formatter),
            updatedAt = this.updatedAt.format(formatter)
        )
    }
}
