package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import com.ntikhoa.ocrreceipt.business.domain.model.Receipt

data class ScanReceiptState(
    var isLoading: Boolean = false,
    var receipt: Receipt? = null,
    var message: String? = null
)