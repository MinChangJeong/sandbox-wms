package com.wms.application.warehouse.dto

data class CreateWarehouseRequest(
    val warehouseCode: String,
    val warehouseName: String,
    val address: String,
    val warehouseType: String
)

data class UpdateWarehouseRequest(
    val warehouseName: String,
    val address: String,
    val warehouseType: String
)

data class WarehouseResponse(
    val id: Long,
    val warehouseCode: String,
    val warehouseName: String,
    val address: String,
    val warehouseType: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
