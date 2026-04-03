package com.wms.presentation.controller

import com.wms.application.warehouse.dto.CreateWarehouseRequest
import com.wms.application.warehouse.dto.UpdateWarehouseRequest
import com.wms.application.warehouse.service.WarehouseCommandService
import com.wms.application.warehouse.service.WarehouseQueryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant

data class ApiResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T?,
    val timestamp: String = Instant.now().toString()
)

@RestController
@RequestMapping("/api/v1/warehouses")
class WarehouseController(
    private val commandService: WarehouseCommandService,
    private val queryService: WarehouseQueryService
) {
    
    @PostMapping
    fun createWarehouse(
        @RequestBody request: CreateWarehouseRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Long>> {
        val warehouseId = commandService.createWarehouse(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                statusCode = 201,
                message = "창고가 생성되었습니다",
                data = warehouseId
            )
        )
    }
    
    @PutMapping("/{id}")
    fun updateWarehouse(
        @PathVariable id: Long,
        @RequestBody request: UpdateWarehouseRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        commandService.updateWarehouse(id, request, userId)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "창고가 수정되었습니다",
                data = null
            )
        )
    }
    
    @PatchMapping("/{id}/activate")
    fun activateWarehouse(
        @PathVariable id: Long,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        commandService.activateWarehouse(id, userId)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "창고가 활성화되었습니다",
                data = null
            )
        )
    }
    
    @PatchMapping("/{id}/deactivate")
    fun deactivateWarehouse(
        @PathVariable id: Long,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        commandService.deactivateWarehouse(id, userId)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "창고가 비활성화되었습니다",
                data = null
            )
        )
    }
    
    @GetMapping("/{id}")
    fun findWarehouse(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> {
        val warehouse = queryService.findWarehouseById(id)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = warehouse
            )
        )
    }
    
    @GetMapping
    fun findAllWarehouses(): ResponseEntity<ApiResponse<List<Any>>> {
        val warehouses = queryService.findAllWarehouses()
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = warehouses
            )
        )
    }
    
    @GetMapping("/code/{code}")
    fun findWarehouseByCode(@PathVariable code: String): ResponseEntity<ApiResponse<Any>> {
        val warehouse = queryService.findWarehouseByCode(code)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = warehouse
            )
        )
    }
}
