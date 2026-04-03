package com.wms.application.inbound

import com.wms.domain.inbound.model.InboundOrder
import com.wms.domain.inbound.model.InboundOrderItem
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import java.time.LocalDateTime

class InboundOrderDomainTest {
    
    private val TEST_USER = "test-user"
    private val SUPPLIER_ID = 1L
    private val WAREHOUSE_ID = 1L
    private val ITEM_ID_1 = 1L
    
    @Test
    fun createInboundOrderSuccessfully() {
        val items = listOf(
            InboundOrderItem.create(ITEM_ID_1, 100, TEST_USER)
        )
        
        val order = InboundOrder.create(
            supplierId = SUPPLIER_ID,
            warehouseId = WAREHOUSE_ID,
            expectedDate = LocalDateTime.now().plusDays(1),
            items = items,
            createdBy = TEST_USER
        )
        
        assertNotNull(order)
        assertEquals(SUPPLIER_ID, order.supplierId)
        assertEquals(WAREHOUSE_ID, order.warehouseId)
        assertEquals("EXPECTED", order.status.code)
        assertEquals(1, order.getItemCount())
    }
    
    @Test
    fun startInspectionTransitionsStatus() {
        val order = createTestOrder()
        order.startInspection(TEST_USER)
        assertEquals("INSPECTING", order.status.code)
    }
    
    @Test
    fun completeInspectionTransitionsStatus() {
        val order = createTestOrder()
        order.startInspection(TEST_USER)
        
        val items = order.getItems()
        items[0].recordInspection(100, 100, 0, null, TEST_USER)
        
        order.completeInspection(TEST_USER)
        assertEquals("INSPECTED", order.status.code)
        assertNotNull(order.inspectionCompletedAt)
    }
    
    @Test
    fun startPutawayAfterInspection() {
        val order = createTestOrder()
        order.startInspection(TEST_USER)
        
        order.getItems()[0].recordInspection(100, 100, 0, null, TEST_USER)
        order.completeInspection(TEST_USER)
        
        order.startPutaway(TEST_USER)
        assertEquals("PUTAWAY_IN_PROGRESS", order.status.code)
        assertNotNull(order.putawayStartedAt)
    }
    
    @Test
    fun completeInboundOrder() {
        val order = createTestOrder()
        order.startInspection(TEST_USER)
        
        order.getItems()[0].recordInspection(100, 100, 0, null, TEST_USER)
        order.completeInspection(TEST_USER)
        
        order.startPutaway(TEST_USER)
        order.complete(TEST_USER)
        
        assertEquals("COMPLETED", order.status.code)
        assertNotNull(order.completedAt)
    }
    
    @Test
    fun rejectInboundOrder() {
        val order = createTestOrder()
        order.reject("공급사 연락 불가", TEST_USER)
        assertEquals("REJECTED", order.status.code)
    }
    
    @Test
    fun fullInboundOrderLifecycle() {
        val order = createTestOrder()
        
        assertEquals("EXPECTED", order.status.code)
        
        order.startInspection(TEST_USER)
        assertEquals("INSPECTING", order.status.code)
        
        order.getItems()[0].recordInspection(100, 100, 0, null, TEST_USER)
        
        order.completeInspection(TEST_USER)
        assertEquals("INSPECTED", order.status.code)
        
        order.startPutaway(TEST_USER)
        assertEquals("PUTAWAY_IN_PROGRESS", order.status.code)
        
        order.complete(TEST_USER)
        assertEquals("COMPLETED", order.status.code)
        
        assertNotNull(order.inspectionCompletedAt)
        assertNotNull(order.putawayStartedAt)
        assertNotNull(order.completedAt)
    }
    
    @Test
    fun addItem() {
        val order = createTestOrder()
        assertEquals(1, order.getItemCount())
        
        val newItem = InboundOrderItem.create(999L, 100, TEST_USER)
        order.addItem(newItem, TEST_USER)
        
        assertEquals(2, order.getItemCount())
    }
    
    @Test
    fun removeItem() {
        val items = listOf(
            InboundOrderItem.create(ITEM_ID_1, 100, TEST_USER),
            InboundOrderItem.create(2L, 50, TEST_USER)
        )
        
        val order = InboundOrder.create(
            supplierId = SUPPLIER_ID,
            warehouseId = WAREHOUSE_ID,
            expectedDate = LocalDateTime.now().plusDays(1),
            items = items,
            createdBy = TEST_USER
        )
        
        assertEquals(2, order.getItemCount())
        
        order.removeItem(2L, TEST_USER)
        assertEquals(1, order.getItemCount())
    }
    
    private fun createTestOrder(): InboundOrder {
        return InboundOrder.create(
            supplierId = SUPPLIER_ID,
            warehouseId = WAREHOUSE_ID,
            expectedDate = LocalDateTime.now().plusDays(1),
            items = listOf(InboundOrderItem.create(ITEM_ID_1, 100, TEST_USER)),
            createdBy = TEST_USER
        )
    }
}
