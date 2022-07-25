package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import com.ntikhoa.ocrreceipt.business.domain.model.ProductSearch
import com.ntikhoa.ocrreceipt.business.domain.model.Receipt

data class ScanReceiptState(
    var isLoading: Boolean = false,
    var productsSearch: MutableList<ProductSearch>? = null,
    var prices: MutableList<String>? = null,
    var message: String? = null
)