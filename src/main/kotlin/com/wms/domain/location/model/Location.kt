package com.wms.domain.location.model

import com.wms.domain.common.AggregateRoot
import com.wms.domain.common.status.LocationStatus
import com.wms.domain.common.status.LocationType
import com.wms.domain.location.event.LocationCreatedEvent
import jakarta.persistence.*

@Entity
@Table(name = "locations")
class Location private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    
    @Column(name = "zone_id", nullable = false)
    val zoneId: Long,
    
    @Column(name = "location_code", nullable = false, unique = true, length = 50)
    val locationCode: String,
    
    @Column(name = "row_num", nullable = false)
    val rowNum: Int,
    
    @Column(name = "column_num", nullable = false)
    val columnNum: Int,
    
    @Column(name = "level", nullable = false)
    val level: Int,
    
    @Convert(converter = LocationTypeConverter::class)
    @Column(name = "location_type", nullable = false)
    val locationType: LocationType,
    
    @Column(name = "max_weight")
    private var _maxWeight: Double? = null,
    
    @Column(name = "max_volume")
    private var _maxVolume: Double? = null,
    
    @Convert(converter = LocationStatusConverter::class)
    @Column(name = "status", nullable = false)
    private var _status: LocationStatus = LocationStatus.Empty,
    
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
    
    val maxWeight: Double? get() = _maxWeight
    val maxVolume: Double? get() = _maxVolume
    val status: LocationStatus get() = _status
    val isActive: Boolean get() = _isActive
    
    companion object {
        fun create(
            zoneId: Long,
            locationCode: String,
            rowNum: Int,
            columnNum: Int,
            level: Int,
            locationType: LocationType,
            maxWeight: Double? = null,
            maxVolume: Double? = null,
            createdBy: String
        ): Location {
            require(zoneId > 0) { "구역 ID는 양수여야 합니다" }
            require(locationCode.isNotBlank() && locationCode.length <= 50) { 
                "로케이션 코드는 필수이고 50자 이내여야 합니다"
            }
            require(rowNum > 0 && columnNum > 0 && level > 0) { 
                "행, 열, 높이는 모두 양수여야 합니다"
            }
            
            val location = Location(
                zoneId = zoneId,
                locationCode = locationCode,
                rowNum = rowNum,
                columnNum = columnNum,
                level = level,
                locationType = locationType,
                _maxWeight = maxWeight,
                _maxVolume = maxVolume,
                _status = LocationStatus.Empty,
                _isActive = true,
                createdBy = createdBy,
                updatedBy = createdBy
            )
            
            location.registerEvent(LocationCreatedEvent(location.id, locationCode, zoneId))
            return location
        }
    }
    
    fun updateCapacity(
        maxWeight: Double?,
        maxVolume: Double?,
        updatedBy: String
    ) {
        this._maxWeight = maxWeight
        this._maxVolume = maxVolume
        this.updatedBy = updatedBy
        this.updatedAt = java.time.LocalDateTime.now()
    }
    
    fun transitionTo(newStatus: LocationStatus, updatedBy: String) {
        require(_status.canTransitionTo(newStatus)) { 
            "상태 전이 불가: ${_status.displayName} → ${newStatus.displayName}"
        }
        
        this._status = newStatus
        this.updatedBy = updatedBy
        this.updatedAt = java.time.LocalDateTime.now()
    }
}

@Converter(autoApply = true)
class LocationTypeConverter : jakarta.persistence.AttributeConverter<LocationType, String> {
    override fun convertToDatabaseColumn(attribute: LocationType?): String? = attribute?.code
    override fun convertToEntityAttribute(dbData: String?): LocationType? = 
        dbData?.let { LocationType.fromCode(it) }
}

@Converter(autoApply = true)
class LocationStatusConverter : jakarta.persistence.AttributeConverter<LocationStatus, String> {
    override fun convertToDatabaseColumn(attribute: LocationStatus?): String? = attribute?.code
    override fun convertToEntityAttribute(dbData: String?): LocationStatus? = 
        dbData?.let { LocationStatus.fromCode(it) }
}
