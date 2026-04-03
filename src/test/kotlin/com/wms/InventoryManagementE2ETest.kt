package com.wms

import com.fasterxml.jackson.databind.ObjectMapper
import com.wms.domain.inventory.model.Inventory
import com.wms.domain.inventory.repository.InventoryRepository
import com.wms.infrastructure.web.inventory.dto.AdjustInventoryRequest
import com.wms.infrastructure.web.inventory.dto.CreateMovementRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class InventoryManagementE2ETest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val inventoryRepository: InventoryRepository,
    @Autowired private val objectMapper: ObjectMapper
) {
    
    private var inventoryId: Long = 0
    
    @BeforeEach
    fun setup() {
        val inventory = Inventory.create(
            itemId = 1,
            locationId = 1,
            quantity = 100,
            createdBy = "test-user"
        )
        val saved = inventoryRepository.save(inventory)
        inventoryId = saved.id
    }
    
    @Test
    fun queryInventoryWithPagination() {
        mockMvc.perform(
            get("/api/v1/inventory/queries")
                .param("page", "0")
                .param("size", "20")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").isNumber)
            .andExpect(jsonPath("$.totalElements").isNumber)
    }
    
    @Test
    fun adjustInventoryIncrease() {
        val request = AdjustInventoryRequest(
            inventoryId = inventoryId,
            adjustmentType = "INCREASE",
            quantity = 50,
            reason = "입고 보정"
        )
        
        mockMvc.perform(
            post("/api/v1/inventory/commands/adjustments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
        
        val updated = inventoryRepository.findById(inventoryId)!!
        assert(updated.quantity == 150) { "Expected quantity 150, got ${updated.quantity}" }
    }
    
    @Test
    fun adjustInventoryDecrease() {
        val request = AdjustInventoryRequest(
            inventoryId = inventoryId,
            adjustmentType = "DECREASE",
            quantity = 20,
            reason = "손실"
        )
        
        mockMvc.perform(
            post("/api/v1/inventory/commands/adjustments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
        
        val updated = inventoryRepository.findById(inventoryId)!!
        assert(updated.quantity == 80) { "Expected quantity 80, got ${updated.quantity}" }
    }
    
    @Test
    fun moveInventoryBetweenLocations() {
        val request = CreateMovementRequest(
            inventoryId = inventoryId,
            fromLocationId = 1,
            toLocationId = 2,
            quantity = 30,
            reason = "로케이션 이동"
        )
        
        mockMvc.perform(
            post("/api/v1/inventory/commands/movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
        
        val source = inventoryRepository.findById(inventoryId)!!
        assert(source.quantity == 70) { "Expected source quantity 70, got ${source.quantity}" }
        
        val target = inventoryRepository.findByItemIdAndLocationId(1, 2)
        assert(target != null) { "Target inventory should exist" }
        assert(target?.quantity == 30) { "Expected target quantity 30, got ${target?.quantity}" }
    }
    
    @Test
    fun queryWithFilterByItemId() {
        mockMvc.perform(
            get("/api/v1/inventory/queries")
                .param("itemId", "1")
                .param("page", "0")
                .param("size", "20")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].itemId").value(1))
    }
    
    @Test
    fun verifyInventoryHistoryTracking() {
        val adjustment = AdjustInventoryRequest(
            inventoryId = inventoryId,
            adjustmentType = "INCREASE",
            quantity = 50,
            reason = "테스트"
        )
        
        mockMvc.perform(
            post("/api/v1/inventory/commands/adjustments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adjustment))
        )
            .andExpect(status().isCreated)
        
        val inventory = inventoryRepository.findById(inventoryId)!!
        val histories = inventory.getHistories()
        assert(histories.isNotEmpty()) { "History should be recorded" }
        assert(histories[0].transactionType == "ADJUSTMENT_INCREASE")
        assert(histories[0].changeQuantity == 50)
    }
}
