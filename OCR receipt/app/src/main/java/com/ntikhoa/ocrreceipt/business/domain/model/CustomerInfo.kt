package com.ntikhoa.ocrreceipt.business.domain.model

data class CustomerInfo(
    var customerName: String = "",
    var customerPhone: String = "",
    var transactionID: String = "",
) {
    fun isNotEmpty(): Boolean {
        return customerName.isNotBlank() && customerPhone.isNotBlank() && transactionID.isNotBlank()
    }
}