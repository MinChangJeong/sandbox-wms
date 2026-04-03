package com.wms.domain.inventory.model

import com.wms.domain.common.AggregateRoot
import com.wms.domain.common.status.InventoryStatus
import com.wms.domain.inventory.event.InventoryAllocatedEvent
import com.wms.domain.inventory.event.InventoryDeallocatedEvent
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "inventories",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["item_id", "location_id"], name = "uk_item_location")
    ],
    indexes = [
        Index(name = "idx_item_id", columnList = "item_id"),
        Index(name = "idx_location_id", columnList = "location_id"),
        Index(name = "idx_status", columnList = "status"),
        Index(name = "idx_allocated_qty", columnList = "allocated_qty"),
        Index(name = "idx_quantity", columnList = "quantity")
    ]
)
class Inventory private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    
    @Column(name = "item_id", nullable = false)
    val itemId: Long,
    
    @Column(name = "location_id", nullable = false)
    val locationId: Long,
    
    @Convert(converter = InventoryStatusConverter::class)
    @Column(name = "status", nullable = false, length = 50)
    private var _status: InventoryStatus = InventoryStatus.Available,
    
    @Column(name = "quantity", nullable = false)
    private var _quantity: Int = 0,
    
    @Column(name = "allocated_qty", nullable = false)
    private var _allocatedQty: Int = 0,
    
    @Transient
    private var _histories: MutableList<InventoryHistory> = mutableListOf(),
    
    @Column(name = "created_at", nullable = false, updatable = false)
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "created_by", nullable = false, updatable = false)
    override var createdBy: String = "",
    
    @Column(name = "updated_at", nullable = false)
    override var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_by", nullable = false)
    override var updatedBy: String = "",
    
    @Column(name = "is_deleted", nullable = false)
    override var isDeleted: Boolean = false
) : AggregateRoot() {
    
    // 읽기 전용 프로퍼티
    val status: InventoryStatus get() = _status
    val quantity: Int get() = _quantity
    val allocatedQty: Int get() = _allocatedQty
    val availableQty: Int get() = _quantity - _allocatedQty
    
    companion object {
        fun create(
            itemId: Long,
            locationId: Long,
            quantity: Int,
            createdBy: String
        ): Inventory {
            require(itemId > 0) { "품목 ID는 필수입니다" }
            require(locationId > 0) { "로케이션 ID는 필수입니다" }
            require(quantity >= 0) { "수량은 0 이상이어야 합니다" }
            
            return Inventory(
                itemId = itemId,
                locationId = locationId,
                _quantity = quantity,
                _allocatedQty = 0,
                _status = InventoryStatus.Available,
                createdBy = createdBy,
                updatedBy = createdBy
            )
        }
    }
    
    /**
     * 재고 증가 (입고 적치)
     */
    fun increase(
        quantity: Int,
        reason: String,
        referenceType: String? = null,
        referenceId: Long? = null,
        updatedBy: String
    ) {
        require(quantity > 0) { "증가 수량은 1 이상이어야 합니다" }
        require(_status.canAdjust()) { "조정 불가 상태: ${_status.displayName}" }
        
        val beforeQty = _quantity
        _quantity += quantity
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "INBOUND",
            changeQuantity = quantity,
            beforeQuantity = beforeQty,
            reason = reason,
            referenceType = referenceType,
            referenceId = referenceId,
            createdBy = updatedBy
        )
    }
    
    /**
     * 재고 감소 (출고 확정)
     */
    fun decrease(
        quantity: Int,
        reason: String,
        referenceType: String? = null,
        referenceId: Long? = null,
        updatedBy: String
    ) {
        require(quantity > 0) { "감소 수량은 1 이상이어야 합니다" }
        require(availableQty >= quantity) { 
            "가용재고 부족: 요청=$quantity, 가용=${availableQty}"
        }
        require(_status.canAdjust()) { "조정 불가 상태: ${_status.displayName}" }
        
        val beforeQty = _quantity
        _quantity -= quantity
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "OUTBOUND",
            changeQuantity = -quantity,
            beforeQuantity = beforeQty,
            reason = reason,
            referenceType = referenceType,
            referenceId = referenceId,
            createdBy = updatedBy
        )
    }
    
    /**
     * 재고 조정 (증가/감소)
     */
    fun adjust(
        adjustmentType: String, // INCREASE, DECREASE, DAMAGE, LOSS, FOUND
        quantity: Int,
        reason: String,
        updatedBy: String
    ) {
        require(quantity > 0) { "조정 수량은 1 이상이어야 합니다" }
        require(_status.canAdjust()) { "조정 불가 상태: ${_status.displayName}" }
        
        val beforeQty = _quantity
        val changeQty = when (adjustmentType) {
            "INCREASE", "FOUND" -> quantity
            "DECREASE", "DAMAGE", "LOSS" -> -quantity
            else -> throw IllegalArgumentException("알 수 없는 조정 유형: $adjustmentType")
        }
        
        require(_quantity + changeQty >= 0) { "조정 후 수량이 음수가 될 수 없습니다" }
        
        _quantity += changeQty
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "ADJUSTMENT_$adjustmentType",
            changeQuantity = changeQty,
            beforeQuantity = beforeQty,
            reason = reason,
            createdBy = updatedBy
        )
    }
    
    /**
     * 재고 할당 (출고 오더 할당)
     */
    fun allocate(
        quantity: Int,
        orderId: Long,
        updatedBy: String
    ) {
        require(quantity > 0) { "할당 수량은 1 이상이어야 합니다" }
        require(availableQty >= quantity) { 
            "가용재고 부족: 요청=$quantity, 가용=${availableQty}"
        }
        require(_status.canAllocate()) { "할당 불가 상태: ${_status.displayName}" }
        
        val beforeAllocatedQty = _allocatedQty
        _allocatedQty += quantity
        
        // 상태 전이: 전량 할당되면 FullyAllocated
        if (availableQty == 0) {
            _status = InventoryStatus.FullyAllocated
        } else if (availableQty < _quantity) {
            _status = InventoryStatus.Allocated
        }
        
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "ALLOCATE",
            changeQuantity = quantity,
            beforeQuantity = beforeAllocatedQty,
            reason = "출고 할당",
            referenceType = "OUTBOUND_ORDER",
            referenceId = orderId,
            createdBy = updatedBy
        )
        
        registerEvent(InventoryAllocatedEvent(
            inventoryId = this.id,
            allocatedQty = quantity,
            orderId = orderId,
            aggregateId = this.id
        ))
    }
    
    /**
     * 재고 할당 해제
     */
    fun deallocate(
        quantity: Int,
        reason: String,
        updatedBy: String
    ) {
        require(quantity > 0) { "할당 해제 수량은 1 이상이어야 합니다" }
        require(_allocatedQty >= quantity) { 
            "할당된 수량 부족: 요청=$quantity, 할당=${_allocatedQty}"
        }
        
        val beforeAllocatedQty = _allocatedQty
        _allocatedQty -= quantity
        
        // 상태 복원
        if (_allocatedQty == 0) {
            _status = InventoryStatus.Available
        } else if (_status == InventoryStatus.FullyAllocated) {
            _status = InventoryStatus.Allocated
        }
        
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "DEALLOCATE",
            changeQuantity = -quantity,
            beforeQuantity = beforeAllocatedQty,
            reason = reason,
            createdBy = updatedBy
        )
        
        registerEvent(InventoryDeallocatedEvent(
            inventoryId = this.id,
            deallocatedQty = quantity,
            reason = reason,
            aggregateId = this.id
        ))
    }
    
    /**
     * 재고 이동 (출발지)
     */
    fun moveOut(
        quantity: Int,
        toLocationId: Long,
        updatedBy: String
    ) {
        require(quantity > 0) { "이동 수량은 1 이상이어야 합니다" }
        require(availableQty >= quantity) { 
            "가용재고 부족: 요청=$quantity, 가용=${availableQty}"
        }
        
        val beforeQty = _quantity
        _quantity -= quantity
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "MOVEMENT_OUT",
            changeQuantity = -quantity,
            beforeQuantity = beforeQty,
            reason = "로케이션 이동: $locationId → $toLocationId",
            referenceType = "MOVEMENT",
            referenceId = toLocationId,
            createdBy = updatedBy
        )
    }
    
    /**
     * 재고 이동 (도착지)
     */
    fun moveIn(
        quantity: Int,
        fromLocationId: Long,
        updatedBy: String
    ) {
        require(quantity > 0) { "이동 수량은 1 이상이어야 합니다" }
        
        val beforeQty = _quantity
        _quantity += quantity
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "MOVEMENT_IN",
            changeQuantity = quantity,
            beforeQuantity = beforeQty,
            reason = "로케이션 이동: $fromLocationId → $locationId",
            referenceType = "MOVEMENT",
            referenceId = fromLocationId,
            createdBy = updatedBy
        )
    }
    
    /**
     * 상태 변경
     */
    fun transitionTo(
        newStatus: InventoryStatus,
        reason: String,
        updatedBy: String
    ) {
        require(_status.canTransitionTo(newStatus)) {
            "상태 전이 불가: ${_status.displayName} → ${newStatus.displayName}"
        }
        
        val previousStatus = _status
        _status = newStatus
        updatedAt = LocalDateTime.now()
        this.updatedBy = updatedBy
        
        recordHistory(
            transactionType = "STATUS_CHANGE",
            changeQuantity = 0,
            beforeQuantity = _quantity,
            reason = "[${previousStatus.displayName} → ${newStatus.displayName}] $reason",
            createdBy = updatedBy
        )
    }
    
    /**
     * 이력 기록 (내부 사용)
     */
    private fun recordHistory(
        transactionType: String,
        changeQuantity: Int,
        beforeQuantity: Int,
        reason: String? = null,
        referenceType: String? = null,
        referenceId: Long? = null,
        createdBy: String
    ) {
        val history = InventoryHistory.create(
            inventoryId = this.id,
            transactionType = transactionType,
            changeQuantity = changeQuantity,
            beforeQuantity = beforeQuantity,
            afterQuantity = beforeQuantity + changeQuantity,
            referenceType = referenceType,
            referenceId = referenceId,
            reason = reason,
            createdBy = createdBy
        )
        _histories.add(history)
    }
    
    fun getHistories(): List<InventoryHistory> = _histories.toList()
}

@Converter(autoApply = true)
class InventoryStatusConverter : jakarta.persistence.AttributeConverter<InventoryStatus, String> {
    override fun convertToDatabaseColumn(attribute: InventoryStatus?): String? = attribute?.code
    override fun convertToEntityAttribute(dbData: String?): InventoryStatus? =
        dbData?.let { InventoryStatus.fromCode(it) }
}
