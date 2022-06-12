package com.ntikhoa.ocrreceipt.presentation.extractreceipt

data class ExtractReceiptState(
    var isLoading: Boolean = false,
    var text: String? = null,
    var message: String? = null
) {
}