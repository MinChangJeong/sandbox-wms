package com.wms.application.zone.dto

data class CreateZoneRequest(
    val warehouseId: Long,
    val zoneCode: String,
    val zoneName: String,
    val zoneType: String,
    val temperatureMin: Int?,
    val temperatureMax: Int?
)

data class UpdateZoneRequest(
    val zoneName: String,
    val zoneType: String,
    val temperatureMin: Int?,
    val temperatureMax: Int?
)

data class ZoneResponse(
    val id: Long,
    val warehouseId: Long,
    val zoneCode: String,
    val zoneName: String,
    val zoneType: String,
    val temperatureMin: Int?,
    val temperatureMax: Int?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
