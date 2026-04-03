package com.wms.presentation.controller

import com.wms.application.location.dto.CreateLocationRequest
import com.wms.application.location.dto.UpdateLocationRequest
import com.wms.application.location.service.LocationCommandService
import com.wms.application.location.service.LocationQueryService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/locations")
class LocationController(
    private val commandService: LocationCommandService,
    private val queryService: LocationQueryService
) {
    
    @PostMapping
    fun createLocation(
        @RequestBody request: CreateLocationRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Long>> {
        val locationId = commandService.createLocation(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                statusCode = 201,
                message = "로케이션이 생성되었습니다",
                data = locationId
            )
        )
    }
    
    @PutMapping("/{id}")
    fun updateLocation(
        @PathVariable id: Long,
        @RequestBody request: UpdateLocationRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        commandService.updateLocation(id, request, userId)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "로케이션이 수정되었습니다",
                data = null
            )
        )
    }
    
    @GetMapping("/{id}")
    fun findLocation(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> {
        val location = queryService.findLocationById(id)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = location
            )
        )
    }
    
    @GetMapping("/code/{code}")
    fun findLocationByCode(@PathVariable code: String): ResponseEntity<ApiResponse<Any>> {
        val location = queryService.findLocationsByCode(code)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = location
            )
        )
    }
    
    @GetMapping("/zone/{zoneId}")
    fun findLocationsByZone(
        @PathVariable zoneId: Long,
        pageable: Pageable?
    ): ResponseEntity<ApiResponse<Any>> {
        val locations = if (pageable != null) {
            queryService.findLocationsByZoneId(zoneId, pageable)
        } else {
            queryService.findLocationsByZoneId(zoneId)
        }
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = locations
            )
        )
    }
}
