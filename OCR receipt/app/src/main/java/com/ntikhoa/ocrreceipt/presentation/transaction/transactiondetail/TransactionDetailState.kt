package com.ntikhoa.ocrreceipt.presentation.transaction.transactiondetail

import com.ntikhoa.ocrreceipt.business.domain.model.TransactionDetail

data class TransactionDetailState(
    var isLoading: Boolean = false,
    var transactionDetail: TransactionDetail? = null,
    var message: String? = null
)