package com.wms.domain.warehouse

import com.wms.domain.common.status.WarehouseType
import com.wms.domain.warehouse.model.Warehouse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*

@DisplayName("Warehouse Domain Tests")
class WarehouseTest {
    
    @DisplayName("창고 생성 시 초기 상태 검증")
    @Test
    fun `창고 생성 시 초기 상태 검증`() {
        val warehouse = Warehouse.create(
            warehouseCode = "WH001",
            warehouseName = "서울 창고",
            address = "서울시 강남구",
            warehouseType = WarehouseType.General,
            createdBy = "admin"
        )
        
        assertEquals("WH001", warehouse.warehouseCode)
        assertEquals("서울 창고", warehouse.warehouseName)
        assertEquals(WarehouseType.General, warehouse.warehouseType)
        assertTrue(warehouse.isActive)
    }
    
    @DisplayName("빈 창고 코드로 생성 시 예외 발생")
    @Test
    fun `빈 창고 코드로 생성 시 예외 발생`() {
        assertThrows<IllegalArgumentException> {
            Warehouse.create(
                warehouseCode = "",
                warehouseName = "서울 창고",
                address = "서울시 강남구",
                warehouseType = WarehouseType.General,
                createdBy = "admin"
            )
        }
    }
    
    @DisplayName("창고 정보 수정")
    @Test
    fun `창고 정보 수정`() {
        val warehouse = Warehouse.create(
            warehouseCode = "WH001",
            warehouseName = "서울 창고",
            address = "서울시 강남구",
            warehouseType = WarehouseType.General,
            createdBy = "admin"
        )
        
        warehouse.updateInfo(
            warehouseName = "서울 부산 창고",
            address = "부산시 중구",
            warehouseType = WarehouseType.ColdStorage,
            updatedBy = "admin"
        )
        
        assertEquals("서울 부산 창고", warehouse.warehouseName)
        assertEquals("부산시 중구", warehouse.address)
        assertEquals(WarehouseType.ColdStorage, warehouse.warehouseType)
    }
    
    @DisplayName("창고 활성화")
    @Test
    fun `창고 활성화`() {
        val warehouse = Warehouse.create(
            warehouseCode = "WH001",
            warehouseName = "서울 창고",
            address = "서울시 강남구",
            warehouseType = WarehouseType.General,
            createdBy = "admin"
        )
        
        warehouse.deactivate("admin")
        assertFalse(warehouse.isActive)
        
        warehouse.activate("admin")
        assertTrue(warehouse.isActive)
    }
    
    @DisplayName("비활성화된 창고 활성화 시 예외 발생")
    @Test
    fun `비활성화된 창고 활성화 시 예외 발생`() {
        val warehouse = Warehouse.create(
            warehouseCode = "WH001",
            warehouseName = "서울 창고",
            address = "서울시 강남구",
            warehouseType = WarehouseType.General,
            createdBy = "admin"
        )
        
        assertThrows<IllegalArgumentException> {
            warehouse.activate("admin")
        }
    }
    
    @DisplayName("활성 창고 비활성화")
    @Test
    fun `활성 창고 비활성화`() {
        val warehouse = Warehouse.create(
            warehouseCode = "WH001",
            warehouseName = "서울 창고",
            address = "서울시 강남구",
            warehouseType = WarehouseType.General,
            createdBy = "admin"
        )
        
        warehouse.deactivate("admin")
        assertFalse(warehouse.isActive)
    }
    
    @DisplayName("도메인 이벤트 생성 검증")
    @Test
    fun `도메인 이벤트 생성 검증`() {
        val warehouse = Warehouse.create(
            warehouseCode = "WH001",
            warehouseName = "서울 창고",
            address = "서울시 강남구",
            warehouseType = WarehouseType.General,
            createdBy = "admin"
        )
        
        assertEquals(1, warehouse.domainEvents.size)
        
        warehouse.deactivate("admin")
        assertEquals(2, warehouse.domainEvents.size)
    }
}
