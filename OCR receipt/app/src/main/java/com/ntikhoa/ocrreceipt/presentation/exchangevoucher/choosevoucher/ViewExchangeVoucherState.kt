package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.choosevoucher

import com.ntikhoa.ocrreceipt.business.domain.model.Receipt
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher

data class ViewExchangeVoucherState(
    var isLoading: Boolean = false,
    var voucher: List<Voucher>? = null,
    var message: String? = null
)