package com.ntikhoa.ocrreceipt.business.domain.model

data class Receipt(
    var products: MutableList<String>,
    var prices: MutableList<String>,
    val visionText: String
)