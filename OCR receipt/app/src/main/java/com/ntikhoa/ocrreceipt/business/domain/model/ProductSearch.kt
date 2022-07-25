package com.ntikhoa.ocrreceipt.business.domain.model

data class ProductSearch(
    var productName: String = "",
    val suggestion: List<String> = listOf(),
)