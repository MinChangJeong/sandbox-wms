package com.wms.domain.warehouse.model

import com.wms.domain.common.AggregateRoot
import com.wms.domain.common.status.WarehouseType
import com.wms.domain.warehouse.event.WarehouseCreatedEvent
import com.wms.domain.warehouse.event.WarehouseActivatedEvent
import com.wms.domain.warehouse.event.WarehouseDeactivatedEvent
import jakarta.persistence.*

@Entity
@Table(name = "warehouses")
class Warehouse private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    
    @Column(name = "warehouse_code", nullable = false, unique = true, length = 10)
    val warehouseCode: String,
    
    @Column(name = "warehouse_name", nullable = false, length = 100)
    private var _warehouseName: String,
    
    @Column(name = "address", nullable = false, length = 255)
    private var _address: String,
    
    @Convert(converter = WarehouseTypeConverter::class)
    @Column(name = "warehouse_type", nullable = false)
    private var _warehouseType: WarehouseType,
    
    @Column(name = "is_active", nullable = false)
    private var _isActive: Boolean = true,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    override val createdAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),
    
    @Column(name = "created_by", nullable = false, updatable = false)
    override var createdBy: String = "",
    
    @Column(name = "updated_at", nullable = false)
    override var updatedAt: java.time.LocalDateTime = java.time.LocalDateTime.now(),
    
    @Column(name = "updated_by", nullable = false)
    override var updatedBy: String = "",
    
    @Column(name = "is_deleted", nullable = false)
    override var isDeleted: Boolean = false
) : AggregateRoot() {
    
    val warehouseName: String get() = _warehouseName
    val address: String get() = _address
    val warehouseType: WarehouseType get() = _warehouseType
    val isActive: Boolean get() = _isActive
    
    companion object {
        fun create(
            warehouseCode: String,
            warehouseName: String,
            address: String,
            warehouseType: WarehouseType,
            createdBy: String
        ): Warehouse {
            require(warehouseCode.isNotBlank() && warehouseCode.length <= 10) { 
                "창고 코드는 필수이고 10자 이내여야 합니다"
            }
            require(warehouseName.isNotBlank() && warehouseName.length <= 100) { 
                "창고명은 필수이고 100자 이내여야 합니다"
            }
            require(address.isNotBlank() && address.length <= 255) { 
                "주소는 필수이고 255자 이내여야 합니다"
            }
            
            val warehouse = Warehouse(
                warehouseCode = warehouseCode,
                _warehouseName = warehouseName,
                _address = address,
                _warehouseType = warehouseType,
                _isActive = true,
                createdBy = createdBy,
                updatedBy = createdBy
            )
            
            warehouse.registerEvent(WarehouseCreatedEvent(warehouse.id, warehouseCode))
            return warehouse
        }
    }
    
    fun updateInfo(
        warehouseName: String,
        address: String,
        warehouseType: WarehouseType,
        updatedBy: String
    ) {
        require(warehouseName.isNotBlank() && warehouseName.length <= 100) { 
            "창고명은 필수이고 100자 이내여야 합니다"
        }
        require(address.isNotBlank() && address.length <= 255) { 
            "주소는 필수이고 255자 이내여야 합니다"
        }
        
        this._warehouseName = warehouseName
        this._address = address
        this._warehouseType = warehouseType
        this.updatedBy = updatedBy
        this.updatedAt = java.time.LocalDateTime.now()
    }
    
    fun activate(updatedBy: String) {
        require(!_isActive) { "이미 활성화된 창고입니다" }
        
        this._isActive = true
        this.updatedBy = updatedBy
        this.updatedAt = java.time.LocalDateTime.now()
        
        registerEvent(WarehouseActivatedEvent(id, warehouseCode))
    }
    
    fun deactivate(updatedBy: String) {
        require(_isActive) { "이미 비활성화된 창고입니다" }
        
        this._isActive = false
        this.updatedBy = updatedBy
        this.updatedAt = java.time.LocalDateTime.now()
        
        registerEvent(WarehouseDeactivatedEvent(id, warehouseCode))
    }
}

@Converter(autoApply = true)
class WarehouseTypeConverter : jakarta.persistence.AttributeConverter<WarehouseType, String> {
    override fun convertToDatabaseColumn(attribute: WarehouseType?): String? = attribute?.code
    override fun convertToEntityAttribute(dbData: String?): WarehouseType? = 
        dbData?.let { WarehouseType.fromCode(it) }
}
