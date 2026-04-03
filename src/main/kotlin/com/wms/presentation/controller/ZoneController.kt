package com.wms.presentation.controller

import com.wms.application.zone.dto.CreateZoneRequest
import com.wms.application.zone.dto.UpdateZoneRequest
import com.wms.application.zone.service.ZoneCommandService
import com.wms.application.zone.service.ZoneQueryService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/zones")
class ZoneController(
    private val commandService: ZoneCommandService,
    private val queryService: ZoneQueryService
) {
    
    @PostMapping
    fun createZone(
        @RequestBody request: CreateZoneRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Long>> {
        val zoneId = commandService.createZone(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                statusCode = 201,
                message = "구역이 생성되었습니다",
                data = zoneId
            )
        )
    }
    
    @PutMapping("/{id}")
    fun updateZone(
        @PathVariable id: Long,
        @RequestBody request: UpdateZoneRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        commandService.updateZone(id, request, userId)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "구역이 수정되었습니다",
                data = null
            )
        )
    }
    
    @GetMapping("/{id}")
    fun findZone(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> {
        val zone = queryService.findZoneById(id)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = zone
            )
        )
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    fun findZonesByWarehouse(
        @PathVariable warehouseId: Long,
        pageable: Pageable?
    ): ResponseEntity<ApiResponse<Any>> {
        val zones = if (pageable != null) {
            queryService.findZonesByWarehouseId(warehouseId, pageable)
        } else {
            queryService.findZonesByWarehouseId(warehouseId)
        }
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = zones
            )
        )
    }
}
