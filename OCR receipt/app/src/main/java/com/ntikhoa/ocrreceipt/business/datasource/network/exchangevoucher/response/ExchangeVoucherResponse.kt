package com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response.VoucherResponse

data class ExchangeVoucherResponse(
    @SerializedName("vouchers")
    @Expose
    val vouchers: List<VoucherResponse>?
)