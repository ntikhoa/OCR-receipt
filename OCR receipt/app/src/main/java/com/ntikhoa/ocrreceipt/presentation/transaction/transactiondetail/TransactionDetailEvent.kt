package com.ntikhoa.ocrreceipt.presentation.transaction.transactiondetail

sealed class TransactionDetailEvent {
    data class GetTransactionDetail(val id: Int) : TransactionDetailEvent()
}