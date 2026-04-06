package com.wms.domain.inventory.repository

import com.wms.domain.inventory.model.InventoryHistory

/**
 * 재고 이력 저장소 (Port Interface)
 * 
 * 모든 재고 변동 사항을 영구적으로 기록합니다.
 * - INBOUND: 입고 적치
 * - OUTBOUND: 출고 확정
 * - ADJUSTMENT_*: 재고 조정 (증가, 감소, 손상, 손실, 발견)
 * - ALLOCATE: 출고 오더 할당
 * - DEALLOCATE: 할당 해제
 * - MOVEMENT_OUT/IN: 로케이션 이동
 * - STATUS_CHANGE: 상태 변경
 */
interface InventoryHistoryRepository {
    
    /**
     * 재고 이력 저장
     */
    fun save(history: InventoryHistory): InventoryHistory
    
    /**
     * 여러 이력 일괄 저장
     */
    fun saveAll(histories: List<InventoryHistory>): List<InventoryHistory>
    
    /**
     * 재고 ID로 모든 이력 조회
     * 최신 순서로 정렬됨
     */
    fun findByInventoryId(inventoryId: Long): List<InventoryHistory>
    
    /**
     * 재고 ID와 거래 유형으로 이력 조회
     */
    fun findByInventoryIdAndTransactionType(
        inventoryId: Long,
        transactionType: String
    ): List<InventoryHistory>
    
    /**
     * 참고 유형별 이력 조회
     * 예: OUTBOUND_ORDER, MOVEMENT
     */
    fun findByInventoryIdAndReferenceType(
        inventoryId: Long,
        referenceType: String
    ): List<InventoryHistory>
    
    /**
     * 재고 ID로 ID 조회
     */
    fun findById(id: Long): InventoryHistory?
}
