package com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ExchangeProductsResponse(
    @Expose
    @SerializedName("products_names")
    val productsNames: List<String>
)