package com.wms.application.warehouse.service

import com.wms.application.warehouse.dto.CreateWarehouseRequest
import com.wms.application.warehouse.dto.UpdateWarehouseRequest
import com.wms.application.warehouse.dto.WarehouseResponse
import com.wms.domain.common.status.WarehouseType
import com.wms.domain.warehouse.model.Warehouse
import com.wms.domain.warehouse.repository.WarehouseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional
class WarehouseCommandService(
    private val warehouseRepository: WarehouseRepository
) {
    
    fun createWarehouse(request: CreateWarehouseRequest, userId: String): Long {
        require(!warehouseRepository.existsByCode(request.warehouseCode)) { 
            "창고 코드 ${request.warehouseCode}는 이미 존재합니다"
        }
        
        val warehouse = Warehouse.create(
            warehouseCode = request.warehouseCode,
            warehouseName = request.warehouseName,
            address = request.address,
            warehouseType = WarehouseType.fromCode(request.warehouseType),
            createdBy = userId
        )
        
        val saved = warehouseRepository.save(warehouse)
        return saved.id
    }
    
    fun updateWarehouse(id: Long, request: UpdateWarehouseRequest, userId: String) {
        val warehouse = warehouseRepository.findById(id) 
            ?: throw IllegalArgumentException("창고를 찾을 수 없습니다: $id")
        
        warehouse.updateInfo(
            warehouseName = request.warehouseName,
            address = request.address,
            warehouseType = WarehouseType.fromCode(request.warehouseType),
            updatedBy = userId
        )
        
        warehouseRepository.save(warehouse)
    }
    
    fun activateWarehouse(id: Long, userId: String) {
        val warehouse = warehouseRepository.findById(id)
            ?: throw IllegalArgumentException("창고를 찾을 수 없습니다: $id")
        
        warehouse.activate(userId)
        warehouseRepository.save(warehouse)
    }
    
    fun deactivateWarehouse(id: Long, userId: String) {
        val warehouse = warehouseRepository.findById(id)
            ?: throw IllegalArgumentException("창고를 찾을 수 없습니다: $id")
        
        warehouse.deactivate(userId)
        warehouseRepository.save(warehouse)
    }
}

@Service
@Transactional(readOnly = true)
class WarehouseQueryService(
    private val warehouseRepository: WarehouseRepository
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun findWarehouseById(id: Long): WarehouseResponse {
        val warehouse = warehouseRepository.findById(id)
            ?: throw IllegalArgumentException("창고를 찾을 수 없습니다: $id")
        
        return warehouse.toResponse()
    }
    
    fun findWarehouseByCode(code: String): WarehouseResponse {
        val warehouse = warehouseRepository.findByCode(code)
            ?: throw IllegalArgumentException("창고를 찾을 수 없습니다: $code")
        
        return warehouse.toResponse()
    }
    
    fun findAllWarehouses(): List<WarehouseResponse> {
        return warehouseRepository.findAll().map { it.toResponse() }
    }
    
    private fun Warehouse.toResponse(): WarehouseResponse {
        return WarehouseResponse(
            id = this.id,
            warehouseCode = this.warehouseCode,
            warehouseName = this.warehouseName,
            address = this.address,
            warehouseType = this.warehouseType.code,
            isActive = this.isActive,
            createdAt = this.createdAt.format(formatter),
            updatedAt = this.updatedAt.format(formatter)
        )
    }
}
