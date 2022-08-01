package com.ntikhoa.ocrreceipt.presentation.transaction


sealed class TransactionEvent {
    object GetListTransaction : TransactionEvent()
    object GetListTransactionNextPage : TransactionEvent()
}