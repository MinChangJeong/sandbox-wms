package com.wms.application.zone.service

import com.wms.application.zone.dto.CreateZoneRequest
import com.wms.application.zone.dto.UpdateZoneRequest
import com.wms.application.zone.dto.ZoneResponse
import com.wms.domain.common.status.ZoneType
import com.wms.domain.zone.model.Zone
import com.wms.domain.zone.repository.ZoneRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter

@Service
@Transactional
class ZoneCommandService(
    private val zoneRepository: ZoneRepository
) {
    
    fun createZone(request: CreateZoneRequest, userId: String): Long {
        require(!zoneRepository.existsByCodeAndWarehouseId(request.zoneCode, request.warehouseId)) {
            "구역 코드 ${request.zoneCode}는 창고 ${request.warehouseId}에 이미 존재합니다"
        }
        
        val zone = Zone.create(
            warehouseId = request.warehouseId,
            zoneCode = request.zoneCode,
            zoneName = request.zoneName,
            zoneType = ZoneType.fromCode(request.zoneType),
            temperatureMin = request.temperatureMin,
            temperatureMax = request.temperatureMax,
            createdBy = userId
        )
        
        val saved = zoneRepository.save(zone)
        return saved.id
    }
    
    fun updateZone(id: Long, request: UpdateZoneRequest, userId: String) {
        val zone = zoneRepository.findById(id)
            ?: throw IllegalArgumentException("구역을 찾을 수 없습니다: $id")
        
        zone.updateInfo(
            zoneName = request.zoneName,
            zoneType = ZoneType.fromCode(request.zoneType),
            temperatureMin = request.temperatureMin,
            temperatureMax = request.temperatureMax,
            updatedBy = userId
        )
        
        zoneRepository.save(zone)
    }
}

@Service
@Transactional(readOnly = true)
class ZoneQueryService(
    private val zoneRepository: ZoneRepository
) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun findZoneById(id: Long): ZoneResponse {
        val zone = zoneRepository.findById(id)
            ?: throw IllegalArgumentException("구역을 찾을 수 없습니다: $id")
        
        return zone.toResponse()
    }
    
    fun findZonesByWarehouseId(warehouseId: Long): List<ZoneResponse> {
        return zoneRepository.findByWarehouseId(warehouseId).map { it.toResponse() }
    }
    
    fun findZonesByWarehouseId(warehouseId: Long, pageable: Pageable): Page<ZoneResponse> {
        return zoneRepository.findByWarehouseId(warehouseId, pageable).map { it.toResponse() }
    }
    
    private fun Zone.toResponse(): ZoneResponse {
        return ZoneResponse(
            id = this.id,
            warehouseId = this.warehouseId,
            zoneCode = this.zoneCode,
            zoneName = this.zoneName,
            zoneType = this.zoneType.code,
            temperatureMin = this.temperatureMin,
            temperatureMax = this.temperatureMax,
            isActive = this.isActive,
            createdAt = this.createdAt.format(formatter),
            updatedAt = this.updatedAt.format(formatter)
        )
    }
}
