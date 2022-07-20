package com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ExchangeVoucherResponse(
    @SerializedName("vouchers")
    @Expose
    val vouchers: List<VoucherResponse>?
)