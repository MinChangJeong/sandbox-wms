package com.wms.application.location.dto

data class CreateLocationRequest(
    val zoneId: Long,
    val locationCode: String,
    val rowNum: Int,
    val columnNum: Int,
    val level: Int,
    val locationType: String,
    val maxWeight: Double?,
    val maxVolume: Double?
)

data class UpdateLocationRequest(
    val maxWeight: Double?,
    val maxVolume: Double?
)

data class LocationResponse(
    val id: Long,
    val zoneId: Long,
    val locationCode: String,
    val rowNum: Int,
    val columnNum: Int,
    val level: Int,
    val locationType: String,
    val maxWeight: Double?,
    val maxVolume: Double?,
    val status: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
