package com.wms.application.item.dto

data class CreateItemRequest(
    val itemCode: String,
    val itemName: String,
    val barcode: String?,
    val category: String,
    val unit: String,
    val storageType: String,
    val expiryManaged: Boolean = false,
    val lotManaged: Boolean = false
)

data class UpdateItemRequest(
    val itemName: String,
    val category: String,
    val unit: String,
    val storageType: String,
    val expiryManaged: Boolean,
    val lotManaged: Boolean
)

data class ItemResponse(
    val id: Long,
    val itemCode: String,
    val itemName: String,
    val barcode: String?,
    val category: String,
    val unit: String,
    val storageType: String,
    val expiryManaged: Boolean,
    val lotManaged: Boolean,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
