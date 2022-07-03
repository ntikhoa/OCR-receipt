package com.ntikhoa.ocrreceipt.business.domain.model

data class Receipt(
    val products: MutableList<String>,
    val prices: MutableList<String>,
    val visionText: String
) {
}