package com.ntikhoa.ocrreceipt.presentation.transaction.transactionlist


sealed class TransactionEvent {
    object GetListTransaction : TransactionEvent()
    object GetListTransactionNextPage : TransactionEvent()
}