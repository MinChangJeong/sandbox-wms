package com.wms.domain.zone.model

import com.wms.domain.common.AggregateRoot
import com.wms.domain.common.status.ZoneType
import com.wms.domain.zone.event.ZoneCreatedEvent
import jakarta.persistence.*

@Entity
@Table(name = "zones")
class Zone private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    
    @Column(name = "warehouse_id", nullable = false)
    val warehouseId: Long,
    
    @Column(name = "zone_code", nullable = false, length = 20)
    val zoneCode: String,
    
    @Column(name = "zone_name", nullable = false, length = 100)
    private var _zoneName: String,
    
    @Convert(converter = ZoneTypeConverter::class)
    @Column(name = "zone_type", nullable = false)
    private var _zoneType: ZoneType,
    
    @Column(name = "temperature_min")
    private var _temperatureMin: Int? = null,
    
    @Column(name = "temperature_max")
    private var _temperatureMax: Int? = null,
    
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
    
    val zoneName: String get() = _zoneName
    val zoneType: ZoneType get() = _zoneType
    val temperatureMin: Int? get() = _temperatureMin
    val temperatureMax: Int? get() = _temperatureMax
    val isActive: Boolean get() = _isActive
    
    companion object {
        fun create(
            warehouseId: Long,
            zoneCode: String,
            zoneName: String,
            zoneType: ZoneType,
            temperatureMin: Int? = null,
            temperatureMax: Int? = null,
            createdBy: String
        ): Zone {
            require(warehouseId > 0) { "창고 ID는 양수여야 합니다" }
            require(zoneCode.isNotBlank() && zoneCode.length <= 20) { 
                "구역 코드는 필수이고 20자 이내여야 합니다"
            }
            require(zoneName.isNotBlank() && zoneName.length <= 100) { 
                "구역명은 필수이고 100자 이내여야 합니다"
            }
            
            if (zoneType in listOf(ZoneType.Refrigerated, ZoneType.Frozen)) {
                require(temperatureMin != null && temperatureMax != null) { 
                    "냉장/냉동 구역은 온도 범위가 필수입니다"
                }
                require(temperatureMin <= temperatureMax) { 
                    "최소 온도가 최대 온도보다 작거나 같아야 합니다"
                }
            }
            
            val zone = Zone(
                warehouseId = warehouseId,
                zoneCode = zoneCode,
                _zoneName = zoneName,
                _zoneType = zoneType,
                _temperatureMin = temperatureMin,
                _temperatureMax = temperatureMax,
                _isActive = true,
                createdBy = createdBy,
                updatedBy = createdBy
            )
            
            zone.registerEvent(ZoneCreatedEvent(zone.id, zoneCode, warehouseId))
            return zone
        }
    }
    
    fun updateInfo(
        zoneName: String,
        zoneType: ZoneType,
        temperatureMin: Int?,
        temperatureMax: Int?,
        updatedBy: String
    ) {
        require(zoneName.isNotBlank() && zoneName.length <= 100) { 
            "구역명은 필수이고 100자 이내여야 합니다"
        }
        
        if (zoneType in listOf(ZoneType.Refrigerated, ZoneType.Frozen)) {
            require(temperatureMin != null && temperatureMax != null) { 
                "냉장/냉동 구역은 온도 범위가 필수입니다"
            }
            require(temperatureMin <= temperatureMax) { 
                "최소 온도가 최대 온도보다 작거나 같아야 합니다"
            }
        }
        
        this._zoneName = zoneName
        this._zoneType = zoneType
        this._temperatureMin = temperatureMin
        this._temperatureMax = temperatureMax
        this.updatedBy = updatedBy
        this.updatedAt = java.time.LocalDateTime.now()
    }
}

@Converter(autoApply = true)
class ZoneTypeConverter : jakarta.persistence.AttributeConverter<ZoneType, String> {
    override fun convertToDatabaseColumn(attribute: ZoneType?): String? = attribute?.code
    override fun convertToEntityAttribute(dbData: String?): ZoneType? = 
        dbData?.let { ZoneType.fromCode(it) }
}
