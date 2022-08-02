package com.ntikhoa.ocrreceipt.presentation.transaction.transactionlist

import com.ntikhoa.ocrreceipt.business.domain.model.Transaction

data class TransactionState(
    var loading: Boolean = false,
    var transactions: List<Transaction>? = null,
    var isExhausted: Boolean = false,
    var page: Int = 1,
    var perPage: Int = 2,
    var message: String? = null,
)