package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import com.ntikhoa.ocrreceipt.business.domain.model.Receipt

data class ExchangeVoucherState(
    var isLoading: Boolean = false,
    var receipt: Receipt? = null,
    var message: String? = null
)