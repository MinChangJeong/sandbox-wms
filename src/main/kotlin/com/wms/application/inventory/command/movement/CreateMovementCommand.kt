package com.wms.application.inventory.command.movement

data class CreateMovementCommand(
    val inventoryId: Long,
    val fromLocationId: Long,
    val toLocationId: Long,
    val quantity: Int,
    val reason: String,
    val createdBy: String
) {
    init {
        require(inventoryId > 0) { "재고 ID는 필수입니다" }
        require(fromLocationId > 0) { "출발 로케이션 ID는 필수입니다" }
        require(toLocationId > 0) { "도착 로케이션 ID는 필수입니다" }
        require(fromLocationId != toLocationId) { "출발지와 도착지가 동일할 수 없습니다" }
        require(quantity > 0) { "이동 수량은 1 이상이어야 합니다" }
        require(reason.isNotBlank()) { "이동 사유는 필수입니다" }
        require(createdBy.isNotBlank()) { "작성자는 필수입니다" }
    }
}
