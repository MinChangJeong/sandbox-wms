package com.wms.domain.item.model

import com.wms.domain.common.AggregateRoot
import com.wms.domain.common.status.ItemUnit
import com.wms.domain.common.status.StorageType
import com.wms.domain.item.event.ItemCreatedEvent
import jakarta.persistence.*

@Entity
@Table(name = "items")
class Item private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    
    @Column(name = "item_code", nullable = false, unique = true, length = 50)
    val itemCode: String,
    
    @Column(name = "item_name", nullable = false, length = 200)
    private var _itemName: String,
    
    @Column(name = "barcode", unique = true, length = 50)
    private var _barcode: String? = null,
    
    @Column(name = "category", nullable = false, length = 50)
    private var _category: String,
    
    @Convert(converter = ItemUnitConverter::class)
    @Column(name = "unit", nullable = false)
    private var _unit: ItemUnit,
    
    @Convert(converter = StorageTypeConverter::class)
    @Column(name = "storage_type", nullable = false)
    private var _storageType: StorageType,
    
    @Column(name = "expiry_managed", nullable = false)
    private var _expiryManaged: Boolean = false,
    
    @Column(name = "lot_managed", nullable = false)
    private var _lotManaged: Boolean = false,
    
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
    
    val itemName: String get() = _itemName
    val barcode: String? get() = _barcode
    val category: String get() = _category
    val unit: ItemUnit get() = _unit
    val storageType: StorageType get() = _storageType
    val expiryManaged: Boolean get() = _expiryManaged
    val lotManaged: Boolean get() = _lotManaged
    val isActive: Boolean get() = _isActive
    
    companion object {
        fun create(
            itemCode: String,
            itemName: String,
            category: String,
            unit: ItemUnit,
            storageType: StorageType,
            barcode: String? = null,
            expiryManaged: Boolean = false,
            lotManaged: Boolean = false,
            createdBy: String
        ): Item {
            require(itemCode.isNotBlank() && itemCode.length <= 50) { 
                "품목 코드는 필수이고 50자 이내여야 합니다"
            }
            require(itemName.isNotBlank() && itemName.length <= 200) { 
                "품목명은 필수이고 200자 이내여야 합니다"
            }
            require(category.isNotBlank() && category.length <= 50) { 
                "카테고리는 필수이고 50자 이내여야 합니다"
            }
            if (barcode != null) {
                require(barcode.isNotBlank() && barcode.length <= 50) { 
                    "바코드는 50자 이내여야 합니다"
                }
            }
            
            val item = Item(
                itemCode = itemCode,
                _itemName = itemName,
                _barcode = barcode,
                _category = category,
                _unit = unit,
                _storageType = storageType,
                _expiryManaged = expiryManaged,
                _lotManaged = lotManaged,
                _isActive = true,
                createdBy = createdBy,
                updatedBy = createdBy
            )
            
            item.registerEvent(ItemCreatedEvent(item.id, itemCode))
            return item
        }
    }
    
    fun updateInfo(
        itemName: String,
        category: String,
        unit: ItemUnit,
        storageType: StorageType,
        updatedBy: String
    ) {
        require(itemName.isNotBlank() && itemName.length <= 200) { 
            "품목명은 필수이고 200자 이내여야 합니다"
        }
        require(category.isNotBlank() && category.length <= 50) { 
            "카테고리는 필수이고 50자 이내여야 합니다"
        }
        
        this._itemName = itemName
        this._category = category
        this._unit = unit
        this._storageType = storageType
        this.updatedBy = updatedBy
        this.updatedAt = java.time.LocalDateTime.now()
    }
}

@Converter(autoApply = true)
class ItemUnitConverter : jakarta.persistence.AttributeConverter<ItemUnit, String> {
    override fun convertToDatabaseColumn(attribute: ItemUnit?): String? = attribute?.code
    override fun convertToEntityAttribute(dbData: String?): ItemUnit? = 
        dbData?.let { ItemUnit.fromCode(it) }
}

@Converter(autoApply = true)
class StorageTypeConverter : jakarta.persistence.AttributeConverter<StorageType, String> {
    override fun convertToDatabaseColumn(attribute: StorageType?): String? = attribute?.code
    override fun convertToEntityAttribute(dbData: String?): StorageType? = 
        dbData?.let { StorageType.fromCode(it) }
}
