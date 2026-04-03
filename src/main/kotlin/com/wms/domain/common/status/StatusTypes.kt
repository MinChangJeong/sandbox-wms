package com.wms.domain.common.status

sealed class WarehouseType(
    val code: String,
    val displayName: String
) {
    data object General : WarehouseType("GENERAL", "일반 창고")
    data object ColdStorage : WarehouseType("COLD_STORAGE", "냉장 창고")
    data object Hazardous : WarehouseType("HAZARDOUS", "위험물 창고")
    
    companion object {
        fun fromCode(code: String): WarehouseType = when (code) {
            "GENERAL" -> General
            "COLD_STORAGE" -> ColdStorage
            "HAZARDOUS" -> Hazardous
            else -> throw IllegalArgumentException("Unknown WarehouseType: $code")
        }
        
        fun all(): List<WarehouseType> = listOf(General, ColdStorage, Hazardous)
    }
}

sealed class ZoneType(
    val code: String,
    val displayName: String
) {
    data object Ambient : ZoneType("AMBIENT", "상온")
    data object Refrigerated : ZoneType("REFRIGERATED", "냉장")
    data object Frozen : ZoneType("FROZEN", "냉동")
    
    companion object {
        fun fromCode(code: String): ZoneType = when (code) {
            "AMBIENT" -> Ambient
            "REFRIGERATED" -> Refrigerated
            "FROZEN" -> Frozen
            else -> throw IllegalArgumentException("Unknown ZoneType: $code")
        }
        
        fun all(): List<ZoneType> = listOf(Ambient, Refrigerated, Frozen)
    }
}

sealed class LocationType(
    val code: String,
    val displayName: String
) {
    data object Standard : LocationType("STANDARD", "표준 로케이션")
    data object Hazmat : LocationType("HAZMAT", "위험물 보관")
    data object ColdChain : LocationType("COLD_CHAIN", "콜드체인")
    data object HighValue : LocationType("HIGH_VALUE", "고가품 보관")
    
    companion object {
        fun fromCode(code: String): LocationType = when (code) {
            "STANDARD" -> Standard
            "HAZMAT" -> Hazmat
            "COLD_CHAIN" -> ColdChain
            "HIGH_VALUE" -> HighValue
            else -> throw IllegalArgumentException("Unknown LocationType: $code")
        }
        
        fun all(): List<LocationType> = listOf(Standard, Hazmat, ColdChain, HighValue)
    }
}

sealed class LocationStatus(
    val code: String,
    val displayName: String
) {
    abstract fun allowedTransitions(): Set<kotlin.reflect.KClass<out LocationStatus>>
    abstract fun canAcceptInventory(): Boolean
    abstract fun canLock(): Boolean
    
    fun canTransitionTo(target: LocationStatus): Boolean =
        target::class in allowedTransitions()
    
    data object Empty : LocationStatus("EMPTY", "빈 로케이션") {
        override fun allowedTransitions() = setOf(Occupied::class, Locked::class, Maintenance::class)
        override fun canAcceptInventory() = true
        override fun canLock() = true
    }
    
    data object Occupied : LocationStatus("OCCUPIED", "사용중") {
        override fun allowedTransitions() = setOf(Empty::class, Locked::class)
        override fun canAcceptInventory() = true
        override fun canLock() = true
    }
    
    data object Locked : LocationStatus("LOCKED", "잠금") {
        override fun allowedTransitions() = setOf(Empty::class, Occupied::class)
        override fun canAcceptInventory() = false
        override fun canLock() = false
    }
    
    data object Maintenance : LocationStatus("MAINTENANCE", "점검중") {
        override fun allowedTransitions() = setOf(Empty::class)
        override fun canAcceptInventory() = false
        override fun canLock() = false
    }
    
    companion object {
        fun fromCode(code: String): LocationStatus = when (code) {
            "EMPTY" -> Empty
            "OCCUPIED" -> Occupied
            "LOCKED" -> Locked
            "MAINTENANCE" -> Maintenance
            else -> throw IllegalArgumentException("Unknown LocationStatus: $code")
        }
        
        fun all(): List<LocationStatus> = listOf(Empty, Occupied, Locked, Maintenance)
    }
}

sealed class ItemUnit(
    val code: String,
    val displayName: String
) {
    data object EA : ItemUnit("EA", "개")
    data object Box : ItemUnit("BOX", "박스")
    data object Pallet : ItemUnit("PALLET", "팰릿")
    data object KG : ItemUnit("KG", "킬로그램")
    data object Liter : ItemUnit("L", "리터")
    
    companion object {
        fun fromCode(code: String): ItemUnit = when (code) {
            "EA" -> EA
            "BOX" -> Box
            "PALLET" -> Pallet
            "KG" -> KG
            "L" -> Liter
            else -> throw IllegalArgumentException("Unknown ItemUnit: $code")
        }
        
        fun all(): List<ItemUnit> = listOf(EA, Box, Pallet, KG, Liter)
    }
}

sealed class StorageType(
    val code: String,
    val displayName: String
) {
    data object Normal : StorageType("NORMAL", "상온 보관")
    data object Cold : StorageType("COLD", "냉장 보관")
    data object Frozen : StorageType("FROZEN", "냉동 보관")
    data object Hazardous : StorageType("HAZARDOUS", "위험물 보관")
    
    companion object {
        fun fromCode(code: String): StorageType = when (code) {
            "NORMAL" -> Normal
            "COLD" -> Cold
            "FROZEN" -> Frozen
            "HAZARDOUS" -> Hazardous
            else -> throw IllegalArgumentException("Unknown StorageType: $code")
        }
        
        fun all(): List<StorageType> = listOf(Normal, Cold, Frozen, Hazardous)
    }
}
