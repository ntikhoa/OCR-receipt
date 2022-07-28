package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.exchangedone

data class ExchangeVoucherState(
    var isLoading: Boolean = false,
    var data: String? = null,
    var message: String? = null
)