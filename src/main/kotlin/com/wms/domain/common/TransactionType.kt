package com.wms.domain.common

sealed class TransactionType(
    val code: String,
    val displayName: String
) {
    data object Inbound : TransactionType("INBOUND", "입고")
    data object Outbound : TransactionType("OUTBOUND", "출고")
    data object AdjustmentIncrease : TransactionType("ADJUSTMENT_INCREASE", "재고 증가 조정")
    data object AdjustmentDecrease : TransactionType("ADJUSTMENT_DECREASE", "재고 감소 조정")
    data object MovementOut : TransactionType("MOVEMENT_OUT", "이동 출고")
    data object MovementIn : TransactionType("MOVEMENT_IN", "이동 입고")
    data object TransferOut : TransactionType("TRANSFER_OUT", "존 간 이동 출고")
    data object TransferIn : TransactionType("TRANSFER_IN", "존 간 이동 입고")
    data object Allocate : TransactionType("ALLOCATE", "재고 할당")
    data object Deallocate : TransactionType("DEALLOCATE", "할당 해제")
    data object CycleCount : TransactionType("CYCLE_COUNT", "재고 실사")
    data object StatusChange : TransactionType("STATUS_CHANGE", "상태 변경")
    data object ReturnInbound : TransactionType("RETURN_INBOUND", "반품 입고")
    
    companion object {
        fun fromCode(code: String): TransactionType = when (code) {
            "INBOUND" -> Inbound
            "OUTBOUND" -> Outbound
            "ADJUSTMENT_INCREASE" -> AdjustmentIncrease
            "ADJUSTMENT_DECREASE" -> AdjustmentDecrease
            "MOVEMENT_OUT" -> MovementOut
            "MOVEMENT_IN" -> MovementIn
            "TRANSFER_OUT" -> TransferOut
            "TRANSFER_IN" -> TransferIn
            "ALLOCATE" -> Allocate
            "DEALLOCATE" -> Deallocate
            "CYCLE_COUNT" -> CycleCount
            "STATUS_CHANGE" -> StatusChange
            "RETURN_INBOUND" -> ReturnInbound
            else -> throw IllegalArgumentException("Unknown TransactionType: $code")
        }
        
        fun all(): List<TransactionType> = listOf(
            Inbound, Outbound, AdjustmentIncrease, AdjustmentDecrease,
            MovementOut, MovementIn, TransferOut, TransferIn,
            Allocate, Deallocate, CycleCount, StatusChange, ReturnInbound
        )
    }
}
