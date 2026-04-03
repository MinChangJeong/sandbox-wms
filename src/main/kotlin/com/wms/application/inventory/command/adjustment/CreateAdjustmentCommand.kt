package com.wms.application.inventory.command.adjustment

data class CreateAdjustmentCommand(
    val inventoryId: Long,
    val adjustmentType: String,  // INCREASE, DECREASE, DAMAGE, LOSS, FOUND
    val quantity: Int,
    val reason: String,
    val createdBy: String
) {
    init {
        require(inventoryId > 0) { "재고 ID는 필수입니다" }
        require(adjustmentType.isNotBlank()) { "조정 유형은 필수입니다" }
        require(quantity > 0) { "조정 수량은 1 이상이어야 합니다" }
        require(reason.isNotBlank()) { "조정 사유는 필수입니다" }
        require(createdBy.isNotBlank()) { "작성자는 필수입니다" }
        require(
            adjustmentType in setOf("INCREASE", "DECREASE", "DAMAGE", "LOSS", "FOUND")
        ) { "알 수 없는 조정 유형: $adjustmentType" }
    }
}
