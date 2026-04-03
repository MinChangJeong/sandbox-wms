package com.wms.presentation.controller

import com.wms.application.item.dto.CreateItemRequest
import com.wms.application.item.dto.UpdateItemRequest
import com.wms.application.item.service.ItemCommandService
import com.wms.application.item.service.ItemQueryService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/items")
class ItemController(
    private val commandService: ItemCommandService,
    private val queryService: ItemQueryService
) {
    
    @PostMapping
    fun createItem(
        @RequestBody request: CreateItemRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Long>> {
        val itemId = commandService.createItem(request, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse(
                statusCode = 201,
                message = "품목이 생성되었습니다",
                data = itemId
            )
        )
    }
    
    @PutMapping("/{id}")
    fun updateItem(
        @PathVariable id: Long,
        @RequestBody request: UpdateItemRequest,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<ApiResponse<Unit>> {
        commandService.updateItem(id, request, userId)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "품목이 수정되었습니다",
                data = null
            )
        )
    }
    
    @GetMapping("/{id}")
    fun findItem(@PathVariable id: Long): ResponseEntity<ApiResponse<Any>> {
        val item = queryService.findItemById(id)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = item
            )
        )
    }
    
    @GetMapping("/code/{code}")
    fun findItemByCode(@PathVariable code: String): ResponseEntity<ApiResponse<Any>> {
        val item = queryService.findItemByCode(code)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = item
            )
        )
    }
    
    @GetMapping("/barcode/{barcode}")
    fun findItemByBarcode(@PathVariable barcode: String): ResponseEntity<ApiResponse<Any>> {
        val item = queryService.findItemByBarcode(barcode)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = item
            )
        )
    }
    
    @GetMapping
    fun findAllItems(pageable: Pageable): ResponseEntity<ApiResponse<Any>> {
        val items = queryService.findAllItems(pageable)
        return ResponseEntity.ok(
            ApiResponse(
                statusCode = 200,
                message = "조회 성공",
                data = items
            )
        )
    }
}
